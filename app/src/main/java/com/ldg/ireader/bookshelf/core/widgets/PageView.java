package com.ldg.ireader.bookshelf.core.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;

import com.ldg.common.log.LogUtil;
import com.ldg.ireader.bookshelf.core.draw.IPageController;

public class PageView extends View {

    private int mWidth, mHeight;
    private IPageController mPageController;
    private int mBgColor = 0xFFCEC29C;
    private Rect mCenterArea;
    private int mDownX, mDownY;
    private ViewConfiguration mViewConfiguration;
    private int mTouchSlop;
    private boolean mCanMove;

    public void setPageController(IPageController pageController) {
        mPageController = pageController;
        mPageController.attachView(this);
        requestLayout();
    }

    //
//    // 动画监听类
//    private PageAnimation.OnPageChangeListener mPageAnimListener = new PageAnimation.OnPageChangeListener() {
//        @Override
//        public boolean hasPrev() {
//            return PageView.this.hasPrevPage();
//        }
//
//        @Override
//        public boolean hasNext() {
//            return PageView.this.hasNextPage();
//        }
//
//        @Override
//        public void pageCancel() {
//            PageView.this.pageCancel();
//        }
//    };
//
//    private void pageCancel() {
//
//    }
//
//    private boolean hasNextPage() {
//        return mPageLoader.drawNext(getNextBitmap(), false);
//    }
//
//    private boolean hasPrevPage() {
//        return mPageLoader.drawPre(getNextBitmap(), false);
//    }

    public PageView(Context context) {
        this(context, null);
    }

    public PageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mViewConfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = mViewConfiguration.getScaledTouchSlop();
        LogUtil.d("" + mTouchSlop);
        mCenterArea = new Rect();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mWidth == w && mHeight == h) {
            return;
        }

        mWidth = w;
        mHeight = h;
        mCenterArea.set(mWidth / 5, 0, mWidth * 4 / 5, mHeight);
        if (mPageController != null) {
            mPageController.prepareDisplay(w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(mBgColor);
        if (mPageController != null) {
            mPageController.onDraw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) x;
                mDownY = (int) y;
                mCanMove = false;
                if (mPageController != null) {
                    mPageController.onTouchEvent(event);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (!mCanMove && (Math.abs(x - mDownX) >= mTouchSlop
                        || Math.abs(y - mDownY) >= mTouchSlop)) {
                    mCanMove = true;
                }

                if (mCanMove) {
                    if (mPageController != null) {
                        mPageController.onTouchEvent(event);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mCanMove) {
                    if (mCenterArea.contains(mDownX, mDownY)) {
                        if (mPageController != null) {
                            mPageController.onClickPageCenter(event);
                        }
                    } else {
                        if (mPageController != null) {
                            mPageController.onTouchEvent(event);
                        }
                    }
                } else {
                    if (mPageController != null) {
                        mPageController.onTouchEvent(event);
                    }
                }
                mCanMove = false;
                break;
        }
        return true;
    }
//
//    public boolean isInCenter(int x, int y) {
//        return mCenterArea != null && mCenterArea.contains(x, y);
//    }
//
//    public PageLoader getPageLoader(BookModel book) {
//        if (mPageLoader != null) {
//            return mPageLoader;
//        }
//
//        mPageLoader = new NetPageLoader(this, book);
//
//        // 判断是否 PageView 已经初始化完成
//        if (mWidth != 0 || mHeight != 0) {
//            // 初始化 PageLoader 的屏幕大小
//            mPageLoader.prepareDisplay(mWidth, mHeight);
//        }
//
//        return mPageLoader;
//    }

//    public void changePage() {
//        if (mPageAnim != null && mPageAnim instanceof HorizonPageAnim) {
//            ((HorizonPageAnim) mPageAnim).changePage();
//        }
//    }
}
