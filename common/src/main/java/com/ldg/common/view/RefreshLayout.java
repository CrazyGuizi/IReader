package com.ldg.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ListViewCompat;

public class RefreshLayout extends ViewGroup {
    private int mTouchSlop;
    private View mHeaderView;

    private View mTarget;
    private NestedScrollingParentHelper mParentHelper;
    private NestedScrollingChildHelper mChildHelper;

    private int mHeaderHeight;
    private int mTargetHeight;

    private int mCurOffsetTop = -1;
    private boolean mRefreshing;
    private int mActivePointerId;
    private int INVALID_POINTER = -1;
    private boolean mDragging;
    private int mInitDownY;
    private int mInitMotionY;


    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mParentHelper = new NestedScrollingParentHelper(this);
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView == null) {
            ensureHeader();
        }

        if (mTarget == null) {
            ensureTarget();
        }

        if (mTarget == null) {
            return;
        }

        mHeaderView.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int) (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()), MeasureSpec.EXACTLY));

        mHeaderHeight = mHeaderView.getMeasuredHeight();

        mTarget.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - mHeaderHeight, MeasureSpec.EXACTLY));
        mTargetHeight = mTarget.getMeasuredHeight();
    }

    private void ensureHeader() {
        mHeaderView = getChildAt(0);
    }

    private void ensureTarget() {
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mHeaderView)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;

        if (mTarget == null) {
            ensureTarget();
        }

        int headerW = mHeaderView.getMeasuredWidth();
        int headerH = mHeaderView.getMeasuredHeight();

        mHeaderView.layout((width - headerW) / 2, mCurOffsetTop - headerH,
                (width + headerW) / 2, mCurOffsetTop);

        int targetW = width - getPaddingLeft() - getPaddingRight();
        int targetH = height - getPaddingBottom() - getPaddingTop() - mCurOffsetTop;

        mTarget.layout(getPaddingLeft(), mCurOffsetTop,
                getPaddingLeft() + targetW, mCurOffsetTop + targetH);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled() || canChildScrollUp() || mRefreshing) {
            return false;
        }

        int pointerIndex;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setOffsetTop(-mTarget.getTop());

                mActivePointerId = ev.getPointerId(0);
                mDragging = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                mInitDownY = (int) ev.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                float y = ev.getY(pointerIndex);
                startDragging(y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER;
                mDragging = false;
                break;
        }

        return mDragging;
    }

    private void onSecondPointerUp(MotionEvent ev) {
        int actionIndex = ev.getActionIndex();
        int pointerId = ev.getPointerId(actionIndex);
        if (mActivePointerId == pointerId) {
            mActivePointerId = actionIndex == 0
                    ? ev.getPointerId(0)
                    : ev.getPointerId(1);
        }
    }

    private void startDragging(float y) {
        float dy = y - mInitDownY;
        if (dy > mTouchSlop && !mDragging) {
            mInitMotionY = mInitDownY + mTouchSlop;
            mDragging = true;
        }
    }

    private void setOffsetTop(int offset) {
        ViewCompat.offsetTopAndBottom(mHeaderView, offset);
        ViewCompat.offsetTopAndBottom(mTarget, offset);
        mCurOffsetTop = mTarget.getTop();
    }

    private boolean canChildScrollUp() {
        if (mTarget instanceof AbsListView) {
            return ListViewCompat.canScrollList((ListView) mTarget, -1);
        }

        return mTarget.canScrollVertically(-1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }
}
