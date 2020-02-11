package com.ldg.ireader.widgets.bottom;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.ldg.ireader.R;
import com.ldg.ireader.utils.Constants;
import com.ldg.ireader.widgets.TabButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class BottomTabLayout extends LinearLayout implements View.OnClickListener {

    private LinkedHashMap<String, String> mTabs = new LinkedHashMap<>();
    private View mPreSelectedView;

    private OnTabSelectedListener mListener;

    public BottomTabLayout(Context context) {
        this(context, null);
    }

    public BottomTabLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomTabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOrientation(HORIZONTAL);
        setBackgroundColor(Color.WHITE);
        initTabData();

        for (Map.Entry<String, String> entry : mTabs.entrySet()) {
            TabButton tabButton = new TabButton(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            tabButton.setLayoutParams(lp);
            tabButton.setTabText((String) entry.getValue());
            tabButton.setTabKey((String) entry.getKey());
            tabButton.setIconSrcResource(getResourceByKey((String) entry.getKey()));
            tabButton.setSelected(false);
            tabButton.setOnClickListener(this);

            addView(tabButton, lp);

            if (mPreSelectedView == null) {
                mPreSelectedView = tabButton;
            }
        }
    }

    private int getResourceByKey(String key) {
        int ret = 0;
        if (TextUtils.isEmpty(key)) {
            return ret;
        }
        if (Constants.TAB_EXPLORE.equals(key)) {
            ret = R.drawable.tab_icon_explore;
        } else if (Constants.TAB_BOOK_SHELF.equals(key)) {
            ret = R.drawable.tab_icon_book_shelf;
        } else if (Constants.TAB_HAVE_A_LOOK.equals(key)) {
            ret = R.drawable.tab_icon_have_a_look;
        } else if (Constants.TAB_MINE.equals(key)) {
            ret = R.drawable.tab_icon_mine;
        }
        return ret;
    }

    private void initTabData() {
        if (mTabs == null) {
            mTabs = new LinkedHashMap<>();
        }

        if (mTabs != null && mTabs.size() > 0) {
            mTabs.clear();
        }

        ArrayList<String> defaultTabs = new ArrayList<>(Arrays.asList(Constants.DEFAULT_TABS));

        for (String bottomTab : defaultTabs) {
            String name;
            if (!TextUtils.isEmpty((name = parseTabData(bottomTab)))) {
                mTabs.put(bottomTab, name);
            }
        }
    }

    public String parseTabData(String key) {
        if (Constants.TAB_EXPLORE.equals(key)) {
            return "发现";
        } else if (Constants.TAB_BOOK_SHELF.equals(key)) {
            return "书架";
        } else if (Constants.TAB_HAVE_A_LOOK.equals(key)) {
            return "看一看";
        } else if (Constants.TAB_MINE.equals(key)) {
            return "我的";
        }

        return "";
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            if (mPreSelectedView != null && mPreSelectedView.isSelected()) {
                mPreSelectedView.setSelected(false);
            }
            v.setSelected(true);
            mPreSelectedView = v;

            if (mListener != null && v instanceof TabButton) {
                mListener.onTabSelected(((TabButton) v).getTabKey());
            }
        }
    }


    public void setTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        this.mListener = onTabSelectedListener;
    }

    public void init() {
        if (mPreSelectedView != null) {
            mPreSelectedView.performClick();
        }
    }

    public interface OnTabSelectedListener {
        void onTabSelected(String key);
    }
}
