package com.ldg.common.http.response;

public interface Response {
    String url();

    Object getObj(String body);
}