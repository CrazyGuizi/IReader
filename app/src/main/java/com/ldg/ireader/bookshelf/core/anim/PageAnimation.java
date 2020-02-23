package com.ldg.ireader.bookshelf.core.anim;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

public abstract class PageAnimation {

    //屏幕的尺寸
    protected int mScreenWidth;
    protected int mScreenHeight;
    //屏幕的间距
    protected int mMarginWidth;
    protected int mMarginHeight;
    //视图的尺寸
    protected int mViewWidth;
    protected int mViewHeight;
    protected int mTouchSlop;

    //正在使用的View
    protected View mView;

    //滑动装置
    protected Scroller mScroller;
    //监听器
    protected OnPageChangeListener mListener;

    public PageAnimation(int w, int h, View view, OnPageChangeListener listener){
        this(w, h, 0, 0, view,listener);
    }

    public PageAnimation(int w, int h, int marginWidth, int marginHeight, View view, OnPageChangeListener listener) {
        mScreenWidth = w;
        mScreenHeight = h;

        mMarginWidth = marginWidth;
        mMarginHeight = marginHeight;

        mViewWidth = mScreenWidth - mMarginWidth * 2;
        mViewHeight = mScreenHeight - mMarginHeight * 2;

        mView = view;
        mListener = listener;

        mScroller = new Scroller(mView.getContext(), new LinearInterpolator());

        mTouchSlop = ViewConfiguration.get(mView.getContext()).getScaledTouchSlop();
    }

    public abstract void draw(Canvas canvas);

    public abstract Bitmap getNextBitmap();

    public abstract Bitmap getBgBitmap();

    public abstract void onTouchEvent(MotionEvent event);

    public interface OnPageChangeListener {
        boolean hasPrev();
        boolean hasNext();
        void pageCancel();
    }
}
