package com.ldg.ireader.bookshelf.core.anim;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.ldg.ireader.bookshelf.core.widgets.BasePageView;
import com.ldg.ireader.utils.ScreenUtils;

public class CoverAnimation extends HorizonPageAnim {

    private GradientDrawable mShapeDrawable;

    public CoverAnimation(int w, int h, BasePageView view, OnPageChangeListener listener) {
        super(w, h, view, listener);
    }

    public CoverAnimation(int w, int h, int marginWidth, int marginHeight, BasePageView view, OnPageChangeListener listener) {
        super(w, h, marginWidth, marginHeight, view, listener);
        mShapeDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{0x66000000, 0x00000000});
    }


    @Override
    public void drawMove(Canvas canvas) {

        Rect nextSrc = new Rect(mLastX, 0, mViewWidth, mViewHeight);
        Rect curSrc = new Rect(mViewWidth - mLastX, 0, mViewWidth, mViewHeight);
        Rect curDst = new Rect(0, 0, mLastX, mViewHeight);
        canvas.drawBitmap(mPageView.getNextBitmap(), nextSrc, nextSrc, null);
        canvas.drawBitmap(mPageView.getCurBitmap(), curSrc, curDst, null);
        mShapeDrawable.setBounds(mLastX, 0, mLastX + ScreenUtils.dp2px(12), mViewHeight);
        mShapeDrawable.draw(canvas);
    }

    @Override
    public void drawStatic(Canvas canvas) {
        if (mIsCancel) {
            mPageView.setNextBitmap(mPageView.getCurBitmap().copy(Bitmap.Config.RGB_565, true));
            canvas.drawBitmap(mPageView.getCurBitmap(), 0, 0, null);
        } else {
            canvas.drawBitmap(mPageView.getNextBitmap(), 0, 0, null);
        }

    }
}
