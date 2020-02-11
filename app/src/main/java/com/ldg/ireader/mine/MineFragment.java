package com.ldg.ireader.mine;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.ldg.common.view.BaseFragment;
import com.ldg.ireader.R;

public class MineFragment extends BaseFragment {

    public static MineFragment newInstance(Bundle args) {
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutView() {
        return R.layout.frag_mine;
    }

    @Override
    protected void initWidgets() {

    }

    @Override
    protected void bindData() {

    }
}
