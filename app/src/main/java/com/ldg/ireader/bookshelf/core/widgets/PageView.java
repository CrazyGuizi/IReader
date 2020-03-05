package com.ldg.ireader.bookshelf.core.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;

import com.ldg.common.log.LogUtil;
import com.ldg.ireader.bookshelf.core.config.PageConfig;
import com.ldg.ireader.bookshelf.core.draw.IPageController;

public class PageView extends BasePageView {


    private IPageController mPageController;
    private Rect mCenterArea;
    private int mDownX, mDownY;
    private ViewConfiguration mViewConfiguration;
    private int mTouchSlop;
    private boolean mCanMove;

    public PageView(Context context) {
        super(context);
    }

    public void setPageController(IPageController pageController) {
        mPageController = pageController;
        mPageController.attachView(this);
        requestLayout();
    }

    protected void initView() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mViewConfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = mViewConfiguration.getScaledTouchSlop();
        mCenterArea = new Rect();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterArea.set(mWidth / 5, 0, mWidth * 4 / 5, mHeight);
        if (mPageController != null) {
            mPageController.prepareDisplay(w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(PageConfig.get().getBgColor());
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
}
