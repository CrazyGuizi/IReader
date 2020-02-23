package com.ldg.ireader.bookshelf.core.anim;

import android.graphics.Canvas;
import android.view.View;

public class NonePageAnim extends HorizonPageAnim {

    public NonePageAnim(int w, int h, View view, OnPageChangeListener listener) {
        super(w, h, view, listener);
    }

    public NonePageAnim(int w, int h, int marginWidth, int marginHeight, View view, OnPageChangeListener listener) {
        super(w, h, marginWidth, marginHeight, view, listener);
    }

    @Override
    public void drawMove(Canvas canvas) {
        if (mIsCancel) {
            canvas.drawBitmap(mCurBitmap, 0, 0, null);
        } else {
            canvas.drawBitmap(mNextBitmap, 0, 0, null);
        }
    }

    @Override
    public void drawStatic(Canvas canvas) {
        if (mIsCancel) {
            canvas.drawBitmap(mCurBitmap, 0, 0, null);
        } else {
            canvas.drawBitmap(mNextBitmap, 0, 0, null);
        }
    }
}
