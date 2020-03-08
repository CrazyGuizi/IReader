package com.ldg.common.mvp;

import android.app.Activity;

public interface IMvpView {

    void showEmpty(String msg);

    void showException(String msg);

    Activity getHostActivity();
}
