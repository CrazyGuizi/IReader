package com.ldg.common.http;

import android.text.TextUtils;
import android.util.Pair;

import com.ldg.common.util.JsonUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public abstract class HttpManager implements IHttpService {
    public static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    protected OkHttpClient mOkHttpClient;
    private static HttpManager sHttpManager;

    public static final HttpManager get() {
        if (sHttpManager == null) {
            synchronized (sHttpManager) {
                if (sHttpManager == null) {
                    sHttpManager = new HttpManagerImpl();
                }
            }
        }

        return sHttpManager;
    }

    protected HttpManager() {
        init();
    }

    private void init() {
        mOkHttpClient = new OkHttpClient();
    }


    @Override
    public void exec(HttpMethod method, String url, Map<String, String> headers, List<Pair<String, String>> params) {
        exec(method, url, headers, params, null);
    }

    @Override
    public void execSync(HttpMethod method, String url, Map<String, String> headers, List<Pair<String, String>> params) {
        execSync(method, url, headers, params, null);
    }

    @Override
    public HttpConfig getConfig() {
        return null;
    }
}
