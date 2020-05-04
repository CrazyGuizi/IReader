package com.ldg.ireader.bookshelf.core.draw;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

import com.ldg.common.util.ToastUtils;
import com.ldg.ireader.bookshelf.core.anim.CoverAnimation;
import com.ldg.ireader.bookshelf.core.anim.NonePageAnim;
import com.ldg.ireader.bookshelf.core.anim.PageAnimation;
import com.ldg.ireader.bookshelf.core.anim.ScrollAnimation;
import com.ldg.ireader.bookshelf.core.anim.SlideAnimation;
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
        public void pageCancel(boolean cancelNext) {
            // 取消翻页后把当前页修正
            if (cancelNext) {
                mPageLoader.getPrePage();
            } else {
                mPageLoader.getNextPage();
            }
            mReadPage.changeBitmap();
        }

        @Override
        public void onClickCenter() {
            ToastUtils.show(mReadPage.getContext(), "呼出菜单");
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
                if (mPageDrawHelper != null) {
                    mPageDrawHelper.drawPage(true);
                }
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
        if (mReadPage != null) {
            mReadPage.setCallback(null);
        }

        mReadPage = pageView;
        mReadPage.setCallback(new BasePageView.Callback() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                prepareDisplay();
            }

            @Override
            public void onDraw(Canvas canvas) {
                if (mPageAnimation != null) {
                    mPageAnimation.draw(canvas);
                }
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (mPageAnimation != null) {
                    return mPageAnimation.onTouchEvent(event);
                }
                return false;
            }

            @Override
            public void onDetachedFromWindow() {
                release();
            }
        });

        mPageDrawHelper = new PageDrawHelper(mReadPage, mPageLoader);
    }

    private PageAnimation getAnimation(int width, int height) {
        PageAnimation animation = null;
        if (width != 0 && height != 0) {
            switch (PageConfig.get().getPageMode()) {
                case COVER:
                    animation = new CoverAnimation(mReadPage, mPageAnimListener);
                    break;
                case SLIDE:
                    animation = new SlideAnimation(mReadPage, mPageAnimListener);
                    break;
                case SCROLL:
                    animation = new ScrollAnimation(mReadPage, mPageAnimListener);
                    break;
                case SIMULATION:
                    break;

                default:
                    animation = new NonePageAnim(mReadPage, mPageAnimListener);
                    break;
            }
        }
        return animation;
    }

    @Override
    public void prepareDisplay() {
        if (mReadPage == null) {
            throw new IllegalArgumentException("you must call attachView() first");
        }

        int width = mReadPage.getMeasuredWidth() - mReadPage.getPaddingLeft() - mReadPage.getPaddingRight();
        int height = mReadPage.getMeasuredHeight() - mReadPage.getPaddingTop() - mReadPage.getPaddingBottom();

        PageConfig.get().setDisplayWidth(width).setDisplayHeight(height);
        mPageAnimation = getAnimation(width, height);
        mPageDrawHelper.drawPage(true);
    }

    @Override
    public void setLoaderListener(PageLoader.PageLoaderListener loaderListener) {
        if (mPageLoader != null) {
            mPageLoader.setPageLoaderListener(loaderListener);
            mPageLoader.initData();
        }

        if (mReadPage == null) {
            throw new IllegalArgumentException("invoke attachView before");
        }
        mReadPage.requestLayout();
    }

    @Override
    public void release() {
        if (mPageLoader != null) {
            mPageLoader.saveDbCurProgress();
            mPageLoader.release();
        }

        if (mPageAnimation != null && mPageAnimation instanceof ScrollAnimation) {
            mPageAnimation.saveScrollProgress();
        }
    }

    @Override
    public void saveReadProgress() {
        if (mPageLoader != null) {
            mPageLoader.saveDbCurProgress();
        }

        if (mPageAnimation != null && mPageAnimation instanceof ScrollAnimation) {
            mPageAnimation.saveScrollProgress();
        }
    }
}
