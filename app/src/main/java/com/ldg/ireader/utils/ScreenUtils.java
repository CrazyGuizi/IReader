package com.ldg.ireader.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.ldg.ireader.App;

public class ScreenUtils {
    public static final int dp2px(int dp) {
        Context context = App.get();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (displayMetrics.density * dp + 0.5F);
    }
}
