package com.ldg.ireader.bookshelf.core.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;

import com.ldg.ireader.App;
import com.ldg.ireader.bookshelf.core.config.PageConfig;
import com.ldg.ireader.bookshelf.model.BookModel;
import com.ldg.ireader.bookshelf.model.TxtChapter;
import com.ldg.ireader.bookshelf.model.TxtPage;
import com.ldg.ireader.utils.StringUtils;
import com.ldg.ireader.bookshelf.core.widgets.PageView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class PageLoader {

    private final ArrayList<TxtChapter> mChapterList;
    private BookModel mBookModel;
    private boolean isChapterOpen;
    private boolean isFirstOpen;

    private TxtPage mCurPage;
    private TxtPage mCancelPage;
    private List<TxtPage> mCurPageList;
    // 当前章
    protected int mCurChapterPos = 0;
    private OnLoadingListener mOnLoadingListener;
    private PageConfig mPageConfig;
    private LoadingStatus mStatus;

    public void setOnLoadingListener(OnLoadingListener onLoadingListener) {
        mOnLoadingListener = onLoadingListener;
    }

    public PageLoader(BookModel bookModel) {
        mBookModel = bookModel;
        mChapterList = new ArrayList<>(1);
    }


    public void setPageConfig(PageConfig pageConfig) {
        mPageConfig = pageConfig;
    }

    public TxtPage getCurPage() {
        return mCurPage;
    }

    public List<TxtPage> getCurPageList() {
        return mCurPageList;
    }

    public void initData() {
        if (!isFirstOpen) {
            // 打开书籍
            openChapter();
        }
    }

    private void openChapter() {
        isFirstOpen = false;
        if (parseCurChapter()) {
            // 如果章节从未打开
            if (!isChapterOpen) {
                int position = 0;

                // 防止记录页的页号，大于当前最大页号
                if (position >= mCurPageList.size()) {
                    position = mCurPageList.size() - 1;
                }
                mCurPage = getCurPage(position);
                mCancelPage = mCurPage;
                // 切换状态
                isChapterOpen = true;
            } else {
                mCurPage = getCurPage(0);
            }
        } else {
            mCurPage = new TxtPage();
        }

        if (mOnLoadingListener != null) {
            mOnLoadingListener.updateStatus(mStatus);
        }
    }


    /**
     * @return:获取初始显示的页面
     */
    public TxtPage getCurPage(int pos) {
        return mCurPageList.get(pos);
    }

    public TxtPage getNextPage() {
        if (mCurPage.position + 1 >= mCurPageList.size()) {
            return null;
        }
        mCurPage = mCurPageList.get(mCurPage.position + 1);
        return mCurPage;
    }

    public TxtPage getPrePage() {
        if (mCurPage.position - 1 < 0) {
            return null;
        }
        mCurPage = mCurPageList.get(mCurPage.position - 1);
        return mCurPage;
    }

    private boolean parseCurChapter() {
        // 解析数据
        dealLoadPageList(mCurChapterPos);
        // 预加载下一页面
//        preLoadNextChapter();
        return mCurPageList != null ? true : false;
    }

    private void dealLoadPageList(int chapterPos) {
        mStatus = null;
        try {
            mCurPageList = loadPageList(chapterPos);
            if (mCurPageList != null) {
                if (mCurPageList.isEmpty()) {
                    mStatus = LoadingStatus.STATUS_EMPTY;

                    // 添加一个空数据
                    TxtPage page = new TxtPage();
                    page.lines = new ArrayList<>(1);
                    mCurPageList.add(page);
                } else {
                    mStatus = LoadingStatus.STATUS_FINISH;
                }
            } else {
                mStatus = LoadingStatus.STATUS_LOADING;
            }
        } catch (Exception e) {
            e.printStackTrace();

            mCurPageList = null;
            mStatus = LoadingStatus.STATUS_ERROR;
        }
    }

    /**
     * 加载页面列表
     *
     * @param chapterPos:章节序号
     * @return
     */
    private List<TxtPage> loadPageList(int chapterPos) throws Exception {
        // 获取章节
//        TxtChapter chapter = mChapterList.get(chapterPos);
        TxtChapter chapter = new TxtChapter();
        chapter.setTitle("假装是个标题");
        // 判断章节是否存在
        if (!hasChapterData(chapter)) {
            return null;
        }
        // 获取章节的文本流
        BufferedReader reader = getChapterReader(chapter);
        List<TxtPage> chapters = loadPages(chapter, reader);

        return chapters;
    }


    /**
     * 将章节数据，解析成页面列表
     *
     * @param chapter：章节信息
     * @param br：章节的文本流
     * @return
     */
    private List<TxtPage> loadPages(TxtChapter chapter, BufferedReader br) {
        TextPaint titlePaint = new TextPaint();
        titlePaint.setTextSize(mPageConfig.getTitleSize());

        // 绘制页面内容的画笔
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mPageConfig.getTextSize());

        //生成的页面
        List<TxtPage> pages = new ArrayList<>();
        //使用流的方式加载
        List<String> lines = new ArrayList<>();
        int rHeight = mPageConfig.getVisibleHeight();
        int titleLinesCount = 0;
        boolean showTitle = true; // 是否展示标题
        String paragraph = chapter.getTitle();//默认展示标题
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
                    rHeight -= mPageConfig.getTitlePara();
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
                        page.title = StringUtils.convertCC(chapter.getTitle(), App.get());
                        page.lines = new ArrayList<>(lines);
                        page.titleLines = titleLinesCount;
                        pages.add(page);
                        // 重置Lines
                        lines.clear();
                        rHeight = mPageConfig.getVisibleHeight();
                        titleLinesCount = 0;

                        continue;
                    }

                    //测量一行占用的字节数
                    if (showTitle) {
                        wordCount = titlePaint.breakText(paragraph,
                                true, mPageConfig.getVisibleWidth(), null);
                    } else {
                        wordCount = textPaint.breakText(paragraph,
                                true, mPageConfig.getVisibleWidth(), null);
                    }

                    subStr = paragraph.substring(0, wordCount);
                    if (!subStr.equals("\n")) {
                        //将一行字节，存储到lines中
                        lines.add(subStr);

                        //设置段落间距
                        if (showTitle) {
                            titleLinesCount += 1;
                            rHeight -= mPageConfig.getTitleInterval();
                        } else {
                            rHeight -= mPageConfig.getTextInterval();
                        }
                    }
                    //裁剪
                    paragraph = paragraph.substring(wordCount);
                }

                //增加段落的间距
                if (!showTitle && lines.size() != 0) {
                    rHeight = rHeight - mPageConfig.getTextPara() + mPageConfig.getTextInterval();
                }

                if (showTitle) {
                    rHeight = rHeight - mPageConfig.getTitlePara() + mPageConfig.getTitleInterval();
                    showTitle = false;
                }
            }

            if (lines.size() != 0) {
                //创建Page
                TxtPage page = new TxtPage();
                page.position = pages.size();
                page.title = StringUtils.convertCC(chapter.getTitle(), App.get());
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


    /**
     * 章节数据是否存在
     *
     * @return
     */
    protected abstract boolean hasChapterData(TxtChapter chapter);

    /**
     * 获取章节的文本流
     *
     * @param chapter
     * @return
     */
    protected abstract BufferedReader getChapterReader(TxtChapter chapter) throws Exception;
}
