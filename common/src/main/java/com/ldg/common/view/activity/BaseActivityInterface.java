package com.ldg.common.view.activity;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.LayoutRes;

public interface BaseActivityInterface {

    void doBeforeInit();

    @LayoutRes
    int getLayoutView();

    void initWidgets();

    void setListener();

    void doAfterInit();
}
