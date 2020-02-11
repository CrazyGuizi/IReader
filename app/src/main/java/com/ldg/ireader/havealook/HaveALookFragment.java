package com.ldg.ireader.havealook;

import android.os.Bundle;

import com.ldg.common.view.BaseFragment;
import com.ldg.ireader.R;

public class HaveALookFragment extends BaseFragment {

    public static HaveALookFragment newInstance(Bundle args) {
        HaveALookFragment fragment = new HaveALookFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutView() {
        return R.layout.frag_have_a_look;
    }

    @Override
    protected void initWidgets() {

    }

    @Override
    protected void bindData() {

    }
}
