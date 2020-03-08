package com.ldg.ireader.bookshelf.presenter;

import com.ldg.common.mvp.IMvpView;
import com.ldg.ireader.bookshelf.model.BookModel;
import com.ldg.ireader.bookshelf.model.ChapterModel;

public class BookContact {

    public interface View extends IMvpView {

        void updateInfo(BookModel bookModel);

        void getChapter(ChapterModel chapterModel);
    }
}
