package com.ldg.ireader.bookshelf.core.draw;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.ldg.ireader.bookshelf.core.loader.PageLoader;
import com.ldg.ireader.bookshelf.core.widgets.BasePageView;
import com.ldg.ireader.bookshelf.core.widgets.PageView;
import com.ldg.ireader.bookshelf.model.BookModel;

public interface IPageController {

    void attachView(BasePageView pageView);

    void prepareDisplay();

    void setLoaderListener(PageLoader.PageLoaderListener loaderListener);
}
