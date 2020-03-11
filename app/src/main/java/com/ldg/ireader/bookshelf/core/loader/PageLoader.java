package com.ldg.ireader.bookshelf.core.loader;

import android.text.TextPaint;

import com.ldg.ireader.App;
import com.ldg.ireader.bookshelf.core.config.PageConfig;
import com.ldg.ireader.bookshelf.model.BookModel;
import com.ldg.ireader.bookshelf.model.TxtChapter;
import com.ldg.ireader.bookshelf.model.TxtPage;
import com.ldg.ireader.db.DbHelp;
import com.ldg.ireader.db.entity.DbBookRecord;
import com.ldg.ireader.utils.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class PageLoader {

    protected BookModel mBookModel;

    protected TxtPage mCurPage;
    protected List<TxtPage> mCurPageList;
    protected List<TxtPage> mPrePageList;
    protected List<TxtPage> mNextPageList;
    // 目录
    protected List<TxtChapter> mCatalogue = new ArrayList<>();
    // 当前章
    protected int mCurChapterPos = 0;
    protected int mLastChapterPos;
    private OnLoadingListener mOnLoadingListener;
    private PageLoaderListener mPageLoaderListener;
    private LoadingStatus mStatus;
    private boolean isFirstOpen = true;
    protected DbBookRecord mDbBookRecord;

    public void setOnLoadingListener(OnLoadingListener onLoadingListener) {
        mOnLoadingListener = onLoadingListener;
    }

    public void setPageLoaderListener(PageLoaderListener pageLoaderListener) {
        mPageLoaderListener = pageLoaderListener;
    }

    public PageLoader(BookModel bookModel) {
        mBookModel = bookModel;
    }

    public TxtPage getCurPage() {
        return mCurPage;
    }

    public void initData() {
        if (mPageLoaderListener != null) {
            mPageLoaderListener.requestCatalogue(mBookModel.getId());
        }
        if (isFirstOpen) {
            mDbBookRecord = DbHelp.get().queryRecord(mBookModel.getId());
            isFirstOpen = false;
        }
        if (hasChapterData()) {
            parseChapter();
        } else {
            if (mBookModel.isLocal() && mOnLoadingListener != null) {
                mOnLoadingListener.updateStatus(LoadingStatus.STATUS_PARSE_ERROR);
            } else if (mPageLoaderListener != null) {
                mPageLoaderListener.requestChapter(new DbBookRecord(mBookModel.getId()));
            }
        }
    }

    private void parseChapter() {
        if (mOnLoadingListener != null) {
            BufferedReader chapterReader = getChapterReader(mDbBookRecord);
            if (chapterReader != null) {
                mCurPageList = loadPages(mDbBookRecord.getChapterName(), chapterReader);
                if (mCurPageList == null || mCurPageList.isEmpty()) {
                    mOnLoadingListener.updateStatus(LoadingStatus.STATUS_EMPTY);
                    return;
                }
                int position = mDbBookRecord.getPagePosition() < mCurPageList.size() ?
                        mDbBookRecord.getPagePosition() : mCurPageList.size() - 1;
                mCurPage = mCurPageList.get(position);
                mOnLoadingListener.updateStatus(LoadingStatus.STATUS_FINISH);
            } else {
                mOnLoadingListener.updateStatus(LoadingStatus.STATUS_EMPTY);
            }
        }


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


    public TxtPage getCurPage(int pos) {
        return mCurPageList.get(pos);
    }

    public TxtPage getNextPage() {
        if (mCurPage.position + 1 >= mCurPageList.size()) {
            if (mNextPageList != null && !mNextPageList.isEmpty()) {
                mPrePageList = mCurPageList;
                mCurPageList = mNextPageList;
                return mCurPage = mCurPageList.get(0);
            }
            return null;
        }
        mCurPage = mCurPageList.get(mCurPage.position + 1);
        return mCurPage;
    }

    public TxtPage getPrePage() {
        if (mCurPage.position - 1 < 0) {
            if (mPrePageList != null && !mPrePageList.isEmpty()) {
                mNextPageList = mCurPageList;
                mCurPageList = mPrePageList;
                // todo 解析前一章
                mPrePageList = null;
                return mCurPage = mCurPageList.get(mCurPageList.size() - 1);
            }
            return null;
        }
        mCurPage = mCurPageList.get(mCurPage.position - 1);
        return mCurPage;
    }


    /**
     * 章节数据是否存在
     *
     * @return
     */
    protected abstract boolean hasChapterData();

    /**
     * 获取章节的文本流
     *
     * @return
     */
    protected abstract BufferedReader getChapterReader(DbBookRecord bookRecord);


    public interface PageLoaderListener {
        void requestChapter(DbBookRecord... bookRecords);

        void requestCatalogue(String bookId);
    }
}
