package com.ldg.ireader.bookshelf.presenter;

import com.ldg.common.util.PairUtils;
import com.ldg.ireader.api.ApiConstants;
import com.ldg.ireader.bookshelf.model.ChapterModel;
import com.ldg.ireader.http.HttpUtils;
import com.ldg.ireader.http.ResponseListener;

import java.util.List;

public class BookRepository implements ResponseListener {

    private BookCallback mBookCallback;

    public BookRepository(BookCallback bookCallback) {
        mBookCallback = bookCallback;
    }

    public void getBook(String id) {
        HttpUtils.get(ApiConstants.REQUEST_GET_BOOK_DETAIL,
                PairUtils.init().add("id", id).build(), this);
    }

    public void getChapter(String bookId, String chapterId) {
        HttpUtils.get(ApiConstants.REQUEST_GET_CHAPTER,
                PairUtils.init().add("novel_id", bookId).add("chapter_id", chapterId).build(), this);
    }

    public void getCatalogue(String bookId) {
        HttpUtils.get(ApiConstants.REQUEST_GET_CATALOGUE,
                PairUtils.init().add("book_id", bookId).build(), this);
    }

    @Override
    public void onResponse(int reqCode, boolean isSuccess, Object res) {
        if (reqCode == ApiConstants.REQUEST_GET_BOOK_DETAIL) {
            if (mBookCallback != null) {
                mBookCallback.onUpdate(isSuccess, res);
            }
        } else if (reqCode == ApiConstants.REQUEST_GET_CHAPTER) {
            if (mBookCallback != null) {
                mBookCallback.onChapter(isSuccess, res);
            }
        } else if (reqCode == ApiConstants.REQUEST_GET_CATALOGUE) {
            if (mBookCallback != null) {
                mBookCallback.onCatalogue(isSuccess, (List<ChapterModel>) res);
            }
        }
    }

}
