package com.ldg.common.view.activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ldg.common.util.StatusBarUtils;

public abstract class BaseActivity extends AppCompatActivity implements BaseActivityInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getStatusBar() != null) {
            StatusBarUtils statusBar = getStatusBar();
            statusBar.setBarDark(this, statusBar.isDark());
            statusBar.setBarColor(this, statusBar.getColor());
        }
        doBeforeInit();
        setContentView(getLayoutView());
        initWidgets();
        createPresenter();
        setListener();
        doAfterInit();
    }

    protected StatusBarUtils getStatusBar() {
        return null;
    }

    protected void createPresenter() {
    }

}
