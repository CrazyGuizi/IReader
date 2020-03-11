package com.ldg.ireader.api;

import com.ldg.common.http.response.Response;
import com.ldg.common.util.JsonUtils;
import com.ldg.httpprocessor.HttpAnnotation;
import com.ldg.ireader.bookshelf.model.ChapterModel;

@HttpAnnotation(requestCode = ApiConstants.REQUEST_GET_CATALOGUE)
public class BookGetCatalogueResponse implements Response {

    @Override
    public String url() {
        return ApiConstants.URL_GET_CATALOGUE;
    }

    @Override
    public Object getObj(String body) {
        return JsonUtils.toList(body, ChapterModel.class);
    }
}
