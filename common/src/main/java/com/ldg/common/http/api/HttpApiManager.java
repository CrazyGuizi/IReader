package com.ldg.common.http.api;

import android.text.TextUtils;
import android.util.SparseArray;

import com.ldg.common.http.response.Response;

public class HttpApiManager {

    private static final SparseArray<Response> API_MAP = new SparseArray<>();

    public static void init(String... modules) {
        ClassLoader classLoader = HttpApiManager.class.getClassLoader();
        if (modules != null && modules.length > 0) {
            for (String module : modules) {
                try {
                    Class<?> aClass = classLoader.loadClass("com.ldg.common.HttpApiServiceImpl_" + module);
                    IHttpApi httpApi = (IHttpApi) aClass.newInstance();
                    httpApi.addApi();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addApi(int reqCode, Response response) {
        if (API_MAP.indexOfKey(reqCode) > 0) {
            throw new IllegalArgumentException(String.format("网络请求码%d已存在！", reqCode));
        }
        API_MAP.append(reqCode, response);
    }

    public static Response getResponse(int requestCode) {
        return API_MAP.get(requestCode);
    }
}
