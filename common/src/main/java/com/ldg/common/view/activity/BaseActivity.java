package com.ldg.common.view.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity implements BaseActivityInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBeforeInit();
        setContentView(getLayoutView());
        initWidgets();
        setListener();
        doAfterInit();
    }

}
