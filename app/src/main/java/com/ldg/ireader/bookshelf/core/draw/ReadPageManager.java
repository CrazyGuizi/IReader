package com.ldg.ireader.bookshelf.core.draw;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.ldg.ireader.bookshelf.core.anim.CoverAnimation;
import com.ldg.ireader.bookshelf.core.anim.NonePageAnim;
import com.ldg.ireader.bookshelf.core.anim.PageAnimation;
import com.ldg.ireader.bookshelf.core.config.PageConfig;
import com.ldg.ireader.bookshelf.core.loader.LoadingStatus;
import com.ldg.ireader.bookshelf.core.loader.LocalPageLoader;
import com.ldg.ireader.bookshelf.core.loader.NetPageLoader;
import com.ldg.ireader.bookshelf.core.loader.OnLoadingListener;
import com.ldg.ireader.bookshelf.core.loader.PageLoader;
import com.ldg.ireader.bookshelf.core.widgets.BasePageView;
import com.ldg.ireader.bookshelf.model.BookModel;

public class ReadPageManager implements IPageController {

    // 阅读view
    private BasePageView mReadPage;
    private PageAnimation mPageAnimation;
    private PageLoader mPageLoader;
    private PageDrawHelper mPageDrawHelper;

    // 动画监听类
    private PageAnimation.OnPageChangeListener mPageAnimListener = new PageAnimation.OnPageChangeListener() {
        @Override
        public boolean hasPrev() {
            return mPageDrawHelper.drawPrePage(true);
        }

        @Override
        public boolean hasNext() {
            return mPageDrawHelper.drawNextPage(true);
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
                mPageDrawHelper.drawPage(true);
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

    public void attachView(BasePageView pageView) {
        if (pageView == null) {
            return;
        }
        mReadPage = pageView;
        mPageDrawHelper = new PageDrawHelper(mReadPage, mPageLoader);
    }

    private PageAnimation getAnimation(int width, int height) {
        PageAnimation animation = null;
        if (width != 0 && height != 0) {
            switch (PageConfig.get().getPageMode()) {
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
                    animation = new NonePageAnim(width, height, mReadPage, mPageAnimListener);
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
        PageConfig.get().setDisplayWidth(pageWidth).setDisplayHeight(pageHeight);
        mPageAnimation = getAnimation(pageWidth, pageHeight);
        mPageLoader.initData();
        mPageDrawHelper.drawPage(true);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mPageAnimation != null) {
            mPageAnimation.draw(canvas);
        }
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (mPageAnimation != null) {
            mPageAnimation.onTouchEvent(event);
        }
    }

    @Override
    public void onClickPageCenter(MotionEvent event) {

    }
}
