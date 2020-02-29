package com.ldg.common.http;


import com.ldg.common.HttpApiManager;
import com.ldg.httpprocessor.HttpAnnotation;

@HttpAnnotation(requestCode = 1234)
public class SimpleRequest {

    public String url() {
        return "http://www.baidu.com";
    }

    public Object getObj(String json) {
        return json;
    }
}
