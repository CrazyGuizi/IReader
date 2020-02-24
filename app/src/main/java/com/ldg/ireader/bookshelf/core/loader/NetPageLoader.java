package com.ldg.ireader.bookshelf.core.loader;

import com.ldg.ireader.bookshelf.model.BookModel;
import com.ldg.ireader.bookshelf.model.TxtChapter;
import com.ldg.ireader.utils.PageHelper;
import com.ldg.ireader.bookshelf.core.widgets.PageView;

import java.io.BufferedReader;

public class NetPageLoader extends PageLoader {

    public static final String content = PageHelper.json;

    public NetPageLoader(BookModel bookModel) {
        super(bookModel);
    }

    @Override
    public void refreshChapterList() {

    }

    @Override
    protected boolean hasChapterData(TxtChapter chapter) {
        return true;
    }

    @Override
    protected BufferedReader getChapterReader(TxtChapter chapter) throws Exception {
        return new BufferedReader(new PageHelper().getReader());
    }
}
