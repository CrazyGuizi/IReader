package com.ldg.ireader.bookshelf.presenter;

import com.ldg.ireader.bookshelf.model.ChapterModel;

import java.util.List;

public interface BookCallback {
    public void onUpdate(boolean isSuccess, Object res);

    void onChapter(boolean isSuccess, Object res);

    void onCatalogue(boolean isSuccess, List<ChapterModel> catalogues);
}
