package com.ldg.ireader.bookshelf.core.anim;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

import androidx.core.view.ViewCompat;

import com.ldg.ireader.bookshelf.core.widgets.BasePageView;

public abstract class HorizonPageAnim extends PageAnimation {

    public static final String TAG = HorizonPageAnim.class.getSimpleName();

    protected boolean mCanMove;
    // 检查是否还有上一页或者下一页
    protected boolean mCheckHasPage;
    // 取消翻页动作
    protected boolean mIsCancel;
    protected boolean mIsMoving;
    protected int mLastX;
    protected int mMoveX;
    protected boolean mIsToNext;
    protected ScrollRunnable mScrollRunnable;
    private boolean mHasPage;

    public HorizonPageAnim(BasePageView pageView, OnPageChangeListener listener) {
        super(pageView, listener);
        mScrollRunnable = new ScrollRunnable();
    }

    @Override
    public void draw(Canvas canvas) {
        if (mIsMoving) {
            drawMove(canvas);
        } else {
            drawStatic(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            if (mCheckHasPage && !mHasPage) {
                return false;
            }
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    return false;
                }

                mIsCancel = false;
                mCanMove = false;
                mIsMoving = false;
                mMoveX = 0;
                mLastX = x;
                mCheckHasPage = false;
                mHasPage = true;
                break;
            case MotionEvent.ACTION_MOVE:
                int diffX = x - mLastX;
                Log.d(TAG, "onTouchEvent: diffX" + diffX + "\tslop:"  + mTouchSlop);
                if (!mCanMove && Math.abs(diffX) >= mTouchSlop) {
                    mCanMove = true;
                }

                if (mCanMove) {
                    if (!mCheckHasPage) {
                        mCheckHasPage = true;
                        // 翻到下一页
                        mIsToNext = x - mLastX < 0;
                        mHasPage = mIsToNext ?
                                mListener.hasNext() : mListener.hasPrev();

                        Log.d("ldg", "onTouchEvent: hasPage" + mHasPage);
                        if (!mHasPage) {
                            return false;
                        }

                        if (mIsToNext) {
                            mMoveX = mViewWidth;
                        }
                    } else {
                        // 取消翻页
                        mIsCancel = mIsToNext ? diffX > 0 : diffX < 0;
                    }

                    mIsMoving = true;

                    mMoveX += diffX;
                    // 边界检查
                    mMoveX = Math.min(Math.max(0, mMoveX), mViewWidth);
//                    Log.d(TAG, "onTouchEvent: x" + mMoveX + "\tdiffX:" + diffX);
                }

                mLastX = x;
                mPageView.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (!mCanMove) {
                    if (mCenterClickArea.contains(x, y)) {
                        mListener.onClickCenter();
                        return false;
                    } else {
                        // 是否有上一页或下一页
                        mIsToNext = x > mCenterClickArea.right;
                        mHasPage = mIsToNext ?
                                mListener.hasNext() : mListener.hasPrev();

                        if (!mHasPage) {
                            return false;
                        }

                        if (mIsToNext) {
                            mMoveX = mViewWidth;
                        }
                    }
                }


                // 检查是否需要滑动
                int scrollDx;
                if (mIsCancel) {
                    // 本来是下一页，取消了
                    if (mIsToNext) {
                        scrollDx = mViewWidth - mMoveX;
                    } else {
                        scrollDx = 0 - mMoveX;
                    }
                } else {
                    if (mIsToNext) {
                        scrollDx = 0 - mMoveX;
                    } else {
                        scrollDx = mViewWidth - mMoveX;
                    }
                }

                if (scrollDx != 0) {
                    mScroller.startScroll(mMoveX, 0, scrollDx, 0);
                    ViewCompat.postOnAnimation(mPageView, mScrollRunnable);
                } else {
                    if (mIsCancel) {
                        mListener.pageCancel(mIsToNext);
                    }
                }

                mIsMoving = false;
                mCanMove = false;
                mPageView.invalidate();
                break;
        }

        return true;
    }

    public abstract void drawMove(Canvas canvas);

    public abstract void drawStatic(Canvas canvas);

    private class ScrollRunnable implements Runnable {

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {
                mIsMoving = true;
                mMoveX = mScroller.getCurrX();
                ViewCompat.postOnAnimation(mPageView, this);
            } else {
                mIsMoving = false;
                if (mIsCancel) {
                    mListener.pageCancel(mIsToNext);
                }
            }
            mPageView.invalidate();
        }
    }

}
