package com.ldg.common.util;

import android.text.TextUtils;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class JsonUtils {

    public static String toStr(Map<String, Object> map) {
        if (map != null && !map.isEmpty()) {
            try {
                JSONObject object = new JSONObject();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry != null && !TextUtils.isEmpty(entry.getKey())) {
                        object.put(entry.getKey(), entry.getValue());
                    }
                }
                return object.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    public static String toStr(List<Pair<String, String>> params) {
        if (params != null && !params.isEmpty()) {
            try {
                JSONObject object = new JSONObject();
                for (Pair<String, String> pair : params) {
                    if (pair != null && !TextUtils.isEmpty(pair.first)) {
                        object.put(pair.first, pair.second);
                    }
                }
                return object.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return "";
    }
}
