package com.ldg.ireader;

import android.app.Application;
import android.content.Context;

import com.ldg.common.BaseApplication;

public class App extends BaseApplication {

    public static Context sContext;


    public static Context get() {
        return sContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sContext = this;
    }
}
