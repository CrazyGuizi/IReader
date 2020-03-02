package com.ldg.common.http;

import android.util.Log;

import com.ldg.common.log.LogUtil;

import java.io.IOException;

import okhttp3.Response;

public class HttpDefaultHandler extends HttpHandler {
    @Override
    public void handle(Response response) {
        if (response != null) {
            boolean successful = response.isSuccessful();
            int code = response.code();
            String string = "";
            try {
                string = response.body().string();
                if (successful) {
                    dispatchSuccess(code, string);
                } else {
                    dispatchFail(code, string);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                response.close();
            }

        }
    }

    protected void dispatchFail(int code, String string) {
        LogUtil.d("code: " + code + "\n" + string);
    }

    protected void dispatchSuccess(int code, String string) {
        LogUtil.d("code: " + code + "\n" + string);
    }

    @Override
    public void requestFail(String msg) {
        dispatchFail(-1, msg);
    }
}
