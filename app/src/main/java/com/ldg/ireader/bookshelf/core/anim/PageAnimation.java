package com.ldg.ireader.bookshelf.core.anim;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.ldg.ireader.bookshelf.core.widgets.BasePageView;

public abstract class PageAnimation {

//    //屏幕的尺寸
//    protected int mScreenWidth;
//    protected int mScreenHeight;
//    //屏幕的间距
//    protected int mMarginWidth;
//    protected int mMarginHeight;
    //视图的尺寸
    protected int mViewWidth;
    protected int mViewHeight;
    protected int mTouchSlop;

    //正在使用的View
    protected BasePageView mPageView;

    //滑动装置
    protected Scroller mScroller;
    //监听器
    protected OnPageChangeListener mListener;

    public PageAnimation(BasePageView pageView, OnPageChangeListener listener){
//        mScreenWidth = w;
//        mScreenHeight = h;
//
//        mMarginWidth = marginWidth;
//        mMarginHeight = marginHeight;

//        mViewWidth = mScreenWidth - mMarginWidth * 2;
//        mViewHeight = mScreenHeight - mMarginHeight * 2;

        if (pageView == null) {
            return;
        }

        mViewWidth = pageView.getMeasuredWidth();
        mViewHeight = pageView.getMeasuredHeight();

        mPageView = pageView;
        mListener = listener;

        mScroller = new Scroller(mPageView.getContext(), new LinearInterpolator());

        mTouchSlop = ViewConfiguration.get(mPageView.getContext()).getScaledTouchSlop();
    }

    public abstract void draw(Canvas canvas);

    public abstract boolean onTouchEvent(MotionEvent event);

    public interface OnPageChangeListener {
        boolean hasPrev();
        boolean hasNext();
        void pageCancel();
    }
}
