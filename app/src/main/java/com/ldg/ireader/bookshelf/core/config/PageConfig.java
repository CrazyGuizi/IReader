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

    // 默认的显示参数配置1
    public static final int DEFAULT_MARGIN_HEIGHT = ScreenUtils.dp2px(25);
    public static final int DEFAULT_MARGIN_WIDTH = ScreenUtils.dp2px(15);
    public static final int DEFAULT_TIP_SIZE = 12;
    public static final int EXTRA_TITLE_SIZE = 4;

    public static final int DEFAULT_TEXT_SIZE = ScreenUtils.sp2Px(20);

    private PageMode mPageMode;
    private PageStyle mPageStyle;

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
    }


    private PageConfig(Builder builder) {
        mPageMode = builder.mPageMode;
        mPageStyle = builder.mPageStyle;
        mMarginWidth = builder.mMarginWidth;
        mMarginHeight = builder.mMarginHeight;
        mTitleSize = builder.mTitleSize;
        mTextSize = builder.mTextSize;
        mTitlePara = builder.mTitlePara;
        mTextPara = builder.mTextPara;
        mTitleInterval = builder.mTitleInterval;
        mTextInterval = builder.mTextInterval;
        mTextColor = ContextCompat.getColor(App.get(), mPageStyle.getFontColor());
        mBgColor = ContextCompat.getColor(App.get(), mPageStyle.getBgColor());
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

    public void setDisplayWidth(int displayWidth) {
        mDisplayWidth = displayWidth;
        mVisibleWidth = displayWidth - mMarginWidth * 2;
    }

    public int getDisplayHeight() {
        return mDisplayHeight;
    }

    public void setDisplayHeight(int displayHeight) {
        mDisplayHeight = displayHeight;
        mVisibleHeight = displayHeight - 2 * mMarginHeight;
    }

    public int getVisibleWidth() {
        return mVisibleWidth;
    }

    public void setVisibleWidth(int visibleWidth) {
        mVisibleWidth = visibleWidth;
    }

    public int getVisibleHeight() {
        return mVisibleHeight;
    }

    public void setVisibleHeight(int visibleHeight) {
        mVisibleHeight = visibleHeight;
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

    public static class Builder {

        private Context mContext;
        private PageMode mPageMode;
        private PageStyle mPageStyle;
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

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("context is null");
            }

            mContext = context;
            mPageMode = PageMode.valueOf(SP.getString(mContext, SPKey.KEY_PAGE_MODE, PageMode.COVER.name()));
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
        }

        public void setPageMode(PageMode pageMode) {
            if (pageMode != null) {
                SP.setParam(mContext, SPKey.KEY_PAGE_MODE, pageMode.name());
                mPageMode = pageMode;
            }
        }

        public void setPageStyle(PageStyle pageStyle) {
            if (pageStyle != null) {
                SP.setParam(mContext, SPKey.KEY_PAGE_MODE, pageStyle.name());
                pageStyle = pageStyle;
            }
        }

        public void setMarginWidth(int marginWidth) {
            if (marginWidth >= 0) {
                SP.putInt(mContext, SPKey.KEY_PAGE_MARGIN_WIDTH, marginWidth);
                mMarginWidth = marginWidth;
            }
        }

        public void setMarginHeight(int marginHeight) {
            if (marginHeight >= 0) {
                SP.putInt(mContext, SPKey.KEY_PAGE_MARGIN_HEIGHT, marginHeight);
                mMarginHeight = marginHeight;
            }
        }

        public void setTitleSize(int titleSize) {
            if (titleSize > 0) {
                SP.putInt(mContext, SPKey.KEY_PAGE_TITLE_SIZE, titleSize);
                mTitleSize = titleSize;
            }
        }

        public void setTextSize(int textSize) {
            if (textSize > 0) {
                SP.putInt(mContext, SPKey.KEY_PAGE_TEXT_SIZE, textSize);
                mTextSize = textSize;
            }
        }

        public void setTitlePara(int titlePara) {
            if (titlePara > 0) {
                SP.putInt(mContext, SPKey.KEY_PAGE_TITLE_PARA, titlePara);
                mTitlePara = titlePara;
            }
        }

        public void setTextPara(int textPara) {
            if (textPara > 0) {
                SP.putInt(mContext, SPKey.KEY_PAGE_TEXT_PARA, textPara);
                mTextPara = textPara;
            }
        }

        public void setTitleInterval(int titleInterval) {
            if (titleInterval > 0) {
                SP.putInt(mContext, SPKey.KEY_PAGE_TITLE_INTERVAL, titleInterval);
                mTitleInterval = titleInterval;
            }
        }

        public void setTextInterval(int textInterval) {
            if (textInterval > 0) {
                SP.putInt(mContext, SPKey.KEY_PAGE_TEXT_INTERVAL, textInterval);
                mTextInterval = textInterval;
            }
        }

        public PageConfig build() {
            return new PageConfig(this);
        }
    }
}
