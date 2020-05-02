package com.ldg.ireader.bookshelf.core.anim;

import android.graphics.Canvas;
import android.view.View;

import com.ldg.ireader.bookshelf.core.widgets.BasePageView;

public class NonePageAnim extends HorizonPageAnim {

    public NonePageAnim(BasePageView pageView, OnPageChangeListener listener) {
        super(pageView, listener);
    }

    @Override
    public void drawMove(Canvas canvas) {
        canvas.drawBitmap(mPageView.getNextBitmap(), 0, 0, null);
    }

    @Override
    public void drawStatic(Canvas canvas) {
        canvas.drawBitmap(mPageView.getNextBitmap(), 0, 0, null);
    }
}
