package com.ldg.ireader.bookshelf.core.config;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.ldg.common.util.SP;
import com.ldg.ireader.App;
import com.ldg.ireader.bookshelf.core.PageStyle;
import com.ldg.ireader.bookshelf.core.anim.PageMode;
import com.ldg.ireader.utils.SPKey;
import com.ldg.ireader.utils.ScreenUtils;

public class PageConfig {

    public static final PageConfig get() {
        return Holder.HOLDER;
    }

    private static class Holder {
        private static final PageConfig HOLDER = new PageConfig();
    }

    // 默认的显示参数配置1
    public static final int DEFAULT_MARGIN_HEIGHT = ScreenUtils.dp2px(25);
    public static final int DEFAULT_MARGIN_WIDTH = ScreenUtils.dp2px(15);

    public static final int DEFAULT_TEXT_SIZE = ScreenUtils.sp2Px(20);

    private PageMode mPageMode;
    private PageStyle mPageStyle;
    private Context mContext = App.get();

    private int mDisplayWidth;
    private int mDisplayHeight;
    //书籍绘制区域的宽高
    private int mVisibleWidth;
    private int mVisibleHeight;
    //间距
    private int mMarginWidth;
    private int mMarginHeight;

    //标题的大小
    private int mTitleSize;
    //字体的大小
    private int mTextSize;
    //段落距离(基于行间距的额外距离)
    private int mTitlePara;
    private int mTextPara;
    //标题的行间距
    private int mTitleInterval;
    //行间距
    private int mTextInterval;

    //字体的颜色
    private int mTextColor;
    private int mBgColor;


    private PageConfig() {
        mPageMode = PageMode.valueOf(SP.getString(mContext, SPKey.KEY_PAGE_MODE, PageMode.SLIDE.name()));
        mPageStyle = PageStyle.valueOf(SP.getString(mContext, SPKey.KEY_PAGE_STYLE, PageStyle.BG_GREEN.name()));
        mMarginWidth = SP.getInt(mContext, SPKey.KEY_PAGE_MARGIN_WIDTH, DEFAULT_MARGIN_WIDTH);
        mMarginHeight = SP.getInt(mContext, SPKey.KEY_PAGE_MARGIN_HEIGHT, DEFAULT_MARGIN_HEIGHT);
        mTitleSize = SP.getInt(mContext, SPKey.KEY_PAGE_TITLE_SIZE, DEFAULT_TEXT_SIZE);
        mTextSize = SP.getInt(mContext, SPKey.KEY_PAGE_TEXT_SIZE, DEFAULT_TEXT_SIZE);

        mTitlePara = SP.getInt(mContext, SPKey.KEY_PAGE_TITLE_PARA);
        if (mTitlePara <= 0) {
            setTitlePara(mTitleSize);
        }

        mTextPara = SP.getInt(mContext, SPKey.KEY_PAGE_TEXT_PARA);
        if (mTextPara <= 0) {
            setTextPara(mTextSize);
        }

        mTitleInterval = SP.getInt(mContext, SPKey.KEY_PAGE_TITLE_INTERVAL);
        if (mTitleInterval <= 0) {
            setTitleInterval(mTitleSize / 2);
        }

        mTextInterval = SP.getInt(mContext, SPKey.KEY_PAGE_TEXT_INTERVAL);
        if (mTextInterval <= 0) {
            setTextInterval(mTextSize / 2);
        }

        mTextColor = ContextCompat.getColor(App.get(), mPageStyle.getFontColor());
        mBgColor = ContextCompat.getColor(App.get(), mPageStyle.getBgColor());
    }


    public PageConfig setDisplayWidth(int displayWidth) {
        mDisplayWidth = displayWidth;
        mVisibleWidth = displayWidth - mMarginWidth * 2;
        return this;
    }

    public PageConfig setDisplayHeight(int displayHeight) {
        mDisplayHeight = displayHeight;
        mVisibleHeight = displayHeight - 2 * mMarginHeight;
        return this;
    }

