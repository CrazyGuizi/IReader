package com.ldg.common.log;

import android.util.Log;

public class LogUtil {

    public static final String TAG = LogUtil.class.getSimpleName();

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }
}
