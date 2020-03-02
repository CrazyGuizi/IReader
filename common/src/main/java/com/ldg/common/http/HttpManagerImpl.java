package com.ldg.common.http;

import android.text.TextUtils;
import android.util.Pair;

import com.ldg.common.util.JsonUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpManagerImpl extends HttpManager {

    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int MAX_POOL_SIZE = CPU_COUNT * 8 + 1;
    private static final BlockingQueue<Runnable> BLOCKING_QUEUE = new LinkedBlockingQueue<>();

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private AtomicInteger mCount = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "iRead thread " + mCount.getAndIncrement());
        }
    };


    private static Executor sExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAX_POOL_SIZE, 1L, TimeUnit.SECONDS, BLOCKING_QUEUE, THREAD_FACTORY);

    private HttpHandler mHttpHandler;

    @Override
    public void exec(HttpMethod method, String url, Map<String, String> headers, List<Pair<String, String>> params, HttpHandler handler) {
        if (handler == null) {
            handler = getDefaultHandler();
        }

        HttpHandler finalHandler = handler;
        sExecutor.execute(() -> {
            mOkHttpClient.newCall(buildRequest(method, url, headers, params)).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    finalHandler.requestFail(e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    finalHandler.handle(response);
                }
            });
        });
    }

    @Override
    public void execSync(HttpMethod method, String url, Map<String, String> headers, List<Pair<String, String>> params, HttpHandler handler) {
        if (handler == null) {
            handler = getDefaultHandler();
        }

        HttpHandler finalHandler = handler;
        sExecutor.execute(() -> {
            try (Response response = mOkHttpClient.newCall(buildRequest(method, url, headers, params)).execute()) {
                if (response != null) {
                    finalHandler.handle(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private HttpHandler getDefaultHandler() {
        if (mHttpHandler == null) {
            mHttpHandler = new HttpDefaultHandler();
        }
        return mHttpHandler;
    }

    protected Request buildRequest(HttpMethod method, String url, Map<String, String> headers, List<Pair<String, String>> params) {
        Request.Builder requestBuild = new Request.Builder();

        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (!TextUtils.isEmpty(entry.getKey())) {
                    requestBuild.addHeader(entry.getKey(), entry.getValue());
                }
            }
        }

        if (method == HttpMethod.GET) {
            StringBuilder builder = new StringBuilder(url)
                    .append("?");
            if (params != null && !params.isEmpty()) {
                for (Pair<String, String> param : params) {
                    if (param != null && !TextUtils.isEmpty(param.first)) {
                        builder.append(param.first)
                                .append("=")
                                .append(param.second)
                                .append("&");
                    }
                }
            }

            requestBuild.get().url(builder.substring(0, builder.length() - 1));
        } else {
            RequestBody body = RequestBody.create(JsonUtils.toStr(params), JSON_TYPE);
            requestBuild.post(body).url(url);
        }

        return requestBuild.build();
    }
}
