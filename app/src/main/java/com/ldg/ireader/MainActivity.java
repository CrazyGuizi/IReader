package com.ldg.ireader;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.ldg.common.util.StatusBarUtils;
import com.ldg.common.view.activity.BaseActivity;
import com.ldg.ireader.explore.ExploreFragment;
import com.ldg.ireader.widgets.bottom.TabManager;

public class MainActivity extends BaseActivity {

    private TabManager mTabManager;

    @Override
    protected StatusBarUtils getStatusBar() {
        return new StatusBarUtils.Builder().setDark(true).setColor(Color.WHITE).build();
    }

    @Override
    public void doBeforeInit() {

    }

    @Override
    public int getLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    public void initWidgets() {
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doAfterInit() {
        mTabManager = new TabManager(this, findViewById(R.id.bottom_tab_layout));
        mTabManager.init();
    }
}
