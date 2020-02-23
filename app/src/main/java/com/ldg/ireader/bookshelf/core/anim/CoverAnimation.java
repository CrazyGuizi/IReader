package com.ldg.ireader.bookshelf.core.anim;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.ldg.ireader.utils.ScreenUtils;

public class CoverAnimation extends HorizonPageAnim {

    private GradientDrawable mShapeDrawable;

    public CoverAnimation(int w, int h, View view, OnPageChangeListener listener) {
        this(w, h, 0, 0, view, listener);
    }

    public CoverAnimation(int w, int h, int marginWidth, int marginHeight, View view, OnPageChangeListener listener) {
        super(w, h, marginWidth, marginHeight, view, listener);

        mShapeDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{0x66000000, 0x00000000});
    }

    @Override
    public void drawMove(Canvas canvas) {

        Rect nextSrc = new Rect(mLastX, 0, mViewWidth, mViewHeight);
        Rect curSrc = new Rect(mViewWidth - mLastX, 0, mViewWidth, mViewHeight);
        Rect curDst = new Rect(0, 0, mLastX, mViewHeight);
        canvas.drawBitmap(mNextBitmap, nextSrc, nextSrc, null);
        canvas.drawBitmap(mCurBitmap, curSrc, curDst, null);
        mShapeDrawable.setBounds(mLastX, 0, mLastX + ScreenUtils.dp2px(12), mViewHeight);
        mShapeDrawable.draw(canvas);
    }

    @Override
    public void drawStatic(Canvas canvas) {
        if (mIsCancel) {
            mNextBitmap = mCurBitmap.copy(Bitmap.Config.RGB_565, true);
            canvas.drawBitmap(mCurBitmap, 0, 0, null);
        } else {
            canvas.drawBitmap(mNextBitmap, 0, 0, null);
        }

    }
}
