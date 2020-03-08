package com.ldg.ireader.api;

import com.ldg.common.http.response.Response;
import com.ldg.httpprocessor.HttpAnnotation;

@HttpAnnotation(requestCode = ApiConstants.REQUEST_GET_BOOK_DETAIL)
public class BookDetailResponse implements Response {
    @Override
    public String url() {
        return ApiConstants.URL_GET_BOOK_DETAIL;
    }

    @Override
    public Object getObj(String body) {
        return body;
    }
}
