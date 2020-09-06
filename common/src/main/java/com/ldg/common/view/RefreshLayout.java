package com.ldg.common.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;

import com.ldg.common.log.LogUtil;
import com.ldg.common.util.ToastUtils;
import com.ldg.common.view.refresh.IRefreshHeader;
import com.ldg.common.view.refresh.header.DRefreshHeader;

public class RefreshLayout extends ViewGroup implements NestedScrollingParent3 {

    private static final String TAG = RefreshLayout.class.getSimpleName();
    private IRefreshHeader mRefreshHeader;
    private View mHeaderView;
    private View mContentView;
    private View mTailView;
    private NestedScrollingParentHelper mParentHelper;

    private int mTouchSlop;

    private int mTotalDragDistance;
    private int mOriginOffsetTop;
    private int mMaxOffset;
    private int mCurOffsetTop;
    private int mInitDownY;
    private int mLastY;
    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;

    private boolean mReturningToStart;
    private boolean mRefreshing;
    private boolean mIsBeginingDrag;


    private RefreshListener mRefreshListener;
    private boolean mNotify;

    private int mFrom;
    private Interpolator mDecelerateInterpolator = new DecelerateInterpolator(2F);

    private Animation.AnimationListener mRefreshAnimListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            if (mRefreshing) {
                mRefreshHeader.onRefreshStart();
            } else {
                mRefreshHeader.onRefreshFinish();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                if (mNotify && mRefreshListener != null) {
                    mRefreshListener.onRefresh();
                }
            } else {
                mRefreshHeader.onRefreshEnd();
                mReturningToStart = false;
            }
        }
    };

    public void setRefreshListener(RefreshListener refreshListener) {
        mRefreshListener = refreshListener;
    }

    public void setRefreshHeader(IRefreshHeader refreshHeader) {
        if (refreshHeader == null) {
            return;
        }

        if (mRefreshHeader != null) {
            releaseHeader();
        }
        mRefreshHeader = refreshHeader;
        attachHeader();
    }

    private void attachHeader() {
        if (mRefreshHeader == null) {
            mRefreshHeader = new DRefreshHeader(getContext());
        }

        View header = mRefreshHeader.getHeader();
        if (header != null) {
            mHeaderView = header;
            addView(header, 0);
        }
    }

    private void releaseHeader() {
        if (mRefreshHeader != null) {
            View header = mRefreshHeader.getHeader();
            if (header != null) {
                removeView(header);
            }
        }
    }


    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        mParentHelper = new NestedScrollingParentHelper(this);
        mTotalDragDistance = (int) (getContext().getResources().getDisplayMetrics().density * 120);
        attachHeader();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(20);
        setWillNotDraw(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount > 3) {
            throw new RuntimeException("the max child count is 3");
        }

        ensureViews();
    }

    private void ensureViews() {
        if (mContentView == null) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (i == 0) {
                    mHeaderView = child;
                } else if (i == 1) {
                    mContentView = child;
                } else {
                    mTailView = child;
                }
            }
        }
        Log.d(TAG, "ensureViews: ");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() > 3) {
            throw new IllegalArgumentException("the max child count must be less than 3");
        }

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }

        if (!mMeasured) {
            mOriginOffsetTop = mCurOffsetTop = -mHeaderView.getMeasuredHeight();
            mMeasured = true;
            mTotalDragDistance += mOriginOffsetTop;
            mMaxOffset = (int) (1.5 * mTotalDragDistance);
            if (!mRefreshHeader.isScrollContent()) {
                mHeaderView.bringToFront();
            }
        }
        Log.d(TAG, "onMeasure: ");
    }

    private boolean mMeasured;

    /**
     * 布局改变
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        LogUtil.d(TAG, "onLayout: mCurTop" + mCurOffsetTop + "\tmaxOffset:" + mMaxOffset);

        if (mContentView == null) {
            ensureViews();
        }

        if (mContentView == null) {
            return;
        }

        int width = getMeasuredWidth();

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == mHeaderView) {
                child.layout(paddingLeft, mCurOffsetTop,
                        paddingLeft + width, mCurOffsetTop + child.getMeasuredHeight());
            } else if (mContentView == child) {
                int contentTop = mHeaderView.getBottom() + paddingTop;
                if (!mRefreshHeader.isScrollContent()) {
                    contentTop = paddingTop;
                }

                child.layout(paddingLeft,
                        contentTop,
                        paddingLeft + width,
                        contentTop + child.getMeasuredHeight());
            } else if (mTailView == child) {
                child.layout(paddingLeft,
                        mContentView.getBottom(),
                        paddingLeft + width,
                        mContentView.getBottom() + child.getMeasuredHeight());
            }
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if ((android.os.Build.VERSION.SDK_INT < 21 && mContentView instanceof AbsListView)
                || (mContentView != null && !ViewCompat.isNestedScrollingEnabled(mContentView))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureViews();

        if (true) {
            return super.onInterceptTouchEvent(ev);
        }
        int action = ev.getActionMasked();
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart ||
                canChildScrollUp() || mRefreshing) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mActivePointerId = ev.getPointerId(0);
                int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                mIsBeginingDrag = false;
                mLastY = mInitDownY = (int) ev.getY(pointerIndex);
                Log.d(TAG, "onInterceptTouchEvent: downY" + mInitDownY);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == MotionEvent.INVALID_POINTER_ID) {
                    return false;
                }

                int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                startDraging(ev.getY(pointerIndex));
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                mIsBeginingDrag = false;
                break;
        }

        return mIsBeginingDrag;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int actionIndex = ev.getActionIndex();
        int pointerId = ev.getPointerId(actionIndex);
        if (pointerId == mActivePointerId) {
            mActivePointerId = ev.getPointerId(actionIndex == 0 ? 1 : 0);
        }
    }

    private void startDraging(float y) {
        if (!mIsBeginingDrag) {
            float diffY = y - mInitDownY;
            if (diffY > mTouchSlop) {
                mIsBeginingDrag = true;
            }
        }
    }

    private boolean canChildScrollUp() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (mContentView instanceof AbsListView) {
                AbsListView listView = (AbsListView) mContentView;
                return listView.getChildCount() > 0
                        && (listView.getFirstVisiblePosition() > 0
                        || listView.getChildAt(0).getTop() > listView.getPaddingTop());
            } else {
                return mContentView.canScrollVertically(-1) ||
                        mContentView.getScrollY() > 0;
            }
        } else {
            return mContentView.canScrollVertically(-1);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart ||
                canChildScrollUp() || mRefreshing) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeginingDrag = false;
                float downY = ev.getY(ev.findPointerIndex(mActivePointerId));
                Log.d(TAG, "onTouchEvent: downY" + downY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == MotionEvent.INVALID_POINTER_ID) {
                    return false;
                }
                int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                final float moveY = ev.getY(pointerIndex);
                startDraging(moveY);
                if (mIsBeginingDrag) {
                    float diffY = moveY - mLastY;
                    Log.d(TAG, "onTouchEvent:moveDiff " + diffY);
                    if ((diffY >= 0 && mHeaderView.getTop() <= mMaxOffset)
                            || (diffY <= 0 && mHeaderView.getTop() >= mOriginOffsetTop)) {
                        handleMove(diffY);
                    } else {
                        return false;
                    }
                }
                mLastY = (int) moveY;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mActivePointerId = ev.getPointerId(ev.getActionIndex());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mActivePointerId == MotionEvent.INVALID_POINTER_ID) {
                    if (action == MotionEvent.ACTION_POINTER_UP) {
                        return false;
                    }
                }

                mIsBeginingDrag = false;
                final float y = ev.getY(ev.findPointerIndex(mActivePointerId));
                finishMove(y - mInitDownY);
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                break;
        }

        return true;
    }

    private Paint mPaint;

    private void finishMove(float diffY) {
        if (diffY > mTotalDragDistance - mOriginOffsetTop) {
            setRefreshing(true, true);
        } else {
            mRefreshing = false;
            animateOffsetToStart(mCurOffsetTop);
        }
    }

    public void setRefresh(boolean refreshing) {
        setRefreshing(refreshing, false);
    }

    private void setRefreshing(boolean refreshing, boolean notify) {
        if (mRefreshing != refreshing) {
            mRefreshing = refreshing;
            mNotify = notify;
            if (mRefreshing) {
                animateOffsetToCorrectPos(mCurOffsetTop);
            } else {
                mReturningToStart = true;
                animateOffsetToStart(mCurOffsetTop);
            }
        }

    }

    private Animation mAnimToStart = new Animation() {
        private int mLast;

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            if (interpolatedTime == 0) {
                mLast = mFrom;
            }

            int targetTop = (int) (mFrom + (mOriginOffsetTop - mFrom) * interpolatedTime);
            int offset = targetTop - mLast;
            mLast = targetTop;
            handleMove(offset);
        }
    };

    private void animateOffsetToStart(int curOffsetTop) {
        mFrom = curOffsetTop;

        mAnimToStart.reset();
        mAnimToStart.setDuration(200);
        mAnimToStart.setInterpolator(mDecelerateInterpolator);
        mAnimToStart.setAnimationListener(mRefreshAnimListener);

        mHeaderView.clearAnimation();
        mHeaderView.startAnimation(mAnimToStart);
    }

    private Animation mAnimToPos = new Animation() {
        private int mLast = -1;

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int endTarget = mTotalDragDistance;
            if (interpolatedTime == 0) {
                mLast = mFrom;
            }
            int targetTop = (int) (mFrom + (endTarget - mFrom) * interpolatedTime);
            int offset = targetTop - mLast;
            mLast = targetTop;

            Log.d(TAG, "applyTransformation: Pos" + offset);
            handleMove(offset);
        }
    };

    private void animateOffsetToCorrectPos(int curOffsetTop) {
        mFrom = curOffsetTop;
        mAnimToPos.reset();
        mAnimToPos.setDuration(200);
        mAnimToPos.setInterpolator(mDecelerateInterpolator);
        mAnimToPos.setAnimationListener(mRefreshAnimListener);

        mHeaderView.clearAnimation();
        mHeaderView.startAnimation(mAnimToPos);
    }

    private void handleMove(float diffY) {
        int diffYScroll = (int) diffY;
        mHeaderView.offsetTopAndBottom(diffYScroll);
        mCurOffsetTop = mHeaderView.getTop();

        if (mRefreshHeader.isScrollContent()) {
            mContentView.offsetTopAndBottom(diffYScroll);
            if (mTailView != null) {
                mTailView.offsetTopAndBottom(diffYScroll);
            }
        } else {
            mHeaderView.bringToFront();
        }

        Log.d(TAG, "handleMove:diffY " + diffYScroll
                + "\tcurTop:" + mCurOffsetTop
                + "\theadT:" + mHeaderView.getTop()
                + "\tcontentT:" + mContentView.getTop());
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(View child) {
        mParentHelper.onStopNestedScroll(child);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        LogUtil.d(TAG, "onNestedScroll: ");
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        boolean nested = isEnabled()
                && (ViewCompat.SCROLL_AXIS_VERTICAL & axes) != 0
                && !mRefreshing
                && !mReturningToStart;
        Log.d(TAG, "onStartNestedScroll: " + nested);
        return nested;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mParentHelper.onNestedScrollAccepted(child, target, axes, type);
        LogUtil.d(TAG, "onNestedScrollAccepted: ");
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        LogUtil.d(TAG, "onStopNestedScroll: ");
        mParentHelper.onStopNestedScroll(target, type);
        finishMove(mCurOffsetTop - mOriginOffsetTop);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        LogUtil.d(TAG, "onNestedScroll: ");
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        LogUtil.d(TAG, "onNestedPreScroll: " + dy);
        if (dy > 0) {
            if (mCurOffsetTop != mOriginOffsetTop) {
                int diffY = mCurOffsetTop - mOriginOffsetTop;
                if (dy - diffY < 0) {
                    consumed[1] = dy;
                    mCurOffsetTop -= dy;
                } else {
                    consumed[1] = diffY;
                    mCurOffsetTop = mOriginOffsetTop;
                }

                ViewCompat.offsetTopAndBottom(mHeaderView, -consumed[1]);
                if (mRefreshHeader.isScrollContent()) {
                    ViewCompat.offsetTopAndBottom(target, -consumed[1]);
                    if (mTailView != null) {
                        ViewCompat.offsetTopAndBottom(target, -consumed[1]);
                    }
                }
            }
        } else {
            if (!target.canScrollVertically(-1)) {
                if (mCurOffsetTop <= mMaxOffset) {
                    int diffY = mMaxOffset - mCurOffsetTop;
                    if (diffY + dy < 0) {
                        consumed[1] = -diffY;
                        mCurOffsetTop = mMaxOffset;
                    } else {
                        consumed[1] = dy;
                        mCurOffsetTop -= dy;
                    }

                    ViewCompat.offsetTopAndBottom(mHeaderView, -consumed[1]);
                    if (mRefreshHeader.isScrollContent()) {
                        ViewCompat.offsetTopAndBottom(target, -consumed[1]);
                        if (mTailView != null) {
                            ViewCompat.offsetTopAndBottom(target, -consumed[1]);
                        }
                    }
                }
            }
        }

        LogUtil.d(TAG, "the curTop" + mCurOffsetTop
                + "\tthe top is:" + mHeaderView.getTop());
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
//        if (mCurOffsetTop >= mOriginOffsetTop && mCurOffsetTop <= mMaxOffset) {
//            Log.d(TAG, "onNestedPreFling: " + velocityY);
//            if (velocityY > 0) {
//
//            }
//            return true;
//
//        }
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
//        if (mCurOffsetTop > mOriginOffsetTop && mCurOffsetTop < mMaxOffset) {
//            if (velocityY > 0) {
//
//            }
//            Log.d(TAG, "onNestedFling: " + velocityY + "\tconsume" + consumed);
//            return true;
//        }
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }


    public interface RefreshListener {
        void onRefresh();
    }
}
