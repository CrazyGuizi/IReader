package com.ldg.ireader.bookshelf.adapter;

import android.view.View;

import com.ldg.common.adapter.BaseRVAdapter;
import com.ldg.common.adapter.BaseViewHolder;
import com.ldg.ireader.R;
import com.ldg.ireader.bookshelf.model.BookModel;

import java.util.List;

public class BookShelfAdapter extends BaseRVAdapter<BookModel, BaseViewHolder> {

    public BookShelfAdapter(List<BookModel> data) {
        super(R.layout.view_item_book_shelf, data);
    }


    @Override
    public void convert(BaseViewHolder holder, BookModel bookModel) {
        holder.setText(R.id.tv_book_name, bookModel.getName());
        holder.addClickListener(R.id.tv_book_name);
    }
}
