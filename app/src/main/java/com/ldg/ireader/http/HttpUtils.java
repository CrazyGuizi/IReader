package com.ldg.ireader.http;

import android.util.Pair;

import com.ldg.common.http.api.HttpApiManager;
import com.ldg.common.http.HttpManager;
import com.ldg.common.http.HttpMethod;
import com.ldg.common.http.response.Response;

import java.util.List;

public class HttpUtils {

    public static void get(int requestCode, List<Pair<String, String>> params) {
        Response response = HttpApiManager.getResponse(requestCode);
        if (response != null) {
            HttpManager.get().exec(HttpMethod.GET, response.url(), null, params);
        }
    }
}
