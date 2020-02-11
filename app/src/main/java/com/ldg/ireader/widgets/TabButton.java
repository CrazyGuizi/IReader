package com.ldg.ireader.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ldg.ireader.R;

public class TabButton extends LinearLayout {
    private TextView mTvTabName;
    private ImageView mImvIcon;
    private String mKey;

    public TabButton(Context context) {
        super(context);
        initView();
    }

    public TabButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TabButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_tab_button, this);
        mTvTabName = findViewById(R.id.tv_tab_name);
        mImvIcon = findViewById(R.id.imv_tab_icon);
    }

    public void setTabText(String tab) {
        if (mTvTabName != null) {
            mTvTabName.setText(tab);
        }
    }

    public void setIconSrcResource(int resId) {
        if (mImvIcon != null) {
            mImvIcon.setImageResource(resId);
        }

    }

    public void setTabKey(String key) {
        this.mKey = key;
    }

    public String getTabKey() {
        return mKey;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (mTvTabName != null) {
            mTvTabName.setSelected(selected);
        }

        if (mImvIcon != null) {
            mImvIcon.setSelected(selected);
        }
    }
}
