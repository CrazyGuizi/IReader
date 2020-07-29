package com.ldg.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ListViewCompat;

import com.ldg.common.log.LogUtil;
import com.ldg.common.view.refresh.IRefreshHeader;
import com.ldg.common.view.refresh.header.DRefreshHeader;

public class RefreshLayout extends ViewGroup implements NestedScrollingParent3 {

    private static final String TAG = RefreshLayout.class.getSimpleName();
    private IRefreshHeader mRefreshHeader;
    private View mHeaderView;
    private View mContentView;
    private NestedScrollingParentHelper mParentHelper;

    private int mMaxOffset;
    private int mCurOffset;

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
        mParentHelper = new NestedScrollingParentHelper(this);
        attachHeader();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount > 3) {
            throw new RuntimeException("the max child count is 3");
        }

        if (childCount == 1) {
            mContentView = getChildAt(0);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }

        mMaxOffset = mHeaderView.getMeasuredHeight();
    }

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
        LogUtil.d(TAG, "onLayout: " + mCurOffset + "\tmaxOffset:" + mMaxOffset);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == mHeaderView) {
                child.layout(l, t + mCurOffset - child.getMeasuredHeight(),
                        r, t + mCurOffset);
            } else {
                child.layout(l, t + mCurOffset,
                        r, t + mCurOffset + child.getMeasuredHeight());
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
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
        return isEnabled() && (ViewCompat.SCROLL_AXIS_VERTICAL & axes) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        LogUtil.d(TAG, "onNestedScrollAccepted: ");
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        LogUtil.d(TAG, "onStopNestedScroll: ");
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        LogUtil.d(TAG, "onNestedScroll: ");
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        LogUtil.d(TAG, "onNestedPreScroll: " + dy);
        if (dy > 0) {
            if (mCurOffset != 0) {
                int scroll = mCurOffset - dy;
                if (scroll < 0) {
                    consumed[1] = mCurOffset;
                    mCurOffset = 0;
                } else {
                    consumed[1] = dy;
                    mCurOffset = scroll;
                }

                ViewCompat.offsetTopAndBottom(mHeaderView, -consumed[1]);
                ViewCompat.offsetTopAndBottom(target, -consumed[1]);
            }
        } else {
            if (!target.canScrollVertically(-1)) {
                if (mCurOffset < mMaxOffset) {
                    int scroll = mCurOffset - dy;
                    if (scroll > mMaxOffset) {
                        consumed[1] = -mMaxOffset + mCurOffset;
                        mCurOffset = mMaxOffset;
                    } else {
                        consumed[1] = dy;
                        mCurOffset = scroll;
                    }

                    ViewCompat.offsetTopAndBottom(mHeaderView, -consumed[1]);
                    ViewCompat.offsetTopAndBottom(target, -consumed[1]);
                }
            }
        }

        LogUtil.d(TAG, "the top is " + mHeaderView.getTop());
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
//        if (mCurOffset != 0 || mCurOffset != mMaxOffset) {
//            Log.d(TAG, "onNestedPreFling: " + velocityY);
//            return true;
//        }
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        if (mCurOffset != 0 || mCurOffset != mMaxOffset) {
            Log.d(TAG, "onNestedFling: " + velocityY + "\tconsume" + consumed);
            return true;
        }
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }
}
