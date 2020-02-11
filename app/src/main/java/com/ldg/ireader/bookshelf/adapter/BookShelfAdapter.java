package com.ldg.ireader.bookshelf.adapter;

import android.view.View;

import com.ldg.common.adapter.BaseRVAdapter;
import com.ldg.common.adapter.BaseViewHolder;
import com.ldg.ireader.R;

import java.util.List;

public class BookShelfAdapter extends BaseRVAdapter<String, BaseViewHolder> {

    public BookShelfAdapter(List<String> data) {
        super(R.layout.view_item_book_shelf, data);
    }


    @Override
    public void convert(BaseViewHolder holder, String item) {
        holder.setText(R.id.tv_book_name, item);
        holder.addClickListener(R.id.tv_book_name);
    }
}
