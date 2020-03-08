package com.ldg.ireader.bookshelf.presenter;

import android.text.TextUtils;

import com.ldg.common.mvp.MvpBasePresenter;
import com.ldg.common.util.JsonUtils;
import com.ldg.ireader.bookshelf.model.BookModel;
import com.ldg.ireader.bookshelf.model.ChapterModel;

public class BookPresenter extends MvpBasePresenter<BookContact.View> {

    private BookRepository mRepository;

    @Override
    public void onViewInit() {
        mRepository = new BookRepository(new BookCallback() {
            @Override
            public void onUpdate(boolean isSuccess, Object res) {
                if (!isViewAttached()) {
                    return;
                }

                if (!isSuccess || res == null || TextUtils.isEmpty(res.toString())) {
                    getView().showException("");
                    return;
                }

                BookModel bookModel = JsonUtils.toObj(res.toString(), BookModel.class);
                getView().updateInfo(bookModel);
            }

            @Override
            public void onChapter(boolean isSuccess, Object res) {
                if (!isViewAttached()) {
                    return;
                }

                if (!isSuccess || res == null) {
                    getView().showException("");
                    return;
                }

                getView().getChapter((ChapterModel) res);
            }
        });
    }

    public void getBook(String id) {
        if (mRepository != null) {
            mRepository.getBook(id);
        }
    }

    public void getChapter(String bookId, String chapterId) {
        if (mRepository != null) {
            mRepository.getChapter(bookId, chapterId);
        }
    }
}
