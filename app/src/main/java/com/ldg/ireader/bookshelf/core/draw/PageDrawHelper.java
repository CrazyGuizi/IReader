package com.ldg.ireader.bookshelf.core.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;

import com.ldg.ireader.App;
import com.ldg.ireader.bookshelf.core.config.PageConfig;
import com.ldg.ireader.bookshelf.core.loader.LoadingStatus;
import com.ldg.ireader.bookshelf.core.loader.PageLoader;
import com.ldg.ireader.bookshelf.core.widgets.PageView;
import com.ldg.ireader.bookshelf.model.TxtPage;

import java.util.List;

public class PageDrawHelper {

    private PageView mReadPage;
    private PageConfig mPageConfig;
    private TextPaint mTextPaint;
    private Paint mBgPaint;
    private Paint mTitlePaint;

    private LoadingStatus mStatus = LoadingStatus.STATUS_LOADING;
    private PageLoader mPageLoader;

    public void setStatus(LoadingStatus status) {
        mStatus = status;
    }

    public void setPageLoader(PageLoader pageLoader) {
        mPageLoader = pageLoader;
    }

    public PageDrawHelper(PageView readPage, PageConfig config) {
        if (readPage == null) {
            return;
        }

        if (config == null) {
            config = new PageConfig.Builder(App.get()).build();
        }
        mReadPage = readPage;
        mPageConfig = config;
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

        if (mPageConfig != null) {
            mTitlePaint.setColor(mPageConfig.getTextColor());
            mTitlePaint.setTextSize(mPageConfig.getTitleSize());

            mBgPaint.setColor(mPageConfig.getTextColor());

            mTextPaint.setColor(mPageConfig.getTextColor());
            mTextPaint.setTextSize(mPageConfig.getTextSize());
        }
    }

    public void setDisplaySize(int pageWidth, int pageHeight) {
        if (mPageConfig.getDisplayWidth() == pageWidth &&
                mPageConfig.getDisplayHeight() == pageHeight) {
            return;
        }
        mPageConfig.setDisplayWidth(pageWidth);
        mPageConfig.setDisplayHeight(pageHeight);
        mPageLoader.initData();
    }

    public void setPageConfig(PageConfig pageConfig) {
        if (pageConfig != null) {
            mPageConfig = pageConfig;
            initPaint();
        }
    }

    public boolean drawNext(Bitmap bitmap, boolean update) {
        TxtPage nextPage = mPageLoader.getNextPage();
        if (nextPage == null) {
            return false;
        }

        drawPage(bitmap, update);
        return true;
    }


    public boolean drawPre(Bitmap bitmap, boolean update) {
        TxtPage prePage = mPageLoader.getPrePage();
        if (prePage == null) {
            return false;
        }
        drawPage(bitmap, update);
        return true;
    }

    public void drawPage(Bitmap bitmap, boolean isUpdate) {
        drawBackground(bitmap, isUpdate);

        if (!isUpdate) {
            drawContent(bitmap);
        }
        mReadPage.invalidate();
    }

    private void drawBackground(Bitmap bgBitmap, boolean isUpdate) {
        Canvas canvas = new Canvas(bgBitmap);

        if (!isUpdate) {
            canvas.drawColor(mPageConfig.getBgColor());
        } else {
            mBgPaint.setColor(mPageConfig.getBgColor());
        }

    }

    private void drawContent(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        TxtPage curPage = mPageLoader.getCurPage();

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
            float pivotX = (mPageConfig.getDisplayWidth() - textWidth) / 2;
            float pivotY = (mPageConfig.getDisplayHeight() - textHeight) / 2;
            canvas.drawText(tip, pivotX, pivotY, mTextPaint);
            return;
        }

        float top = mPageConfig.getMarginHeight() - mTextPaint.getFontMetrics().top;

        //设置总距离
        int interval = mPageConfig.getTextInterval() + (int) mTextPaint.getTextSize();
        int para = mPageConfig.getTextPara() + (int) mTextPaint.getTextSize();
        int titleInterval = mPageConfig.getTitleInterval() + (int) mTitlePaint.getTextSize();
        int titlePara = mPageConfig.getTitlePara() + (int) mTextPaint.getTextSize();
        String str = null;

        for (int i = 0; i < curPage.titleLines; i++) {
            str = curPage.lines.get(0);
            //设置顶部间距
            if (i == 0) {
                top += mPageConfig.getTitlePara();
            }

            //计算文字显示的起始点
            int start = (int) (mPageConfig.getDisplayWidth() - mTitlePaint.measureText(str)) / 2;
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

            canvas.drawText(str, mPageConfig.getMarginWidth(), top, mTextPaint);
            if (str.endsWith("\n")) {
                top += para;
            } else {
                top += interval;
            }
        }
    }


}
