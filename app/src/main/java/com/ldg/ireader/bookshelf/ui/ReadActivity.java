package com.ldg.ireader.bookshelf.ui;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ldg.common.adapter.BaseRVAdapter;
import com.ldg.common.view.activity.BaseActivity;
import com.ldg.ireader.R;
import com.ldg.ireader.bookshelf.adapter.BookCatalogueAdapter;
import com.ldg.ireader.bookshelf.core.PageControllerListener;
import com.ldg.ireader.bookshelf.core.PageStyle;
import com.ldg.ireader.bookshelf.core.anim.PageAnimation;
import com.ldg.ireader.bookshelf.core.anim.PageMode;
import com.ldg.ireader.bookshelf.core.config.PageConfig;
import com.ldg.ireader.bookshelf.core.draw.IPageController;
import com.ldg.ireader.bookshelf.core.draw.ReadPageManager;
import com.ldg.ireader.bookshelf.core.loader.PageLoader;
import com.ldg.ireader.bookshelf.core.widgets.PageView;
import com.ldg.ireader.bookshelf.model.BookModel;
import com.ldg.ireader.bookshelf.model.ChapterModel;
import com.ldg.ireader.bookshelf.presenter.BookContact;
import com.ldg.ireader.bookshelf.presenter.BookPresenter;
import com.ldg.ireader.bookshelf.widgets.SimpleDrawerLayout;
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
    private ReadSettingFragment mSettingFragment;
    private SimpleDrawerLayout mDrawerLayout;
    private RecyclerView mCatalogueList;
    private BookCatalogueAdapter mCatalogueAdapter;

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
        mCatalogueList = findViewById(R.id.rv_catalogue);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        initRecycler();
    }

    private void initRecycler() {
        mCatalogueAdapter = new BookCatalogueAdapter(null);
        mCatalogueAdapter.setOnItemChildClick(new BaseRVAdapter.onItemChildClick() {
            @Override
            public void onViewClick(BaseRVAdapter adapter, View view, int position) {
                Log.d(TAG, "onViewClick: " + adapter.getData().get(position));
                mDrawerLayout.close();
                ChapterModel model = mCatalogueAdapter.getData().get(position);
                mPageController.jumpChapter(model.getId());
                changeCurChapter(model.getId());
            }
        });

        mCatalogueList.setLayoutManager(new LinearLayoutManager(this));
        mCatalogueList.setAdapter(mCatalogueAdapter);
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
        mDrawerLayout.setEnableScroll(PageConfig.get().getPageMode() != PageMode.SCROLL);
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

            @Override
            public void changeCurChapter(String chapterId) {
                ReadActivity.this.changeCurChapter(chapterId);
            }
        });

        mPageController.setControllerListener(new PageControllerListener() {
            @Override
            public void onClickPageCenter() {
                showSetting();
            }

            @Override
            public boolean interceptTouch() {
                if (mSettingFragment != null && mSettingFragment.isVisible()) {
                    mSettingFragment.dismiss();
                    return true;
                }

                if (mDrawerLayout != null && mDrawerLayout.isOpened()) {
                    mDrawerLayout.close();
                    return true;
                }

                return false;
            }
        });
    }

    private void changeCurChapter(String chapterId) {
        List<ChapterModel> data = mCatalogueAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            if (TextUtils.equals(chapterId, data.get(i).getId())) {
                data.get(i).setReading(true);
            } else {
                data.get(i).setReading(false);
            }
        }

        mCatalogueAdapter.notifyDataSetChanged();
    }

    private void showSetting() {
        if (mSettingFragment == null) {
            mSettingFragment = new ReadSettingFragment();
        }

        mSettingFragment.setCallback(new ReadSettingFragment.Callback() {
            @Override
            public void onClickBack() {
                finish();
            }

            @Override
            public void onClickMenu() {
                if (!mDrawerLayout.isOpened()) {
                    mDrawerLayout.open();
                    int curPos = mCatalogueAdapter.getCurChapterPosition();
                    if (curPos >= 0) {
                        mCatalogueList.scrollToPosition(curPos);
                    }
                }
            }

            @Override
            public void onChangeColor(PageStyle style) {
                mPageController.updateConfig();
            }

            @Override
            public void onChangeFont(int size) {
                if (PageConfig.get().getTextSize() != size) {
                    PageConfig.get().setTextSize(size);
                    mPageController.updateConfig();
                }
            }

            @Override
            public void onChangeAnimMode(PageMode pageMode) {
                mDrawerLayout.setEnableScroll(pageMode != PageMode.SCROLL);
                mPageController.updateConfig();
            }
        });

        mSettingFragment.show(getSupportFragmentManager(), "setting");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPageController.saveReadProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSettingFragment != null && mSettingFragment.isVisible()) {
            mSettingFragment.dismiss();
        }
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
        mCatalogueAdapter.setNewData(catalogue);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode && mDrawerLayout != null && mDrawerLayout.isOpened()) {
            mDrawerLayout.close();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
