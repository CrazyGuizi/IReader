package com.ldg.ireader.bookshelf.core.anim;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.ldg.ireader.bookshelf.core.widgets.BasePageView;

public class SlideAnimation extends HorizonPageAnim {

    private final Rect srcCur;
    private final Rect dstCur;
    private final Rect srcNext;
    private final Rect dstNext;

    public SlideAnimation(BasePageView pageView, OnPageChangeListener listener) {
        super(pageView, listener);
        srcCur = new Rect();
        dstCur = new Rect();
        srcNext = new Rect();
        dstNext = new Rect();
    }

    @Override
    public void drawMove(Canvas canvas) {
        if (mIsToNext) {
            srcNext.set(0, 0, mViewWidth, mViewHeight);
            dstNext.set(mMoveX, 0, mViewWidth + mMoveX, mViewHeight);
            srcCur.set(0, 0, mViewWidth, mViewHeight);
            dstCur.set(-(mViewWidth - mMoveX), 0, mMoveX, mViewHeight);
        } else {
            srcCur.set(0, 0, mViewWidth, mViewHeight);
            dstCur.set(mMoveX, 0, mMoveX + mViewWidth, mViewHeight);
            srcNext.set(0, 0, mViewWidth, mViewHeight);
            dstNext.set(mMoveX - mViewWidth, 0, mMoveX, mViewHeight);
        }

        canvas.drawBitmap(mPageView.getCurBitmap(), srcCur, dstCur, null);
        canvas.drawBitmap(mPageView.getNextBitmap(), srcNext, dstNext, null);
    }

    @Override
    public void drawStatic(Canvas canvas) {
        canvas.drawBitmap(mPageView.getNextBitmap(), 0, 0, null);
    }
}
