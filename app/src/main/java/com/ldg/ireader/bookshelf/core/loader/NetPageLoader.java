package com.ldg.ireader.bookshelf.core.loader;

import android.util.Log;

import com.ldg.ireader.bookshelf.model.BookModel;
import com.ldg.ireader.bookshelf.model.ChapterModel;
import com.ldg.ireader.bookshelf.model.TxtChapter;
import com.ldg.ireader.db.DbHelp;
import com.ldg.ireader.db.entity.DbBookRecord;
import com.ldg.ireader.subscribe.BookLoaderObservable;
import com.ldg.ireader.utils.PageHelper;

import java.io.BufferedReader;

public class NetPageLoader extends PageLoader implements BookLoaderObservable.Observer {

    public static final String content = PageHelper.json;

    public NetPageLoader(BookModel bookModel) {
        super(bookModel);
        if (!BookLoaderObservable.get().isRegister(this)) {
            BookLoaderObservable.get().regist(this);
        }
    }

    @Override
    public void refreshChapterList() {

    }

    @Override
    protected boolean hasChapterData() {
        DbBookRecord dbBookRecord = DbHelp.get().queryRecord(mBookModel.getId());
        if (dbBookRecord != null) {
            dbBookRecord.getChapterId();
        }
        return true;
    }

    @Override
    protected BufferedReader getChapterReader(TxtChapter chapter) throws Exception {
        return new BufferedReader(new PageHelper().getReader());
    }

    @Override
    public void onGetChapter(ChapterModel chapterModel) {
        Log.d("ldg", "onGetChapter: " + chapterModel.getContent());
    }
}
