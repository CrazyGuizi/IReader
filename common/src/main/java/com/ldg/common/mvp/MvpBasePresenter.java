package com.ldg.common.mvp;

import java.lang.ref.WeakReference;

public abstract class MvpBasePresenter<V extends IMvpView> implements IMvpPresenter<V> {

    protected WeakReference<V> mView;

    @Override
    public void attach(V view) {
        mView = new WeakReference<>(view);
    }

    public abstract void onViewInit();

    @Override
    public void detach() {
        if (mView != null) {
            mView.clear();
            mView = null;
        }
    }

    @Override
    public V getView() {
        return mView.get();
    }

    @Override
    public boolean isViewAttached() {
        return mView != null && mView.get() != null;
    }
}
