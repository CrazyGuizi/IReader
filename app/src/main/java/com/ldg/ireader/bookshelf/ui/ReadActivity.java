package com.ldg.ireader.bookshelf.ui;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.ldg.common.view.activity.BaseActivity;
import com.ldg.ireader.R;
import com.ldg.ireader.bookshelf.core.draw.IPageController;
import com.ldg.ireader.bookshelf.core.draw.ReadPageManager;
import com.ldg.ireader.bookshelf.core.loader.PageLoader;
import com.ldg.ireader.bookshelf.core.widgets.PageView;
import com.ldg.ireader.bookshelf.model.BookModel;
import com.ldg.ireader.bookshelf.model.ChapterModel;
import com.ldg.ireader.bookshelf.presenter.BookContact;
import com.ldg.ireader.bookshelf.presenter.BookPresenter;
import com.ldg.ireader.db.entity.DbBookRecord;
import com.ldg.ireader.subscribe.BookLoaderObservable;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class ReadActivity extends BaseActivity implements BookContact.View {

    public static final String TAG = ReadActivity.class.getSimpleName();

    public static final String KEY_BOOK_MODEL = "key_book_model";

    private PageView mPageView;
    private IPageController mPageController;
    private BookPresenter mBookPresenter;
    private BookModel mBook;

    @Override
    public void doBeforeInit() {
        mBook = (BookModel) getIntent().getSerializableExtra(KEY_BOOK_MODEL);
        if (mBook == null) {
            finish();
            return;
        }
    }

    @Override
    public int getLayoutView() {
        return R.layout.activity_read;
    }

    @Override
    public void initWidgets() {
        mPageView = findViewById(R.id.page_view);
    }

    @Override
    public void setListener() {

    }

    @Override
    protected void createPresenter() {
        mBookPresenter = new BookPresenter();
        if (!mBookPresenter.isViewAttached()) {
            mBookPresenter.attach(this);
        }
        mBookPresenter.onViewInit();
    }

    @Override
    public void doAfterInit() {
        mPageController = new ReadPageManager(mBook);
        mPageController.attachView(mPageView);

        mPageController.setLoaderListener(new PageLoader.PageLoaderListener() {
            @Override
            public void requestChapter(String bookId, String chapterId) {
                if (mBookPresenter != null) {
                    if (!TextUtils.isEmpty(bookId) &&
                            !TextUtils.isEmpty(chapterId)) {
                        mBookPresenter.getChapter(bookId, chapterId);
                    }
                }
            }

            @Override
            public void requestCatalogue(String bookId) {
                if (mBookPresenter != null) {
                    mBookPresenter.getCatalogue(bookId);
                }
            }
        });
    }

    @Override
    public void updateInfo(BookModel bookModel) {
        mBook = bookModel;
        Log.d("ldg", "updateInfo: " + bookModel.getName());
    }

    @Override
    public void getChapter(ChapterModel chapterModel) {
        if (chapterModel != null) {
            BookLoaderObservable.get().notifyUpdateChapter(chapterModel);
        }
    }

    @Override
    public void getCatalogue(List<ChapterModel> catalogue) {
        if (catalogue != null && !catalogue.isEmpty()) {
            BookLoaderObservable.get().notifyCatalogue(catalogue);
        }
    }

    @Override
    public void showEmpty(String msg) {

    }

    @Override
    public void showException(String msg) {

    }

    @Override
    public Activity getHostActivity() {
        return this;
    }
}
