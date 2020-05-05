package com.ldg.ireader.bookshelf.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;

import com.ldg.ireader.utils.ScreenUtils;

public class SimpleDrawerLayout extends ViewGroup {

    public static final String TAG = SimpleDrawerLayout.class.getSimpleName();
    private static final int DEFAULT_SCRIM_COLOR = 0x99000000;

    public static final int ELEVATION = ScreenUtils.dp2px(10);

    public static final int FLAG_IS_OPENED = 0x1;
    public static final int FLAG_IS_OPENING = 0x2;
    public static final int FLAG_IS_CLOSING = 0x4;
    public static final int FLAG_IS_CLOSED = 0x6;

    // 遮罩
    private int mScrimColor = DEFAULT_SCRIM_COLOR;
    private float mScrimOpacity;
    private Paint mScrimPaint = new Paint();

    private static int EDGE_SIZE = ScreenUtils.dp2px(20);

    private int mOpenStatus = FLAG_IS_CLOSED;

    private int mInitInterceptX;
    private int mInitInterceptY;

    private int mInitTouchX;
    private int mInitTouchY;
    // 是否移动了
    private boolean mCanMove;

    private Scroller mScroller;
    private int mTouchSlop;
    private boolean mTouchEdge;
    private float mMinXVelocity;
    private VelocityTracker mVelocityTracker;
    private ScrollRunnable mScrollRunnable;

    private boolean mEnableScroll;


    /**
     * 禁止滑动
     *
     * @param enableScroll
     */
    public void setEnableScroll(boolean enableScroll) {
        mEnableScroll = enableScroll;
    }

    public SimpleDrawerLayout(Context context) {
        this(context, null);
    }

    public SimpleDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mScroller = new Scroller(getContext());
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        mMinXVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modeW = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int modeH = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (modeW != MeasureSpec.EXACTLY || modeH != MeasureSpec.EXACTLY) {
            if (modeW == MeasureSpec.AT_MOST) {
                modeW = MeasureSpec.EXACTLY;
            } else if (modeW == MeasureSpec.UNSPECIFIED) {
                modeW = MeasureSpec.EXACTLY;
                widthSize = 300;
            }
            if (modeH == MeasureSpec.AT_MOST) {
                modeH = MeasureSpec.EXACTLY;
            } else if (modeH == MeasureSpec.UNSPECIFIED) {
                modeH = MeasureSpec.EXACTLY;
                heightSize = 300;
            }
        }
        setMeasuredDimension(widthSize, heightSize);

