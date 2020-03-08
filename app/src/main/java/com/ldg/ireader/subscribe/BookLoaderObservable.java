package com.ldg.ireader.subscribe;

import com.ldg.common.subscribe.BaseObserver;
import com.ldg.ireader.bookshelf.model.ChapterModel;

public class BookLoaderObservable extends BaseObserver<BookLoaderObservable.Observer> {

    private static final BookLoaderObservable INSTANCE = new BookLoaderObservable();

    private BookLoaderObservable() {
    }

    public static BookLoaderObservable get() {
        return INSTANCE;
    }

    public void notifyUpdateChapter(ChapterModel chapterModel) {
        synchronized (mObservables) {
            for (Observer observer : mObservables) {
                observer.onGetChapter(chapterModel);
            }
        }
    }


    public interface Observer {
        void onGetChapter(ChapterModel chapterModel);
    }
}
