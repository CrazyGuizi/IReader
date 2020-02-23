package com.ldg.ireader.bookshelf.core.loader;

import com.ldg.ireader.bookshelf.core.widgets.PageView;
import com.ldg.ireader.bookshelf.model.BookModel;
import com.ldg.ireader.bookshelf.model.TxtChapter;

import java.io.BufferedReader;

public class LocalPageLoader extends PageLoader {

    public LocalPageLoader(BookModel bookModel) {
        super(bookModel);
    }

    @Override
    protected boolean hasChapterData(TxtChapter chapter) {
        return false;
    }

    @Override
    protected BufferedReader getChapterReader(TxtChapter chapter) throws Exception {
        return null;
    }
}
