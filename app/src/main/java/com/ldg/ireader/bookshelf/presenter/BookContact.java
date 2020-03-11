package com.ldg.ireader.bookshelf.presenter;

import com.ldg.common.mvp.IMvpView;
import com.ldg.ireader.bookshelf.model.BookModel;
import com.ldg.ireader.bookshelf.model.ChapterModel;

import java.util.List;

public class BookContact {

    public interface View extends IMvpView {

        void updateInfo(BookModel bookModel);

        void getChapter(ChapterModel chapterModel);

        void getCatalogue(List<ChapterModel> catalogue);
    }
}
