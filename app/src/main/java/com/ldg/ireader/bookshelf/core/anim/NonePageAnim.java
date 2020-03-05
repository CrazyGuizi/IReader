package com.ldg.ireader.bookshelf.core.anim;

import android.graphics.Canvas;
import android.view.View;

import com.ldg.ireader.bookshelf.core.widgets.BasePageView;

public class NonePageAnim extends HorizonPageAnim {

    public NonePageAnim(int w, int h, BasePageView view, OnPageChangeListener listener) {
        super(w, h, view, listener);
    }

    public NonePageAnim(int w, int h, int marginWidth, int marginHeight, BasePageView view, OnPageChangeListener listener) {
        super(w, h, marginWidth, marginHeight, view, listener);
    }

    @Override
    public void drawMove(Canvas canvas) {
        if (mIsCancel) {
            canvas.drawBitmap(mPageView.getCurBitmap(), 0, 0, null);
        } else {
            canvas.drawBitmap(mPageView.getNextBitmap(), 0, 0, null);
        }
    }

    @Override
    public void drawStatic(Canvas canvas) {
        if (mIsCancel) {
            canvas.drawBitmap(mPageView.getCurBitmap(), 0, 0, null);
        } else {
            canvas.drawBitmap(mPageView.getNextBitmap(), 0, 0, null);
        }
    }
}
