package com.ldg.ireader.bookshelf.presenter;

import android.text.TextUtils;

import com.ldg.common.mvp.IMvpView;
import com.ldg.common.mvp.MvpBasePresenter;
import com.ldg.common.util.PairUtils;
import com.ldg.ireader.api.ApiConstants;
import com.ldg.ireader.bookshelf.model.BookShelfMyBooksModel;
import com.ldg.ireader.http.HttpUtils;
import com.ldg.ireader.http.ResponseListener;

public class BookShelfPresenter extends MvpBasePresenter<BookShelfPresenter.BookShelfView> {

    private Repository mRepository;

    @Override
    public void onViewInit() {
        mRepository = new Repository(new BookShelfCallback() {
            @Override
            public void onFail() {
                if (!isViewAttached()) {
                    return;
                }

                getView().showException("");
            }

            @Override
            public void onMyBookInfo(BookShelfMyBooksModel bookModel) {
                if (bookModel != null && isViewAttached()) {
                    getView().updateBooks(bookModel);
                }
            }
        });
    }

    public void getMyBooks(String memberId) {
        if (mRepository != null) {
            mRepository.getMyBooks(memberId);
        }
    }

    public interface BookShelfView extends IMvpView {
        void updateBooks(BookShelfMyBooksModel booksModel);
    }

    public static class Repository implements ResponseListener {
        private BookShelfCallback mBookCallback;

        public Repository(BookShelfCallback bookCallback) {
            mBookCallback = bookCallback;
        }

        public void getMyBooks(String memberId) {
            HttpUtils.get(ApiConstants.REQUEST_GET_MY_BOOK_INFO,
                    PairUtils.init().add("member_id", memberId).build(),
                    this);
        }

        @Override
        public void onResponse(int reqCode, boolean isSuccess, Object res) {
            if (reqCode == ApiConstants.REQUEST_GET_MY_BOOK_INFO) {
                if (mBookCallback != null) {
                    if (!isSuccess || res == null || TextUtils.isEmpty(res.toString())) {
                        mBookCallback.onFail();
                    } else {
                        mBookCallback.onMyBookInfo((BookShelfMyBooksModel) res);
                    }
                }
            }
        }
    }

    public interface BookShelfCallback {
        void onFail();

        void onMyBookInfo(BookShelfMyBooksModel bookModel);
    }
}
