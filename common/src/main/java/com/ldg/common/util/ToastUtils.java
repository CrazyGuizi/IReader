package com.ldg.common.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


public class ToastUtils {

    public static void show(Context context, String text) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {

        } else {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }
}