        boolean hasDrawerLeft = false;
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (isContentView(child)) {
                int contentWidth = MeasureSpec.makeMeasureSpec(
                        widthSize - lp.leftMargin - lp.rightMargin, modeW);
                int contentHeight = MeasureSpec.makeMeasureSpec(
                        heightSize - lp.topMargin - lp.bottomMargin, modeH);
                child.measure(contentWidth, contentHeight);
            } else if (isDrawerView(child)) {
                boolean leftEdge = lp.gravity == Gravity.LEFT;

                if (hasDrawerLeft && leftEdge) {
                    throw new IllegalStateException("already has a drawer view");
                }
                hasDrawerLeft = leftEdge;

                if (ViewCompat.getElevation(child) != ELEVATION) {
                    ViewCompat.setElevation(child, ELEVATION);
                }

                child.measure(getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width),
                        getChildMeasureSpec(heightMeasureSpec, lp.topMargin + lp.bottomMargin,
                                lp.height));
            } else {
                throw new IllegalStateException("child " + child + "'s gravity not can be" +
                        "other else Gravity.LEFT | Gravity.NO_GRAVITY");
            }
        }
    }

    private boolean isDrawerView(View child) {
        LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        return (layoutParams.gravity & Gravity.LEFT) != 0;
    }

    private boolean isContentView(View child) {
        return ((LayoutParams) child.getLayoutParams()).gravity == Gravity.NO_GRAVITY;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (isContentView(child)) {
                child.layout(lp.leftMargin, lp.topMargin,
                        lp.leftMargin + child.getMeasuredWidth(),
                        lp.topMargin + child.getMeasuredHeight());
            } else if (isDrawerView(child)) {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                int childLeft = -childWidth + (int) (childWidth * lp.onScreen);

                updateStatus(lp.onScreen);
                Log.d(TAG, "onLayout: " + lp.onScreen);
                child.layout(childLeft, lp.topMargin,
                        childLeft + childWidth, lp.topMargin + childHeight);
            }
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        boolean contentView = isContentView(child);
        if (contentView && mScrimOpacity > 0) {
            canvas.save();
            int clipLeft = getDrawerView().getRight();
            canvas.clipRect(clipLeft, 0, getMeasuredWidth(), getMeasuredHeight());
            final int baseAlpha = (mScrimColor & 0xff000000) >>> 24;
            final int imag = (int) (baseAlpha * mScrimOpacity);
            final int color = imag << 24 | (mScrimColor & 0xffffff);
            mScrimPaint.setColor(color);
            canvas.drawRect(clipLeft, 0, getMeasuredWidth(), getHeight(), mScrimPaint);
            canvas.restore();
        }
        return result;
    }


    private void updateStatus(float onScreen) {
        Log.d(TAG, "updateStatus: " + onScreen);
        if (onScreen == 0F) {
            mOpenStatus = FLAG_IS_CLOSED;
        } else if (onScreen == 1F) {
            mOpenStatus = FLAG_IS_OPENED;
        }

        mScrimOpacity = MathUtils.clamp(onScreen, 0F, 1F);
    }

    private void updateStatus(boolean closing) {
        if (closing) {
            mOpenStatus = FLAG_IS_CLOSING;
        } else {
            mOpenStatus = FLAG_IS_OPENING;
        }
    }

    public boolean isClosing() {
        return mOpenStatus == FLAG_IS_CLOSING;
    }


    public boolean isOpening() {
        return mOpenStatus == FLAG_IS_OPENING;
    }


    public boolean isOpened() {
        return mOpenStatus == FLAG_IS_OPENED;
    }

    public boolean isClosed() {
        return mOpenStatus == FLAG_IS_CLOSED;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        boolean intercept = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitInterceptX = x;
                mInitInterceptY = y;
                mInitTouchX = x;
                mInitTouchY = y;
                mTouchEdge = false;
                mCanMove = false;
                if (isClosed()) {
                    checkEdgeTouch(x, y);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int diffX = x - mInitInterceptX;
                int diffY = y - mInitInterceptY;

                if (isOpened()
                        && isUnderTheDrawerView(x, y)
                        && Math.abs(diffX) > Math.abs(diffY)) {
                    intercept = true;
                } else {
                    if (mTouchEdge) {
                        if (diffX > 0) {
                            intercept = true;
                        }
                    }
                }
                mInitInterceptX = x;
                mInitInterceptY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;

        }

        return intercept && !mEnableScroll;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        ensureVelocity(ev);
        View drawerView = getDrawerView();
        LayoutParams lp = (LayoutParams) drawerView.getLayoutParams();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mInitTouchX = x;
                mInitTouchY = y;
                mCanMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int diffX = x - mInitTouchX;
                int diffY = y - mInitTouchY;

                if ((isOpened() && isUnderTheDrawerView(x, y) && diffX < 0)
                        || !isOpened() && lp.onScreen >= 0F && lp.onScreen <= 1F && drawerView.getMeasuredWidth() > 0) {
                    updateStatus(diffX < 0);

                    float newOnScreen = (drawerView.getMeasuredWidth() + drawerView.getLeft() + diffX) * 1F / drawerView.getMeasuredWidth();
//                    Log.d(TAG, "onTouchEvent: diffX " + diffX +  "\tper:" + newOnScreen);
                    if (newOnScreen != lp.onScreen) {
                        lp.onScreen = Math.max(0, Math.min(1, newOnScreen));
                        drawerView.setLayoutParams(lp);
                    }
                }

                if (!mCanMove) {
                    if (Math.abs(diffX) >= mTouchSlop || Math.abs(diffY) >= mTouchSlop) {
                        mCanMove = true;
                    }
                }

                mInitTouchX = x;
                mInitTouchY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                releaseVelocity();
                if (!isClosed() && !isOpened()) {
                    int dx = 0;
                    int startX = drawerView.getLeft();
                    if (Math.abs(xVelocity) > mMinXVelocity) {
                        if (xVelocity > 0) {
                            dx = -startX;
                        } else {
                            dx = -drawerView.getMeasuredWidth() - startX;
                        }
                    } else {
                        if (startX >= (-drawerView.getMeasuredWidth() * 1F) / 2) {
                            dx = -startX;
                        } else {
                            dx = -drawerView.getMeasuredWidth() - startX;
                        }
                    }
                    updateStatus(dx < 0);
                    scrollDrawer(startX, dx);
                } else if (isOpened() && !mCanMove && !isUnderTheDrawerView(x, y)) {
                    close();
                }
                break;
        }

        return !mEnableScroll;
    }

    public void open() {
        View drawerView = getDrawerView();
        if (drawerView != null) {
            scrollDrawer(drawerView.getLeft(), -drawerView.getLeft());
        }
    }

    public void close() {
        View drawerView = getDrawerView();
        if (drawerView != null) {
            scrollDrawer(drawerView.getLeft(), -drawerView.getMeasuredHeight() - drawerView.getLeft());
        }
    }

    private void scrollDrawer(int from, int diffX) {
        if (mScrollRunnable == null) {
            mScrollRunnable = new ScrollRunnable();
        }
        mScroller.startScroll(from, 0, diffX, 0);
        ViewCompat.postOnAnimation(this, mScrollRunnable);
    }

    private class ScrollRunnable implements Runnable {
        @Override
        public void run() {
            View drawerView = getDrawerView();
            LayoutParams lp = (LayoutParams) drawerView.getLayoutParams();
            if (mScroller.computeScrollOffset()) {
                int newLeft = mScroller.getCurrX();
                float newOnScreen = (drawerView.getMeasuredWidth() + newLeft) * 1F / drawerView.getMeasuredWidth();
                lp.onScreen = Math.min(Math.max(0F, newOnScreen), 1F);
                ViewCompat.postOnAnimation(SimpleDrawerLayout.this, this);
            }
            drawerView.setLayoutParams(lp);
        }
    }

    private void releaseVelocity() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void ensureVelocity(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);
    }

    private boolean isUnderTheDrawerView(float x, float y) {
        View drawerView = getDrawerView();
        if (drawerView != null) {
            if (x >= drawerView.getLeft() && x < drawerView.getRight()
                    && y >= drawerView.getTop() && y < drawerView.getBottom()) {
                return true;
            }
        }

        return false;
    }

    private boolean checkEdgeTouch(float x, float y) {
        return mTouchEdge = (x <= getLeft() + EDGE_SIZE);
    }

    private View getDrawerView() {
        for (int i = 0; i < getChildCount(); i++) {
            if (isDrawerView(getChildAt(i))) {
                return getChildAt(i);
            }
        }

        return null;
    }


    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams
                ? new LayoutParams((LayoutParams) p)
                : p instanceof MarginLayoutParams
                ? new MarginLayoutParams(p)
                : new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    private static class LayoutParams extends MarginLayoutParams {

        public int gravity = Gravity.NO_GRAVITY;
        float onScreen = 0.0F;


        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray typedArray = c.obtainStyledAttributes(attrs, new int[]{android.R.attr.gravity});
            gravity = typedArray.getInt(0, Gravity.NO_GRAVITY);
            typedArray.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = layoutParams.gravity;
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
