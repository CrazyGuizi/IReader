package com.ldg.ireader.bookshelf.core.anim;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;

import com.ldg.ireader.bookshelf.core.widgets.BasePageView;
import com.ldg.ireader.utils.ScreenUtils;

public class CoverAnimation extends HorizonPageAnim {

    private GradientDrawable mShapeDrawable;

    public CoverAnimation(BasePageView pageView, OnPageChangeListener listener) {
        super(pageView, listener);
        mShapeDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{0x66000000, 0x00000000});
    }

    @Override
    public void drawMove(Canvas canvas) {
        Rect srcCur = new Rect();
        Rect dstCur = new Rect();
        Rect srcNext = new Rect();
        Rect dstNext = new Rect();
        if (mIsToNext) {
            srcNext.set(0, 0, mViewWidth, mViewHeight);
            dstNext.set(0, 0, mViewWidth, mViewHeight);
            srcCur.set(0, 0, mViewWidth, mViewHeight);
            dstCur.set(mMoveX - mViewWidth, 0, mMoveX, mViewHeight);
            canvas.drawBitmap(mPageView.getNextBitmap(), srcNext, dstNext, null);
            canvas.drawBitmap(mPageView.getCurBitmap(), srcCur, dstCur, null);
        } else {
            srcCur.set(0, 0, mViewWidth, mViewHeight);
            dstCur.set(0, 0, mViewWidth, mViewHeight);
            srcNext.set(0, 0, mViewWidth, mViewHeight);
            dstNext.set(mMoveX - mViewWidth, 0, mMoveX, mViewHeight);
            canvas.drawBitmap(mPageView.getCurBitmap(), srcCur, dstCur, null);
            canvas.drawBitmap(mPageView.getNextBitmap(), srcNext, dstNext, null);
        }

        mShapeDrawable.setBounds(mMoveX, 0, mMoveX + ScreenUtils.dp2px(12), mViewHeight);
        mShapeDrawable.draw(canvas);
    }

    @Override
    public void drawStatic(Canvas canvas) {
        canvas.drawBitmap(mPageView.getNextBitmap(), 0, 0, null);
    }
}
