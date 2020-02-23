package com.ldg.ireader.bookshelf.core.draw;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.ldg.ireader.bookshelf.core.anim.CoverAnimation;
import com.ldg.ireader.bookshelf.core.anim.PageAnimation;
import com.ldg.ireader.bookshelf.core.config.PageConfig;
import com.ldg.ireader.bookshelf.core.loader.LoadingStatus;
import com.ldg.ireader.bookshelf.core.loader.LocalPageLoader;
import com.ldg.ireader.bookshelf.core.loader.NetPageLoader;
import com.ldg.ireader.bookshelf.core.loader.OnLoadingListener;
import com.ldg.ireader.bookshelf.core.loader.PageLoader;
import com.ldg.ireader.bookshelf.core.widgets.PageView;
import com.ldg.ireader.bookshelf.model.BookModel;

public class ReadPageManager implements IPageController {

    // 阅读view
    private PageView mReadPage;
    private PageAnimation mPageAnimation;
    private PageLoader mPageLoader;
    private PageDrawHelper mPageDrawHelper;
    private PageConfig mPageConfig;

    // 动画监听类
    private PageAnimation.OnPageChangeListener mPageAnimListener = new PageAnimation.OnPageChangeListener() {
        @Override
        public boolean hasPrev() {
            return true;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public void pageCancel() {
        }
    };

    public ReadPageManager(BookModel bookModel) {
        if (bookModel == null) {
            throw new IllegalArgumentException("the book must not be null");
        }

        mPageLoader = getPageLoader(bookModel);
        mPageLoader.setOnLoadingListener(new OnLoadingListener() {
            @Override
            public void updateStatus(LoadingStatus status) {
                mPageDrawHelper.setStatus(status);
                mPageDrawHelper.drawPage(mPageAnimation.getNextBitmap(), false);
            }
        });
    }

    private PageLoader getPageLoader(BookModel bookModel) {
        PageLoader pageLoader;
        if (bookModel.isLocal()) {
            pageLoader = new LocalPageLoader(bookModel);
        } else {
            pageLoader = new NetPageLoader(bookModel);
        }
        return pageLoader;
    }

    public void attachView(PageView pageView) {
        if (pageView == null) {
            return;
        }
        mReadPage = pageView;
        mPageConfig = new PageConfig.Builder(mReadPage.getContext()).build();
        mPageDrawHelper = new PageDrawHelper(mReadPage, mPageConfig);
        mPageDrawHelper.setPageLoader(mPageLoader);
        mPageLoader.setPageConfig(mPageConfig);
    }

    private PageAnimation getAnimation(int width, int height) {
        PageAnimation animation = null;
        if (width != 0 && height != 0) {
            switch (mPageConfig.getPageMode()) {
                case COVER:
                    animation = new CoverAnimation(width, height,
                            mReadPage, mPageAnimListener);
                    break;
                case SLIDE:
                    break;
                case SCROLL:
                    break;
                case SIMULATION:
                    break;

                default:

                    break;
            }
        }
        return animation;
    }

    @Override
    public void prepareDisplay(int pageWidth, int pageHeight) {
        if (pageHeight == 0 || pageHeight == 0) {
            return;
        }
        mPageAnimation = getAnimation(pageWidth, pageHeight);
        mPageDrawHelper.setDisplaySize(pageWidth, pageHeight);
        mPageDrawHelper.drawPage(mPageAnimation.getNextBitmap(), false);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mPageAnimation != null) {
            mPageAnimation.draw(canvas);
        }
    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }

    @Override
    public void onClickPageCenter(MotionEvent event) {

    }
}
