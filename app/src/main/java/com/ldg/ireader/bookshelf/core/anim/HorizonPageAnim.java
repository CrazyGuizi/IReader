package com.ldg.ireader.bookshelf.core.anim;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import com.ldg.common.log.LogUtil;

public abstract class HorizonPageAnim extends PageAnimation {
    protected boolean mIsRuning;
    protected boolean mIsCancel;
    protected boolean mIsMoving;
    protected Bitmap mNextBitmap;
    protected Bitmap mCurBitmap;
    protected int mLastX, mLastY;
    protected int mMoveX, mMoveY;
    private boolean mHasPrev, mHasNext, mIsToNext;


    public HorizonPageAnim(int w, int h, View view, OnPageChangeListener listener) {
        this(w, h, 0, 0, view, listener);
    }

    public HorizonPageAnim(int w, int h, int marginWidth, int marginHeight, View view, OnPageChangeListener listener) {
        super(w, h, marginWidth, marginHeight, view, listener);

        mCurBitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.RGB_565);
        mNextBitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.RGB_565);
    }

    public void changePage() {
        Bitmap bitmap = mNextBitmap;
        mNextBitmap = mCurBitmap;
        mCurBitmap = bitmap;
    }


    @Override
    public void draw(Canvas canvas) {
        if (mIsRuning) {
            drawMove(canvas);
        } else {
            if (mIsCancel) {
                mNextBitmap = mCurBitmap.copy(Bitmap.Config.RGB_565, true);
            }
            drawStatic(canvas);
        }
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsRuning = false;
                mIsMoving = false;
                mMoveX = 0;
                mMoveY = 0;
                LogUtil.d("down");
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mIsMoving && Math.abs(x - mLastX) >= mTouchSlop) {
                    mIsMoving = true;
                }

                if (mIsMoving) {
                    if (mMoveX == 0 && mMoveY == 0) {
                        if (mIsToNext = (x - mLastX < 0)) {
                            mHasNext = mListener.hasNext();
                            if (!mHasNext) {
                                return;
                            }
                        } else {
                            mHasPrev = mListener.hasPrev();
                            if (!mHasPrev) {
                                return;
                            }
                        }
                    } else {
                        mIsCancel = mIsToNext ? x - mMoveX > 0 : x - mMoveX < 0;
                    }
                }
                mIsRuning = true;
                mMoveX = x;
                mMoveY = y;
                mView.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (!mIsMoving) {
                    mIsToNext = x > mViewWidth / 2;

                    if (mIsToNext) {
                        mListener.hasNext();
                    } else {
                        mListener.hasPrev();
                    }
                }

                mIsRuning = false;
                mMoveX = 0;
                mMoveY = 0;
                mView.invalidate();
                break;

        }

        mLastX = x;
        mLastY = y;

    }

    @Override
    public Bitmap getBgBitmap() {
        return mNextBitmap;
    }

    @Override
    public Bitmap getNextBitmap() {
        return mNextBitmap;
    }

    public abstract void drawMove(Canvas canvas);

    public abstract void drawStatic(Canvas canvas);


}
