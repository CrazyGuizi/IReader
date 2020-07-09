package com.ldg.common.view.refresh.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldg.common.view.refresh.IRefreshHeader;

public class DRefreshHeader extends FrameLayout implements IRefreshHeader {


    public DRefreshHeader(@NonNull Context context) {
        super(context);
    }

    public DRefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DRefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    @Override
    public View getHeader() {
        return this;
    }
}
