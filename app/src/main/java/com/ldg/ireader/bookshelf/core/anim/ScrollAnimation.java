package com.ldg.ireader.bookshelf.core.anim;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import androidx.core.view.ViewCompat;

import com.ldg.common.util.SP;
import com.ldg.ireader.bookshelf.core.widgets.BasePageView;
import com.ldg.ireader.utils.SPKey;

public class ScrollAnimation extends PageAnimation {

    public static final String TAG = ScrollAnimation.class.getSimpleName();
    private VelocityTracker mVelocityTracker;

    private int mScrollY;
    private boolean mHasPage;
    private int mLastY;
    private boolean mToNext, mToPre;
    private FilingRunnable mFilingRunnable;
    private int mMinVelocity;
    private int mDownX, mDownY;

    public ScrollAnimation(BasePageView pageView, OnPageChangeListener listener) {
        super(pageView, listener);
        getSaveScrollY();

        mRedCurPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRedCurPaint.setStyle(Paint.Style.STROKE);
        mRedCurPaint.setStrokeWidth(12);
        mRedCurPaint.setColor(Color.RED);

        mBlueNextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlueNextPaint.setStyle(Paint.Style.STROKE);
        mBlueNextPaint.setStrokeWidth(12);
        mBlueNextPaint.setColor(Color.BLUE);
        mVelocityTracker = VelocityTracker.obtain();
        mMinVelocity = ViewConfiguration.get(mPageView.getContext()).getScaledMinimumFlingVelocity();
    }

    public void getSaveScrollY() {
//        mScrollY = SP.getInt(mPageView.getContext(), SPKey.KEY_SCROLL_Y, 0);
        Log.d(TAG, "getSaveScrollY: " + mScrollY);
    }

    @Override
    public void saveScrollProgress() {
        SP.putInt(mPageView.getContext(), SPKey.KEY_SCROLL_Y, mScrollY);
        Log.d(TAG, "saveScrollProgress: " + mScrollY);
    }

    private Paint mRedCurPaint;
    private Paint mBlueNextPaint;

    @Override
    public void draw(Canvas canvas) {
        if (!mToPre) {
            srcCur.set(0, 0, mViewWidth, mViewHeight);
            dstCur.set(0, -(mViewHeight - mScrollY), mViewWidth, mScrollY);
            canvas.drawBitmap(mPageView.getCurBitmap(), srcCur, dstCur, null);

            srcNext.set(0, 0, mViewWidth, mViewHeight);
            dstNext.set(0, mScrollY, mViewWidth, mScrollY + mViewHeight);
            canvas.drawBitmap(mPageView.getNextBitmap(), srcNext, dstNext, null);
        } else {
            srcNext.set(0, 0, mViewWidth, mViewHeight);
            dstNext.set(0, -(mViewHeight - mScrollY), mViewWidth, mScrollY);
            canvas.drawBitmap(mPageView.getNextBitmap(), srcNext, dstNext, null);

            srcCur.set(0, 0, mViewWidth, mViewHeight);
            dstCur.set(0, mScrollY, mViewWidth, mScrollY + mViewHeight);
            canvas.drawBitmap(mPageView.getCurBitmap(), srcCur, dstCur, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        int x = (int) event.getX();

        ensureVelocity(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    mDownX = Integer.MAX_VALUE;
                    mDownY = Integer.MAX_VALUE;
                    // 如果是滑动停止，则不考虑点击中间事件
                } else {
                    mDownX = x;
                    mDownY = y;
                }
                mHasPage = false;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int diffY = y - mLastY;
                if (!handleScroll(diffY)) {
                    Log.d(TAG, "onTouchEvent: 不滑动move");
                    return false;
                }

                Log.d(TAG, "onTouchEvent: 滑动move");

                mLastY = y;
                mPageView.invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.computeCurrentVelocity(1000);
                int yVelocity = (int) mVelocityTracker.getYVelocity();
                releaseVelocity();

                if (Math.abs(yVelocity) > Math.abs(mMinVelocity)) {
                    if (mFilingRunnable == null) {
                        mFilingRunnable = new FilingRunnable();
                    }

                    mScroller.fling(0, 0, 0, yVelocity, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    ViewCompat.postOnAnimation(mPageView, mFilingRunnable);
                } else {
                    int dx = mDownX - x;
                    int dy = mDownY - y;
                    if (dx * dx + dy * dy < mTouchSlop * mTouchSlop
                            && mCenterClickArea.contains(x, y)) {
                        mListener.onClickCenter();
                    }
//                    Log.d(TAG, "onTouchEvent: 不滑动up");
                }
                break;
        }

        return true;
    }

    private void ensureVelocity(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocity() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }


    private class FilingRunnable implements Runnable {
        private int mLastFlingY;

        public FilingRunnable() {
            mLastFlingY = 0;
        }

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {
                int y = mScroller.getCurrY();
                int diffY = y - mLastFlingY;
//                Log.d(TAG, "run: " + y + "\tdiffY:" + diffY);
                if (!handleScroll(diffY)) return;

                Log.d(TAG, "run: ScrollY" + mScrollY);
                mLastFlingY = y;
                mPageView.invalidate();
                ViewCompat.postOnAnimation(mPageView, this);
            } else {
                mLastFlingY = 0;
            }
        }
    }

    /**
     * 处理移动或者惯性滑动
     *
     * @param diffY
     * @return
     */
    private boolean handleScroll(int diffY) {
        if (mScrollY == mViewHeight && diffY < 0) {
            mScrollY = 0;
        } else if (mScrollY == 0 && diffY > 0) {
            mScrollY = mViewHeight;
        }

        int newScrollY = mScrollY + diffY;
        if (newScrollY < 0) {
            if (mToPre) {
                mListener.pageCancel(false);
                mToPre = false;
            }
            mHasPage = mListener.hasNext();
            if (!mHasPage) {
                mScrollY = mViewHeight;
                mToNext = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                return false;
            }
            mScrollY = mViewHeight + newScrollY;
            mToNext = true;
        } else if (newScrollY > mViewHeight) {
            if (mToNext) {
                mListener.pageCancel(true);
//                Log.d(TAG, "onTouchEvent: 恢复上一页");
                mToNext = false;
            }
            mHasPage = mListener.hasPrev();
//            Log.d(TAG, "onTouchEvent: 上一页" + mHasPage);
            if (!mHasPage) {
                mScrollY = 0;
                mToPre = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                return false;
            }
            mScrollY = newScrollY - mViewHeight;
            mToPre = true;
        } else {
            mScrollY = newScrollY;
        }
        return true;
    }
}
