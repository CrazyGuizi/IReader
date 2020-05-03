package com.ldg.ireader.bookshelf.core.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;

import com.ldg.ireader.bookshelf.core.config.PageConfig;
import com.ldg.ireader.bookshelf.core.loader.LoadingStatus;
import com.ldg.ireader.bookshelf.core.loader.PageLoader;
import com.ldg.ireader.bookshelf.core.widgets.BasePageView;
import com.ldg.ireader.bookshelf.model.TxtPage;

public class PageDrawHelper {

    private BasePageView mReadPage;
    private TextPaint mTextPaint;
    private Paint mBgPaint;
    private Paint mTitlePaint;

    private LoadingStatus mStatus = LoadingStatus.STATUS_LOADING;
    private PageLoader mPageLoader;

    public void setStatus(LoadingStatus status) {
        mStatus = status;
    }

    public PageDrawHelper(BasePageView readPage, PageLoader pageLoader) {
        if (readPage == null || pageLoader == null) {
            return;
        }

        mReadPage = readPage;
        mPageLoader = pageLoader;
        initPaint();
    }

    private void initPaint() {
        // 绘制标题的画笔
        if (mTitlePaint == null) {
            mTitlePaint = new TextPaint();
            mTitlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD);
            mTitlePaint.setAntiAlias(true);
        }

        // 绘制背景的画笔
        if (mBgPaint == null) {
            mBgPaint = new Paint();
        }

        // 绘制页面内容的画笔
        if (mTextPaint == null) {
            mTextPaint = new TextPaint();
            mTextPaint.setStyle(Paint.Style.FILL);
            mTextPaint.setAntiAlias(true);
        }

        if (PageConfig.get() != null) {
            mTitlePaint.setColor(PageConfig.get().getTextColor());
            mTitlePaint.setTextSize(PageConfig.get().getTitleSize());

            mBgPaint.setColor(PageConfig.get().getTextColor());

            mTextPaint.setColor(PageConfig.get().getTextColor());
            mTextPaint.setTextSize(PageConfig.get().getTextSize());
        }
    }

    public boolean drawNextPage(boolean updateContent) {
        TxtPage page = mPageLoader.getNextPage();
        if (page != null) {
            mReadPage.changeBitmap();
            drawPage(updateContent);
        }

        return page != null;
    }

    public boolean drawPrePage(boolean updateContent) {
        TxtPage prePage = mPageLoader.getPrePage();
        if (prePage != null) {
            mReadPage.changeBitmap();
            drawPage(updateContent);
        }

        return prePage != null;
    }

    public void drawPage(boolean updateContent) {
        Bitmap bitmap = mReadPage.getNextBitmap();
        drawBackground(bitmap, updateContent);

        if (updateContent) {
            TxtPage curPage = mPageLoader.getCurPage();
            mStatus = mPageLoader.getStatus();
            drawContent(bitmap, curPage);
        }
        mReadPage.invalidate();
    }

    private void drawBackground(Bitmap bgBitmap, boolean updateContent) {
        Canvas canvas = new Canvas(bgBitmap);

        if (updateContent) {
            canvas.drawColor(PageConfig.get().getBgColor());
        } else {
            mBgPaint.setColor(PageConfig.get().getBgColor());
        }

    }

    private void drawContent(Bitmap bitmap, TxtPage curPage) {
        Canvas canvas = new Canvas(bitmap);

        if (mStatus != LoadingStatus.STATUS_FINISH) {
            //绘制字体
            String tip = "";
            switch (mStatus) {
                case STATUS_LOADING:
                    tip = "正在拼命加载中...";
                    break;
                case STATUS_ERROR:
                    tip = "加载失败(点击边缘重试)";
                    break;
                case STATUS_EMPTY:
                    tip = "文章内容为空";
                    break;
                case STATUS_END:
                    tip = "全书完";
                    break;
                case STATUS_PARING:
                    tip = "正在排版请等待...";
                    break;
                case STATUS_PARSE_ERROR:
                    tip = "文件解析错误";
                    break;
                case STATUS_CATEGORY_EMPTY:
                    tip = "目录列表为空";
                    break;
            }

            //将提示语句放到正中间
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float textHeight = fontMetrics.top - fontMetrics.bottom;
            float textWidth = mTextPaint.measureText(tip);
            float pivotX = (PageConfig.get().getDisplayWidth() - textWidth) / 2;
            float pivotY = (PageConfig.get().getDisplayHeight() - textHeight) / 2;
            canvas.drawText(tip, pivotX, pivotY, mTextPaint);
            return;
        }

        if (curPage == null) {
            return;
        }

        float top = PageConfig.get().getMarginHeight() - mTextPaint.getFontMetrics().top;

        //设置总距离
        int interval = PageConfig.get().getTextInterval() + (int) mTextPaint.getTextSize();
        int para = PageConfig.get().getTextPara() + (int) mTextPaint.getTextSize();
        int titleInterval = PageConfig.get().getTitleInterval() + (int) mTitlePaint.getTextSize();
        int titlePara = PageConfig.get().getTitlePara() + (int) mTextPaint.getTextSize();
        String str = null;

        for (int i = 0; i < curPage.titleLines; i++) {
            str = curPage.lines.get(0);
            //设置顶部间距
            if (i == 0) {
                top += PageConfig.get().getTitlePara();
            }

            //计算文字显示的起始点
            int start = (int) (PageConfig.get().getDisplayWidth() - mTitlePaint.measureText(str)) / 2;
            //进行绘制
            canvas.drawText(str, start, top, mTitlePaint);

            //设置尾部间距
            if (i == curPage.titleLines - 1) {
                top += titlePara;
            } else {
                //行间距
                top += titleInterval;
            }
        }

        //对内容进行绘制
        for (int i = curPage.titleLines; i < curPage.lines.size(); ++i) {
            str = curPage.lines.get(i);

            canvas.drawText(str, PageConfig.get().getMarginWidth(), top, mTextPaint);
            if (str.endsWith("\n")) {
                top += para;
            } else {
                top += interval;
            }
        }
    }


}
