package com.ldg.ireader.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ldg.common.view.activity.BaseActivity;
import com.ldg.ireader.R;
import com.ldg.ireader.widgets.PageView;

public class ReadActivity extends BaseActivity {

    private PageView mPageView;

    @Override
    public void doBeforeInit() {

    }

    @Override
    public int getLayoutView() {
        return R.layout.activity_read;
    }

    @Override
    public void initWidgets() {
        mPageView = findViewById(R.id.page_view);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doAfterInit() {

    }
}
