package com.ldg.common.view.refresh.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldg.common.R;
import com.ldg.common.view.refresh.IRefreshHeader;

public class DRefreshHeader extends FrameLayout implements IRefreshHeader {


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
        inflate(getContext(), R.layout.view_refresh_header, this);
    }

    @NonNull
    @Override
    public View getHeader() {
        return this;
    }
}
