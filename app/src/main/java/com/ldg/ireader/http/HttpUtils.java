package com.ldg.ireader.http;

import android.text.TextUtils;
import android.util.Pair;
import android.widget.Toast;

import com.ldg.common.http.HttpDefaultHandler;
import com.ldg.common.http.api.HttpApiManager;
import com.ldg.common.http.HttpManager;
import com.ldg.common.http.HttpMethod;
import com.ldg.common.http.response.Response;
import com.ldg.common.util.ThreadUtils;
import com.ldg.ireader.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

public class HttpUtils {

    public static void get(int requestCode, List<Pair<String, String>> params, ResponseListener listener) {
        Response response = HttpApiManager.getResponse(requestCode);
        if (response != null) {
            HttpManager.get().exec(HttpMethod.GET, response.url(), null, params, new IReadHttpHandler(requestCode, response, listener));
        }
    }


    public static void post(int requestCode, List<Pair<String, String>> params, ResponseListener listener) {
        Response response = HttpApiManager.getResponse(requestCode);
        if (response != null) {
            HttpManager.get().exec(HttpMethod.POST, response.url(), null, params, new IReadHttpHandler(requestCode, response, listener));
        }
    }


    public static class IReadHttpHandler extends HttpDefaultHandler {

        private int mReqCode;
        private WeakReference<Response> mResponseRef;
        private ResponseListener mListener;

        public IReadHttpHandler(int requestCode, Response response, ResponseListener listener) {
            mReqCode = requestCode;
            mResponseRef = new WeakReference<>(response);
            mListener = listener;
        }

        @Override
        protected void dispatchSuccess(int code, String string) {
            if (code >= 200 && code < 400 && !TextUtils.isEmpty(string)) {
                try {
                    JSONObject object = new JSONObject(string);
                    if (object.optInt("code") == 0) {
                        if (mResponseRef.get() != null) {
                            Object data = object.opt("data");
                            Object resData = mResponseRef.get().getObj(data == null ? "" : data.toString());
                            if (mListener != null) {
                                ThreadUtils.runUi(() -> {
                                    mListener.onResponse(mReqCode, resData != null, resData);
                                });
                            }
                        }
                    } else {
                        dispatchFail(code, object.optString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void dispatchFail(int code, String string) {
            if (mListener != null) {
                ThreadUtils.runUi(() -> {
                    Toast.makeText(App.get(), string, Toast.LENGTH_LONG).show();
                    mListener.onResponse(mReqCode, false, null);
                });

            }
        }
    }
}
