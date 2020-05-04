package com.ldg.common.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


public class ToastUtils {

    private static Toast sToast;

    public static void show(Context context, String text) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            ThreadUtils.runUi(() -> {
                getToast(context).makeText(context, text, Toast.LENGTH_SHORT).show();
            });
        } else {
            getToast(context).makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    public static Toast getToast(Context context) {
        if (sToast == null) {
            sToast = new Toast(context);
        }
        return sToast;
    }
}
