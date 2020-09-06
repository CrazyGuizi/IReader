package com.ldg.common.view.refresh.header;

import android.view.View;

import androidx.annotation.NonNull;

import com.ldg.common.view.refresh.IRefreshHeader;

/**
 * created by gui 2020/9/6
 */
public class RefreshHeaderWrapper implements IRefreshHeader {
    private View mHeaderView;

    public RefreshHeaderWrapper(View headerView) {
        mHeaderView = headerView;
    }

    @NonNull
    @Override
    public View getHeader() {
        return mHeaderView;
    }

    @Override
    public boolean isScrollContent() {
        return true;
    }

    @Override
    public void onRefreshTrigger() {

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

    }

    @Override
    public void onRefreshEnd() {

    }

    @Override
    public void onOffsetChange(float percent) {

    }
}
