package com.ldg.common.mvp;

public interface IMvpPresenter<V extends IMvpView> {

    void attach(V view);

    void detach();

    V getView();

    boolean isViewAttached();
}
