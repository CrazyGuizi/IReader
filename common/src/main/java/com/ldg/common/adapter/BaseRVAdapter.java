package com.ldg.common.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRVAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

    private List<T> mData = new ArrayList<>();

    private int mLayoutResId;

    private onItemChildClick mOnItemChildClick;

    public BaseRVAdapter(onItemChildClick onItemChildClick) {
        mOnItemChildClick = onItemChildClick;
    }

    public void setOnItemChildClick(onItemChildClick onItemChildClick) {
        mOnItemChildClick = onItemChildClick;
    }

    public onItemChildClick getOnItemChildClick() {
        return mOnItemChildClick;
    }

    public List<T> getData() {
        return mData;
    }

    public BaseRVAdapter(@LayoutRes int resId) {
        this(resId, new ArrayList<T>());
    }

    public BaseRVAdapter(List<T> data) {
        mData = data;
    }

    public BaseRVAdapter(@LayoutRes int resId, List<T> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        mData = data;
        mLayoutResId = resId;
    }

    public void setNewData(List<T> datas){
        if (datas == null) {
            datas = new ArrayList<>();
        }

        mData = datas;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutResId, parent, false);
        VH holder = (VH) new BaseViewHolder(view);
        holder.setAdapter(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (mData != null && !mData.isEmpty()) {
            convert(holder, mData.get(position));
        }
    }

    public abstract void convert(VH holder, T item);

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


    public interface onItemChildClick {
        void onViewClick(BaseRVAdapter adapter, View view, int position);
    }
}
