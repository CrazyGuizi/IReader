package com.ldg.common.http;

import okhttp3.Response;

public abstract class HttpHandler {

    public abstract void handle(Response response);

    public abstract void requestFail(String msg);
}
