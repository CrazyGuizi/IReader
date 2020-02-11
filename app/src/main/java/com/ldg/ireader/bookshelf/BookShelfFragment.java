package com.ldg.ireader.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ldg.common.adapter.BaseRVAdapter;
import com.ldg.common.view.BaseFragment;
import com.ldg.ireader.R;
import com.ldg.ireader.bookshelf.adapter.BookShelfAdapter;
import com.ldg.ireader.ui.ReadActivity;

import java.util.ArrayList;
import java.util.List;


public class BookShelfFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private BookShelfAdapter mBookShelfAdapter;
    private List<String> mBooks = new ArrayList<>();

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
    }

    @Override
    protected void bindData() {
        for (int i = 0; i < 10; i++) {
            mBooks.add(i, "测试" + i);
        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBookShelfAdapter = new BookShelfAdapter(mBooks);

        mBookShelfAdapter.setOnItemChildClick(new BaseRVAdapter.onItemChildClick() {
            @Override
            public void onViewClick(BaseRVAdapter adapter, View view, int position) {
                startActivity(new Intent(getActivity(), ReadActivity.class));
            }
        });

        mRecyclerView.setAdapter(mBookShelfAdapter);
    }
}
