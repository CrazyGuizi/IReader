package com.ldg.common.http.api;

import android.text.TextUtils;
import android.util.SparseArray;

import com.ldg.common.http.response.Response;
import com.ldg.http.IHttpApi;

import java.util.HashMap;
import java.util.Map;

public class HttpApiManager {

    private static final Map<Integer, Class<?>> API_MAP = new HashMap<>();

    public static void init(String... modules) {
        ClassLoader classLoader = HttpApiManager.class.getClassLoader();
        if (modules != null && modules.length > 0) {
            for (String module : modules) {
                try {
                    Class<?> aClass = classLoader.loadClass("com.ldg.apt.http.HttpApiImpl_" + module);
                    IHttpApi httpApi = (IHttpApi) aClass.newInstance();
                    httpApi.addApi(API_MAP);
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
        if (API_MAP.containsKey(reqCode)) {
            throw new IllegalArgumentException(String.format("网络请求码%d已存在！", reqCode));
        }
        API_MAP.put(reqCode, response.getClass());
    }

    public static Response getResponse(int requestCode) {
        Class<?> aClass = API_MAP.get(requestCode);
        if (aClass != null) {
            try {
                Object o = aClass.newInstance();
                return (Response) o;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
