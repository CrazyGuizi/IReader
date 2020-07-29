package com.ldg.ireader.api;

import com.ldg.annotation.HttpAnnotation;
import com.ldg.common.http.response.Response;
import com.ldg.common.util.JsonUtils;
import com.ldg.ireader.bookshelf.model.BookShelfMyBooksModel;

@HttpAnnotation(requestCode = ApiConstants.REQUEST_GET_MY_BOOK_INFO)
public class BookGetMyBookResponse implements Response {

    @Override
    public String url() {
        return ApiConstants.URL_GET_MY_BOOK_INFO;
    }

    @Override
    public Object getObj(String body) {
        return JsonUtils.toObj(body, BookShelfMyBooksModel.class);
    }
}
