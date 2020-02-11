package com.ldg.ireader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ldg.common.view.activity.BaseActivity;
import com.ldg.ireader.explore.ExploreFragment;
import com.ldg.ireader.widgets.bottom.TabManager;

public class MainActivity extends BaseActivity {

    private TabManager mTabManager;

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
