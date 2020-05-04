package com.ldg.ireader.bookshelf.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.ldg.common.adapter.BaseRVAdapter;
import com.ldg.common.adapter.BaseViewHolder;
import com.ldg.ireader.R;
import com.ldg.ireader.bookshelf.model.ChapterModel;

import java.util.List;

public class BookCatalogueAdapter extends BaseRVAdapter<ChapterModel, BaseViewHolder> {

    public BookCatalogueAdapter(List<ChapterModel> data) {
        super(R.layout.view_item_book_catalogue, data);
    }

    @Override
    public void convert(BaseViewHolder holder, ChapterModel chapterModel) {
        TextView name = holder.getView(R.id.tv_name);
        if (chapterModel.isReading()) {
            name.setTextColor(Color.parseColor("#3f99e0"));
        } else {
            name.setTextColor(Color.parseColor("#1e1e1e"));
        }
        name.setText(chapterModel.getName());

        holder.addClickListener(R.id.ll_item);
    }

    public int getCurChapterPosition() {
        if (getData() != null) {
            for (int i = 0; i < getData().size(); i++) {
                if (getData().get(i).isReading()) {
                    return i;
                }
            }
        }

        return -1;
    }
}
