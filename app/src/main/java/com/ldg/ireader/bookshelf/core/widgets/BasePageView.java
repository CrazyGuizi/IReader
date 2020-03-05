package com.ldg.ireader.bookshelf.core.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.ldg.ireader.bookshelf.core.draw.IPageController;

public abstract class BasePageView extends View {

    protected int mWidth, mHeight;
    protected Bitmap mCurBitmap, mNextBitmap;

    protected IPageController mPageController;

    public BasePageView(Context context) {
        this(context, null);
    }

    public BasePageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasePageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected abstract void initView();

    public void setCurBitmap(Bitmap curBitmap) {
        mCurBitmap = curBitmap;
    }

    public void setNextBitmap(Bitmap nextBitmap) {
        mNextBitmap = nextBitmap;
    }

    public Bitmap getCurBitmap() {
        return mCurBitmap;
    }

    public Bitmap getNextBitmap() {
        return mNextBitmap;
    }

    public void changeBitmap() {
        Bitmap change = mCurBitmap;
        mCurBitmap = mNextBitmap;
        mNextBitmap = change;
    }

    public void setPageController(IPageController pageController) {
        mPageController = pageController;
        mPageController.attachView(this);
        requestLayout();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mWidth == w && mHeight == h) {
            return;
        }

        mWidth = w;
        mHeight = h;
        if (mCurBitmap == null) {
            mCurBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
        } else {
            mCurBitmap = Bitmap.createScaledBitmap(mCurBitmap, mWidth, mHeight, false);
        }

        if (mNextBitmap == null) {
            mNextBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
        } else {
            mNextBitmap = Bitmap.createScaledBitmap(mNextBitmap, mWidth, mHeight, false);
        }
    }
}
