package com.ldg.ireader.bookshelf.core.loader;

import android.os.Build;
import android.util.Log;

import com.ldg.common.util.FileUtils;
import com.ldg.ireader.App;
import com.ldg.ireader.bookshelf.model.BookModel;
import com.ldg.ireader.bookshelf.model.ChapterModel;
import com.ldg.ireader.bookshelf.model.TxtChapter;
import com.ldg.ireader.db.DbHelp;
import com.ldg.ireader.db.entity.DbBookRecord;
import com.ldg.ireader.subscribe.BookLoaderObservable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;

public class NetPageLoader extends PageLoader implements BookLoaderObservable.Observer {

    public NetPageLoader(BookModel bookModel) {
        super(bookModel);
        if (!BookLoaderObservable.get().isRegister(this)) {
            BookLoaderObservable.get().regist(this);
        }
    }

    @Override
    protected boolean hasChapterData() {
        if (mDbBookRecord != null) {
            mCurChapterPos = mDbBookRecord.getPagePosition();
            return FileUtils.isExit(FileUtils.getBookCachePath(App.get(), mDbBookRecord.getId(), mDbBookRecord.getChapterName()));
        } else {
            mCurChapterPos = 0;
        }
        return false;
    }

    @Override
    protected BufferedReader getChapterReader(DbBookRecord bookRecord) {
        try {
            return new BufferedReader(new FileReader(FileUtils.getBookCachePath(App.get(), bookRecord.getId(), bookRecord.getChapterName())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onGetChapter(ChapterModel chapterModel) {
        FileUtils.saveFile(App.get(), chapterModel.getId(),
                chapterModel.getName(), chapterModel.getContent());

    }

    @Override
    public void notifyCatalogue(List<ChapterModel> catalogue) {
        if (catalogue != null && !catalogue.isEmpty()) {
            synchronized (mCatalogue) {
                Observable.fromIterable(catalogue)
                        .map(new Function<ChapterModel, TxtChapter>() {
                            @Override
                            public TxtChapter apply(ChapterModel chapterModel) throws Throwable {
                                return new TxtChapter(chapterModel);
                            }
                        })
                        .doAfterNext(new Consumer<TxtChapter>() {
                            @Override
                            public void accept(TxtChapter txtChapter) throws Throwable {
                                if (mDbBookRecord.getChapterId().equals(txtChapter.getId())) {
                                    mDbBookRecord.setPagePosition(mCatalogue.indexOf(txtChapter));
                                }
                            }
                        })
                        .subscribe(new Consumer<TxtChapter>() {
                            @Override
                            public void accept(TxtChapter txtChapter) throws Throwable {
                                mCatalogue.add(txtChapter);
                            }
                        });
            }
        }
    }
}
