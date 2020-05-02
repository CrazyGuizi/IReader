package com.ldg.common.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadUtils {

    private static Handler sUiHandler;

    private static Executor sExecutor = Executors.newScheduledThreadPool(4);

    public static void runUi(Runnable runnable) {
        getUiHandler().post(runnable);
    }

    public static void execute(Runnable runnable) {
        sExecutor.execute(runnable);
    }

    private static Handler getUiHandler() {
        if (sUiHandler == null) {
            sUiHandler = new Handler(Looper.getMainLooper());
        }

        return sUiHandler;
    }

}
