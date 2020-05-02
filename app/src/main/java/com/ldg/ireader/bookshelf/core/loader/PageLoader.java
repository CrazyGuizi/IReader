package com.ldg.ireader.bookshelf.core.loader;

import android.text.TextPaint;
import android.text.TextUtils;

import com.ldg.common.util.ThreadUtils;
import com.ldg.common.util.ToastUtils;
import com.ldg.ireader.App;
import com.ldg.ireader.bookshelf.core.config.PageConfig;
import com.ldg.ireader.bookshelf.model.BookModel;
import com.ldg.ireader.bookshelf.model.ChapterModel;
import com.ldg.ireader.bookshelf.model.TxtPage;
import com.ldg.ireader.db.DbHelp;
import com.ldg.ireader.db.entity.DbBookRecord;
import com.ldg.ireader.subscribe.BookLoaderObservable;
import com.ldg.ireader.utils.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class PageLoader {

    // 提前几页加载
    private static final int PRE_LOAD_INDEX = 5;
    protected BookModel mBookModel;

    protected TxtPage mCurPage;
    protected List<TxtPage> mCurPageList;

    // 目录
    protected List<ChapterModel> mCatalogue = new ArrayList<>();
    protected BookCache<String, ChapterModel> mChapterCache = new BookCache();
    // 当前章
    protected String mCurChapterId;

    private OnLoadingListener mOnLoadingListener;
    private PageLoaderListener mPageLoaderListener;

    protected LoadingStatus mStatus = LoadingStatus.STATUS_LOADING;
    protected DbBookRecord mDbBookRecord;

    protected void putCache(ChapterModel chapterModel) {
        if (mChapterCache == null) {
            mChapterCache = new BookCache<>();
        }

        if (chapterModel == null) {
            throw new IllegalArgumentException("the chapterModel is null");
        }

        mChapterCache.put(chapterModel.getId(), chapterModel);
    }

    public void setOnLoadingListener(OnLoadingListener onLoadingListener) {
        mOnLoadingListener = onLoadingListener;
    }

    public void setPageLoaderListener(PageLoaderListener pageLoaderListener) {
        mPageLoaderListener = pageLoaderListener;
    }

    public PageLoader(BookModel bookModel) {
        mBookModel = bookModel;
    }

    public void initData() {
        requestCatalogue(mBookModel.getId());

        mDbBookRecord = DbHelp.get().queryRecord(mBookModel.getId());
        if (mDbBookRecord != null) {
            mCurChapterId = mDbBookRecord.getChapterId();
            if (!hasChapterData()) {
                if (mBookModel.isLocal()) {
                    // todo 本地加载数据
                } else if (mPageLoaderListener != null) {
                    requestChapter(mBookModel.getId(), mCurChapterId);
                    updateStatus(LoadingStatus.STATUS_LOADING);
                }
            }
        }
    }

    /**
     * 通知更新状态
     *
     * @param status
     */
    protected void notifyStatus(LoadingStatus status) {
        updateStatus(status);
        if (mOnLoadingListener != null) {
            mOnLoadingListener.updateStatus(status);
        }
    }

    private void updateStatus(LoadingStatus status) {
        mStatus = status;
    }

    public LoadingStatus getStatus() {
        return mStatus;
    }

    protected void requestCatalogue(String bookId) {
        if (mPageLoaderListener != null) {
            mPageLoaderListener.requestCatalogue(bookId);
        }
    }

    protected void requestChapter(String bookId, String novelId) {
        if (mPageLoaderListener != null) {
            mPageLoaderListener.requestChapter(bookId, novelId);
        }
    }


    /**
     * 获取当前页
     *
     * @return
     */
    public TxtPage getCurPage() {
        if (mCurPage == null) {
            if (mDbBookRecord != null) {
                // 查看是否缓存有数据
                if (hasChapterData()) {
                    List<TxtPage> txtPages = parseChapter(mDbBookRecord);
                    if (txtPages != null) {
                        mCurPageList = txtPages;
                        int position;
                        if (mDbBookRecord.getPagePosition() == DbBookRecord.POSITION_PRE) {
                            position = mCurPageList.size() - 1;
                        } else if (mDbBookRecord.getPagePosition() == DbBookRecord.POSITION_NEXT) {
                            position = 0;
                        } else {
                            position = mDbBookRecord.getPagePosition() < mCurPageList.size() ?
                                    Math.max(mDbBookRecord.getPagePosition(), 0) : mCurPageList.size() - 1;
                        }
                        mCurPage = mCurPageList.get(position);
                        changeProgress(position);
                        updateStatus(LoadingStatus.STATUS_FINISH);
                    }
                } else {
                    if (mBookModel.isLocal()) {
                        // todo
                    } else {
                        requestCurChapter();

                        // 顺带缓存该章节的前后两章节
                        preRequestNext();
                        preRequestPre();
                    }
                }
            } else {
                requestCurChapter();
            }
        }

        return mCurPage;
    }

    /**
     * 查看是否有该章节,有则请求网络
     */
    private void requestCurChapter() {
        ChapterModel curChapter = getCurChapter();
        if (curChapter != null) {
            requestChapter(mBookModel.getId(), curChapter.getId());
            updateStatus(LoadingStatus.STATUS_LOADING);
        } else {
            updateStatus(LoadingStatus.STATUS_ERROR);
        }
    }

    /**
     * 请求上一章
     */
    private void preRequestPre() {

        ChapterModel preChapter = getPreChapter();
        if (preChapter != null
                && (!hasChapterData(preChapter.getNovelId(), preChapter.getId(), preChapter.getName()))) {
            requestChapter(mBookModel.getId(), preChapter.getId());
        }
    }

    /**
     * 请求下一章
     */
    private void preRequestNext() {
        ChapterModel nexChapter = getNexChapter();
        if (nexChapter != null
                && (!hasChapterData(nexChapter.getNovelId(), nexChapter.getId(), nexChapter.getName()))) {
            requestChapter(mBookModel.getId(), nexChapter.getId());
        }
    }

    protected void changeProgress(int pagePosition) {
        changeProgress(mCurChapterId, "", pagePosition);
    }

    protected void changeProgress(String chapterId, String chapterName, int pagePosition) {
        mDbBookRecord.setChapterId(chapterId);
        mDbBookRecord.setPagePosition(pagePosition);
        if (!TextUtils.isEmpty(chapterName)) {
            mDbBookRecord.setChapterName(chapterName);
        }
    }

    public void saveDbCurProgress() {
        if (mDbBookRecord != null) {
            ThreadUtils.execute(() -> {
                DbHelp.get().insertBookRecord(mDbBookRecord);
            });
        }
    }

    protected ChapterModel getCurChapter() {
        if (!TextUtils.isEmpty(mCurChapterId) && mCatalogue != null) {
            for (ChapterModel chapterModel : mCatalogue) {
                if (TextUtils.equals(mCurChapterId, chapterModel.getId())) {
                    return chapterModel;
                }
            }
        }

        return null;
    }

    protected ChapterModel getPreChapter() {
        if (!TextUtils.isEmpty(mCurChapterId) && mCatalogue != null) {
            for (int i = 0; i < mCatalogue.size(); i++) {
                if (TextUtils.equals(mCurChapterId, mCatalogue.get(i).getId())) {
                    return i - 1 >= 0 && i < mCatalogue.size() ? mCatalogue.get(i - 1) : null;
                }
            }
        }

        return null;
    }

    protected ChapterModel getNexChapter() {
        if (!TextUtils.isEmpty(mCurChapterId) && mCatalogue != null) {
            for (int i = 0; i < mCatalogue.size(); i++) {
                if (TextUtils.equals(mCurChapterId, mCatalogue.get(i).getId())) {
                    return i + 1 >= 0 && i < mCatalogue.size() ? mCatalogue.get(i + 1) : null;
                }
            }
        }

        return null;
    }


    protected List<TxtPage> parseChapter(DbBookRecord dbBookRecord) {
        List<TxtPage> txtPages = null;
        BufferedReader chapterReader = getChapterReader(dbBookRecord);
        if (chapterReader != null) {
            txtPages = loadPages(dbBookRecord.getChapterName(), chapterReader);
            if (txtPages == null || txtPages.isEmpty()) {
                updateStatus(LoadingStatus.STATUS_EMPTY);
            }
        } else {
            updateStatus(LoadingStatus.STATUS_EMPTY);
        }

        return txtPages;
    }

    public TxtPage getNextPage() {
        if (mCurPage == null || mCurPageList == null ||
                mCurPage.position + 1 >= mCurPageList.size()) {
            // 正在请求下一章数据
            if (mStatus == LoadingStatus.STATUS_LOADING
                    && mDbBookRecord.getPagePosition() == DbBookRecord.POSITION_NEXT) {
                getCurPage();
                return null;
            }

            // 当前章节没数据了
            ChapterModel nexChapter = getNexChapter();
            if (nexChapter != null) {
                mCurChapterId = nexChapter.getId();
                changeProgress(nexChapter.getId(), nexChapter.getName(), DbBookRecord.POSITION_NEXT);
                mCurPage = null;
                getCurPage();
            } else {
                updateStatus(LoadingStatus.STATUS_END);
                ToastUtils.show(App.get(), "没有下一页了");
            }
        } else {
            int nextIndex = mCurPage.position + 1;
            mCurPage = mCurPageList.get(nextIndex);
            changeProgress(nextIndex);

            if (nextIndex >= mCurPageList.size() - PRE_LOAD_INDEX) {
                preRequestNext();
            }
        }

        return mCurPage;
    }

    public TxtPage getPrePage() {
        if (mCurPage == null || mCurPageList == null ||
                mCurPage.position - 1 < 0) {
            // 正在请求上一章数据
            if (mStatus == LoadingStatus.STATUS_LOADING
                    && mDbBookRecord.getPagePosition() == DbBookRecord.POSITION_PRE) {
                getCurPage();
                return null;
            }

            ChapterModel preChapter = getPreChapter();
            if (preChapter != null) {
                mCurChapterId = preChapter.getId();
                changeProgress(preChapter.getId(), preChapter.getName(), DbBookRecord.POSITION_PRE);
                mCurPage = null;
                getCurPage();
            } else {
                ToastUtils.show(App.get(), "没有上一页了");
            }
        } else {
            int preIndex = mCurPage.position - 1;
            mCurPage = mCurPageList.get(preIndex);
            changeProgress(preIndex);

            if (preIndex <= PRE_LOAD_INDEX) {
                preRequestPre();
            }
        }

        return mCurPage;
    }

    /**
     * 将章节数据，解析成页面列表
     *
     * @param br：章节的文本流
     * @return
     */
    private List<TxtPage> loadPages(String title, BufferedReader br) {
        TextPaint titlePaint = new TextPaint();
        titlePaint.setTextSize(PageConfig.get().getTitleSize());

        // 绘制页面内容的画笔
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(PageConfig.get().getTextSize());

        //生成的页面
        List<TxtPage> pages = new ArrayList<>();
        //使用流的方式加载
        List<String> lines = new ArrayList<>();
        int rHeight = PageConfig.get().getVisibleHeight();
        int titleLinesCount = 0;
        boolean showTitle = true; // 是否展示标题
        String paragraph = title;//默认展示标题
        try {
            while (showTitle || (paragraph = br.readLine()) != null) {
                paragraph = StringUtils.convertCC(paragraph, App.get());
                // 重置段落
                if (!showTitle) {
                    paragraph = paragraph.replaceAll("\\s", "");
                    // 如果只有换行符，那么就不执行
                    if (paragraph.equals("")) continue;
                    paragraph = StringUtils.halfToFull("  " + paragraph + "\n");
                } else {
                    //设置 title 的顶部间距
                    rHeight -= PageConfig.get().getTitlePara();
                }
                int wordCount = 0;
                String subStr = null;
                while (paragraph.length() > 0) {
                    //当前空间，是否容得下一行文字
                    if (showTitle) {
                        rHeight -= titlePaint.getTextSize();
                    } else {
                        rHeight -= textPaint.getTextSize();
                    }
                    // 一页已经填充满了，创建 TextPage
                    if (rHeight <= 0) {
                        // 创建Page
                        TxtPage page = new TxtPage();
                        page.position = pages.size();
                        page.title = StringUtils.convertCC(title, App.get());
                        page.lines = new ArrayList<>(lines);
                        page.titleLines = titleLinesCount;
                        pages.add(page);
                        // 重置Lines
                        lines.clear();
                        rHeight = PageConfig.get().getVisibleHeight();
                        titleLinesCount = 0;

                        continue;
                    }

                    //测量一行占用的字节数
                    if (showTitle) {
                        wordCount = titlePaint.breakText(paragraph,
                                true, PageConfig.get().getVisibleWidth(), null);
                    } else {
                        wordCount = textPaint.breakText(paragraph,
                                true, PageConfig.get().getVisibleWidth(), null);
                    }

                    subStr = paragraph.substring(0, wordCount);
                    if (!subStr.equals("\n")) {
                        //将一行字节，存储到lines中
                        lines.add(subStr);

                        //设置段落间距
                        if (showTitle) {
                            titleLinesCount += 1;
                            rHeight -= PageConfig.get().getTitleInterval();
                        } else {
                            rHeight -= PageConfig.get().getTextInterval();
                        }
                    }
                    //裁剪
                    paragraph = paragraph.substring(wordCount);
                }

                //增加段落的间距
                if (!showTitle && lines.size() != 0) {
                    rHeight = rHeight - PageConfig.get().getTextPara() + PageConfig.get().getTextInterval();
                }

                if (showTitle) {
                    rHeight = rHeight - PageConfig.get().getTitlePara() + PageConfig.get().getTitleInterval();
                    showTitle = false;
                }
            }

            if (lines.size() != 0) {
                //创建Page
                TxtPage page = new TxtPage();
                page.position = pages.size();
                page.title = StringUtils.convertCC(title, App.get());
                page.lines = new ArrayList<>(lines);
                page.titleLines = titleLinesCount;
                pages.add(page);
                //重置Lines
                lines.clear();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return pages;
    }

    public abstract void release();

    /**
     * 章节数据是否存在
     *
     * @return
     */
    protected abstract boolean hasChapterData();

    /**
     * 章节数据是否存在
     *
     * @return
     */
    protected abstract boolean hasChapterData(String bookId, String chapterId, String chapterName);

    /**
     * 获取章节的文本流
     *
     * @return
     */
    protected abstract BufferedReader getChapterReader(DbBookRecord bookRecord);


    public interface PageLoaderListener {
        void requestChapter(String bookId, String chapterId);

        void requestCatalogue(String bookId);
    }


    protected class BookCache<K, V> extends LinkedHashMap<K, V> {
        public static final int CACHE_SIZE = 20;

        public BookCache() {
            super(16, 0.75F, true);
        }

        @Override
        protected boolean removeEldestEntry(Entry<K, V> eldest) {
            return size() > CACHE_SIZE;
        }
    }
}
