package com.ldg.common.http;

import android.util.SparseArray;

import com.ldg.common.http.response.Response;

public class HttpApiManager {

    private static final SparseArray<Response> API_MAP = new SparseArray<>();


    public static Response getResponse(int requestCode) {
        return API_MAP.get(requestCode);
    }
}
