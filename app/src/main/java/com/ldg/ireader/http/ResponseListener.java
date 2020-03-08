package com.ldg.ireader.http;

public interface ResponseListener {
    void onResponse(int reqCode, boolean isSuccess, Object res);
}
