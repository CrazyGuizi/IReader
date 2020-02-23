package com.ldg.ireader.bookshelf.core.draw;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.ldg.ireader.bookshelf.core.loader.PageLoader;
import com.ldg.ireader.bookshelf.core.widgets.PageView;
import com.ldg.ireader.bookshelf.model.BookModel;

public interface IPageController {

    void attachView(PageView pageView);

    void prepareDisplay(int pageWidth, int pageHeight);

    /**
     * PageView onDraw回调
     *
     * @param canvas
     */
    void onDraw(Canvas canvas);

    void onTouchEvent(MotionEvent event);

    void onClickPageCenter(MotionEvent event);
}
