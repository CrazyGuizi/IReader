package com.ldg.common.http;

import android.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public  class HttpManager implements IHttpService {
    private static HttpManager mHttpManager;

    private OkHttpClient mOkHttpClient;

    private HttpManager() {

    }

    private void init() {
        mOkHttpClient = new OkHttpClient();
    }


    @Override
    public void exec(HttpMethod method, String url, Map<String, String> headers, List<Pair<String, String>> params) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = mOkHttpClient.newCall(request).execute()) {
            if (response != null) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execSync(HttpMethod method, String url, Map<String, String> headers, List<Pair<String, String>> params) {

    }
}
