package com.ldg.ireader.explore;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ldg.common.view.BaseFragment;
import com.ldg.ireader.R;
import com.ldg.ireader.http.HttpUtils;


public class ExploreFragment extends BaseFragment {

    public static ExploreFragment newInstance(Bundle args) {
        ExploreFragment fragment = new ExploreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutView() {
        return R.layout.frag_explore;
    }

    @Override
    protected void initWidgets() {

    }

    @Override
    protected void bindData() {

    }
}
