package com.ldg.ireader;

import android.app.Application;
import android.content.Context;

import com.ldg.common.BaseApplication;
import com.ldg.common.http.api.HttpApiManager;

public class App extends BaseApplication {

    public static Context sContext;
    private static final String[] modules = new String[]{"App", "Common"};


    public static Context get() {
        return sContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sContext = this;
        HttpApiManager.init(modules);
    }
}
