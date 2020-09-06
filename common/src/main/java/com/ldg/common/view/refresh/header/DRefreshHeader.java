package com.ldg.common.view.refresh.header;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldg.common.R;
import com.ldg.common.view.refresh.IRefreshHeader;

public class DRefreshHeader extends FrameLayout implements IRefreshHeader {

    private TextView mTvRefresh;

    public DRefreshHeader(@NonNull Context context) {
        this(context, null);
    }

    public DRefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DRefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View inflate = inflate(getContext(), R.layout.view_refresh_header, this);
        mTvRefresh = inflate.findViewById(R.id.tvRefresh);
        setBackgroundColor(Color.GREEN);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width, height;
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);

        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        if (wMode == MeasureSpec.AT_MOST) {
            width = wSize;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @NonNull
    @Override
    public View getHeader() {
        return this;
    }

    @Override
    public boolean isScrollContent() {
        return true;
    }

    @Override
    public void onRefreshTrigger() {
        mTvRefresh.setText("松手立即刷新");
    }

    @Override
    public void onRefreshShut() {

    }

    @Override
    public void onRefreshCancel() {

    }

    @Override
    public void onRefreshStart() {

    }

    @Override
    public void onRefreshFinish() {
        mTvRefresh.setText("刷新成功");
    }

    @Override
    public void onRefreshEnd() {
        mTvRefresh.setText("我是刷新条");
    }

    @Override
    public void onOffsetChange(float percent) {

    }
}
