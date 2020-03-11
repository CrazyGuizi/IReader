package com.ldg.ireader.bookshelf.ui;

import android.app.Activity;
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

    private PageView mPageView;
    private IPageController mPageController;
    private BookPresenter mBookPresenter;
    private BookModel mBook;

    @Override
    public void doBeforeInit() {

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
        mPageController = new ReadPageManager(new BookModel());
        mPageView.setPageController(mPageController);

        mPageController.setLoaderListener(new PageLoader.PageLoaderListener() {
            @Override
            public void requestChapter(DbBookRecord... bookRecords) {
                if (mBookPresenter != null && bookRecords != null) {
                    for (DbBookRecord bookRecord : bookRecords) {
                        mBookPresenter.getChapter(bookRecord.getId(), bookRecord.getChapterId());
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

        mBookPresenter.getBook("1000");
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
