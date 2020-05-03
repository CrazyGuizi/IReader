package com.ldg.ireader.bookshelf.core.anim;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.ldg.ireader.bookshelf.core.widgets.BasePageView;

public abstract class PageAnimation {

    protected int mViewWidth;
    protected int mViewHeight;
    protected int mTouchSlop;

    //正在使用的View
    protected BasePageView mPageView;

    //滑动装置
    protected Scroller mScroller;
    //监听器
    protected OnPageChangeListener mListener;
    protected RectF mCenterClickArea;


    public PageAnimation(BasePageView pageView, OnPageChangeListener listener) {
        if (pageView == null) {
            throw new IllegalArgumentException("the PageView must not be null");
        }

        if (listener == null) {
            throw new IllegalArgumentException("the OnPageChangeListener must not be null");
        }

        mPageView = pageView;
        mListener = listener;
        mViewWidth = pageView.getMeasuredWidth();
        mViewHeight = pageView.getMeasuredHeight();
        mCenterClickArea = new RectF(1F * mViewWidth / 3, 0, 2F * mViewWidth / 3, mViewHeight);

        mScroller = new Scroller(mPageView.getContext(), new LinearInterpolator());

        mTouchSlop = ViewConfiguration.get(mPageView.getContext()).getScaledTouchSlop();
    }

    public abstract void draw(Canvas canvas);

    public abstract boolean onTouchEvent(MotionEvent event);

    public interface OnPageChangeListener {
        boolean hasPrev();

        boolean hasNext();

        /**
         * 取消翻页
         *
         * @param cancelNext 取消翻下一页
         */
        void pageCancel(boolean cancelNext);

        void onClickCenter();
    }
}