    public PageConfig setPageMode(PageMode pageMode) {
        if (pageMode != null) {
            SP.setParam(mContext, SPKey.KEY_PAGE_MODE, pageMode.name());
            mPageMode = pageMode;
        }
        return this;
    }

    public PageConfig setPageStyle(PageStyle pageStyle) {
        if (pageStyle != null) {
            SP.setParam(mContext, SPKey.KEY_PAGE_MODE, pageStyle.name());
            mPageStyle = pageStyle;
        }
        return this;
    }

    public PageConfig setMarginWidth(int marginWidth) {
        if (marginWidth >= 0) {
            SP.putInt(mContext, SPKey.KEY_PAGE_MARGIN_WIDTH, marginWidth);
            mMarginWidth = marginWidth;
        }
        return this;
    }

    public PageConfig setMarginHeight(int marginHeight) {
        if (marginHeight >= 0) {
            SP.putInt(mContext, SPKey.KEY_PAGE_MARGIN_HEIGHT, marginHeight);
            mMarginHeight = marginHeight;
        }
        return this;
    }

    public PageConfig setTitleSize(int titleSize) {
        if (titleSize > 0) {
            SP.putInt(mContext, SPKey.KEY_PAGE_TITLE_SIZE, titleSize);
            mTitleSize = titleSize;
        }
        return this;
    }

    public PageConfig setTextSize(int textSize) {
        if (textSize > 0) {
            SP.putInt(mContext, SPKey.KEY_PAGE_TEXT_SIZE, textSize);
            mTextSize = textSize;
        }
        return this;
    }

    public PageConfig setTitlePara(int titlePara) {
        if (titlePara > 0) {
            SP.putInt(mContext, SPKey.KEY_PAGE_TITLE_PARA, titlePara);
            mTitlePara = titlePara;
        }
        return this;
    }

    public PageConfig setTextPara(int textPara) {
        if (textPara > 0) {
            SP.putInt(mContext, SPKey.KEY_PAGE_TEXT_PARA, textPara);
            mTextPara = textPara;
        }
        return this;
    }

    public PageConfig setTitleInterval(int titleInterval) {
        if (titleInterval > 0) {
            SP.putInt(mContext, SPKey.KEY_PAGE_TITLE_INTERVAL, titleInterval);
            mTitleInterval = titleInterval;
        }
        return this;
    }

    public PageConfig setTextInterval(int textInterval) {
        if (textInterval > 0) {
            SP.putInt(mContext, SPKey.KEY_PAGE_TEXT_INTERVAL, textInterval);
            mTextInterval = textInterval;
        }
        return this;
    }


    public PageConfig setVisibleWidth(int visibleWidth) {
        mVisibleWidth = visibleWidth;
        return this;
    }

    public PageConfig setVisibleHeight(int visibleHeight) {
        mVisibleHeight = visibleHeight;
        return this;
    }


    public PageMode getPageMode() {
        return mPageMode;
    }

    public PageStyle getPageStyle() {
        return mPageStyle;
    }

    public int getDisplayWidth() {
        return mDisplayWidth;
    }

    public int getDisplayHeight() {
        return mDisplayHeight;
    }

    public int getVisibleWidth() {
        return mVisibleWidth;
    }

    public int getVisibleHeight() {
        return mVisibleHeight;
    }

    public int getMarginWidth() {
        return mMarginWidth;
    }

    public int getMarginHeight() {
        return mMarginHeight;
    }

    public int getTitleSize() {
        return mTitleSize;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public int getTitlePara() {
        return mTitlePara;
    }

    public int getTextPara() {
        return mTextPara;
    }

    public int getTitleInterval() {
        return mTitleInterval;
    }

    public int getTextInterval() {
        return mTextInterval;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public int getBgColor() {
        return mBgColor;
    }

}
