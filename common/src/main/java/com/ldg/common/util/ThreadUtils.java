package com.ldg.common.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

public class ThreadUtils {

    private static Handler sUiHandler;

    public static void runUi(Runnable runnable) {
        getUiHandler().post(runnable);
    }

    private static Handler getUiHandler() {
        if (sUiHandler == null) {
            sUiHandler = new Handler(Looper.getMainLooper());
        }

        return sUiHandler;
    }

}
