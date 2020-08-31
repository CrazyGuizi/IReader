package com.ldg.router;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * created by gui 2020/8/24
 */
public class AptHub {
    public static final String TAG = AptHub.class.getSimpleName();

    public static final Map<String, Class> sRouteTables = new HashMap<>();


    public static final void test() {
        if (sRouteTables != null) {
            for (Map.Entry<String, Class> entry : sRouteTables.entrySet()) {
                Log.d(TAG, "test: " + entry.getKey() + "\tval" + entry.getValue());
            }
        }
    }
}
