package com.ldg.ireader.bookshelf.core.loader;

import android.text.TextUtils;

import com.ldg.common.log.LogUtil;
import com.ldg.common.util.FileUtils;
import com.ldg.ireader.App;
import com.ldg.ireader.bookshelf.model.BookModel;
import com.ldg.ireader.bookshelf.model.ChapterModel;
import com.ldg.ireader.db.entity.DbBookRecord;
import com.ldg.ireader.subscribe.BookLoaderObservable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.List;

public class NetPageLoader extends PageLoader implements BookLoaderObservable.Observer {

    public NetPageLoader(BookModel bookModel) {
        super(bookModel);
        if (!BookLoaderObservable.get().isRegister(this)) {
            BookLoaderObservable.get().regist(this);
        }
    }

    @Override
    public void release() {
        if (BookLoaderObservable.get().isRegister(this)) {
            BookLoaderObservable.get().unregist(this);
        }
    }

    @Override
    protected boolean hasChapterData() {
        return hasChapterData(mDbBookRecord.getId(), mCurChapterId, mDbBookRecord.getChapterName());
    }

    @Override
    protected boolean hasChapterData(String bookId, String chapterId, String chapterName) {
        boolean hasData = mChapterCache != null && mChapterCache.get(chapterId) != null;
        if (!hasData) {
            hasData = FileUtils.isExit(FileUtils.getBookCachePath(App.get(), bookId, chapterName));
        }

        return hasData;
    }

    @Override
    protected BufferedReader getChapterReader(DbBookRecord bookRecord) {
        BufferedReader bufferedReader = null;
        if (mChapterCache != null && mChapterCache.get(bookRecord.getChapterId()) != null) {
            bufferedReader = new BufferedReader(new StringReader(mChapterCache.get(bookRecord.getChapterId()).getContent()));
        } else if (bufferedReader == null) {
            try {
                String bookCachePath = FileUtils.getBookCachePath(App.get(), bookRecord.getId(), bookRecord.getChapterName());
                LogUtil.d("读取位置:" + bookCachePath);
                bufferedReader = new BufferedReader(new FileReader(bookCachePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return bufferedReader;
    }

    @Override
    public void onGetChapter(ChapterModel chapterModel) {
        putCache(chapterModel);
        FileUtils.saveFile(App.get(), chapterModel.getNovelId(),
                chapterModel.getName(), chapterModel.getContent());

        if (mDbBookRecord == null) {
            mDbBookRecord = new DbBookRecord(chapterModel.getNovelId(), chapterModel.getId(), chapterModel.getName());
            saveDbCurProgress();
        }

        if (mStatus == LoadingStatus.STATUS_LOADING
                && TextUtils.equals(mCurChapterId, chapterModel.getId())) {
            notifyStatus(LoadingStatus.STATUS_FINISH);
        }
    }

    @Override
    public void notifyCatalogue(List<ChapterModel> catalogue) {
        if (catalogue != null && !catalogue.isEmpty()) {
            synchronized (mCatalogue) {
                mCatalogue = catalogue;
                if (TextUtils.isEmpty(mCurChapterId)) {
                    mCurChapterId = mCatalogue.get(0).getId();
                    notifyStatus(LoadingStatus.STATUS_LOADING);
                }

                if (mPageLoaderListener != null) {
                    mPageLoaderListener.changeCurChapter(mCurChapterId);
                }
            }
        }
    }
}
