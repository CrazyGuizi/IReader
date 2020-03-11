package com.ldg.ireader.bookshelf.core.loader;

import com.ldg.ireader.bookshelf.model.BookModel;
import com.ldg.ireader.db.entity.DbBookRecord;

import java.io.BufferedReader;

public class LocalPageLoader extends PageLoader {

    public LocalPageLoader(BookModel bookModel) {
        super(bookModel);
    }

    @Override
    protected boolean hasChapterData() {
        return false;
    }

    @Override
    protected BufferedReader getChapterReader(DbBookRecord bookRecord){
        return null;
    }
}
