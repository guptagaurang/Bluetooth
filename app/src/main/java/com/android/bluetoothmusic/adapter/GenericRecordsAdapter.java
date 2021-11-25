package com.android.bluetoothmusic.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class GenericRecordsAdapter<Model, Listener> extends RecyclerView.Adapter<BaseHolder<Model, Listener>> {

    private List<Model> records;
    private Listener listener;

    public abstract BaseHolder<Model, Listener> setViewHolder(ViewGroup parent, int typeOfView, Listener listener);

    protected abstract void onBindData(BaseHolder<Model, Listener> holder, int position, Model model, List<Model> records, Listener listener);

    @NonNull
    @Override
    public BaseHolder<Model, Listener> onCreateViewHolder(@NonNull ViewGroup parent, int typeOfView) {
        return setViewHolder(parent, typeOfView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseHolder<Model, Listener> holder, int position) {
        onBindData(holder, position, records.get(position), records, listener);
        Model item = records.get(position);
        holder.onBind(position, records.get(position), records, listener);
    }

    @Override
    public int getItemCount() {
        return records != null ? records.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItemsObserver(List<Model> records) {
        this.records = records;
        this.notifyDataSetChanged();
    }

    public void filter(List<Model> records) {
        this.records = records;
        this.notifyDataSetChanged();
    }

    public List<Model> getRecords() {
        return records;
    }

    public Model getRecord(int position) {
        return records.get(position);
    }

    public void setOnClickObserver(Listener listener) {
        this.listener = listener;
    }
}
