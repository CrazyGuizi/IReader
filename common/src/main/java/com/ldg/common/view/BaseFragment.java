package com.ldg.common.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    protected View mRoot;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutView = getLayoutView();
        if (layoutView <= 0) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + "'s layout is illegal");
        } else {
            if (mRoot == null) {
                mRoot = inflater.inflate(layoutView, container, false);
                initWidgets();
                createPresenter();
                bindData();
            } else {
                ViewGroup parent = (ViewGroup) mRoot.getParent();
                if (parent != null) {
                    parent.removeView(mRoot);
                }
            }
        }
        return mRoot;
    }

    protected abstract int getLayoutView();

    protected abstract void initWidgets();

    protected void createPresenter() {}

    protected abstract void bindData();
}
