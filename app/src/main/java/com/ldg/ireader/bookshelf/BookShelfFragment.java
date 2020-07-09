package com.ldg.ireader.bookshelf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ldg.common.adapter.BaseRVAdapter;
import com.ldg.common.util.ToastUtils;
import com.ldg.common.view.BaseFragment;
import com.ldg.ireader.R;
import com.ldg.ireader.bookshelf.adapter.BookShelfAdapter;
import com.ldg.ireader.bookshelf.model.BookModel;
import com.ldg.ireader.bookshelf.model.BookShelfMyBooksModel;
import com.ldg.ireader.bookshelf.presenter.BookShelfPresenter;
import com.ldg.ireader.bookshelf.ui.ReadActivity;

import java.util.ArrayList;
import java.util.List;


public class BookShelfFragment extends BaseFragment implements BookShelfPresenter.BookShelfView {

    public static final String TAG = BookShelfFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private BookShelfAdapter mBookShelfAdapter;
    private List<BookModel> mBooks = new ArrayList<>();
    private BookShelfPresenter mPresenter;

    public static BookShelfFragment newInstance(Bundle args) {
        BookShelfFragment fragment = new BookShelfFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutView() {
        return R.layout.frag_book_shelf;
    }

    @Override
    protected void initWidgets() {
        mRecyclerView = mRoot.findViewById(R.id.recycler_view);
        initRecyclerView();
    }

    @Override
    protected void createPresenter() {
        mPresenter = new BookShelfPresenter();
        if (!mPresenter.isViewAttached()) {
            mPresenter.attach(this);
        }

        mPresenter.onViewInit();
    }

    @Override
    protected void bindData() {

    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false));

        mBookShelfAdapter = new BookShelfAdapter(mBooks);

        mBookShelfAdapter.setOnItemChildClick(new BaseRVAdapter.onItemChildClick() {
            @Override
            public void onViewClick(BaseRVAdapter adapter, View view, int position) {
                if (position < 0 || position >= mBooks.size()) {
                    return;
                }

                Intent intent = new Intent(getActivity(), ReadActivity.class);
                intent.putExtra(ReadActivity.KEY_BOOK_MODEL, mBooks.get(position));
                startActivity(intent);
            }
        });

        mRecyclerView.setAdapter(mBookShelfAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBooks.isEmpty()) {
            mPresenter.getMyBooks("1");
        }
    }

    @Override
    public void updateBooks(BookShelfMyBooksModel booksModel) {
        if (booksModel != null && booksModel.getBooks() != null) {
            mBooks = booksModel.getBooks();
            mBookShelfAdapter.setNewData(mBooks);
        }
    }

    @Override
    public void showEmpty(String msg) {

    }

    @Override
    public void showException(String msg) {
        ToastUtils.show(getContext(), msg);
        BookShelfMyBooksModel booksModel = new BookShelfMyBooksModel();
        List<BookModel> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            BookModel bookModel = new BookModel();
            bookModel.setName("书本" + i);
            bookModel.setCoverUrl("http://cdn-app-qn-bj.colorv.cn/photos/02640543960c3777/de2be8015d0c4856ab1adb870c96dbc3.jpg");
            list.add(bookModel);
        }
        booksModel.setBooks(list);
        updateBooks(booksModel);
    }

    @Override
    public Activity getHostActivity() {
        return getActivity();
    }
}
