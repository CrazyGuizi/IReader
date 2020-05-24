package com.ldg.common.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class StatusBarUtils {

    private int color;
    private boolean isDark;

    private StatusBarUtils(Builder builder) {
        this.color = builder.color;
        this.isDark = builder.isDark;
    }

    public int getColor() {
        return color;
    }

    public boolean isDark() {
        return isDark;
    }

    public static void setBarColor(Activity activity, int color) {
        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            if (window != null) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            }
        }
    }

    public static void setBarDark(Activity activity, boolean dark) {
        if (activity == null) {
            return;
        }

        View view = activity.getWindow().getDecorView();
        if (dark) {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    public static class Builder {
        private boolean isDark;
        private int color;

        public boolean isDark() {
            return isDark;
        }

        public Builder setDark(boolean dark) {
            isDark = dark;
            return this;
        }

        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public StatusBarUtils build() {
            return new StatusBarUtils(this);
        }
    }
}


