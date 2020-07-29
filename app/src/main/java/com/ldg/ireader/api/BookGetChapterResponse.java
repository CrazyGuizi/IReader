package com.ldg.ireader.api;

import com.ldg.annotation.HttpAnnotation;
import com.ldg.common.http.response.Response;
import com.ldg.common.util.JsonUtils;
import com.ldg.ireader.bookshelf.model.ChapterModel;

@HttpAnnotation(requestCode = ApiConstants.REQUEST_GET_CHAPTER)
public class BookGetChapterResponse implements Response {

    @Override
    public String url() {
        return ApiConstants.URL_GET_CHAPTER;
    }

    @Override
    public Object getObj(String body) {
        return JsonUtils.toObj(body, ChapterModel.class);
    }
}
