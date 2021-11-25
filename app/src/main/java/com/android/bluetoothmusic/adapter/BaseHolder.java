package com.android.bluetoothmusic.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;

import java.util.List;

public abstract class BaseHolder<Model, Listener> extends RecyclerView.ViewHolder implements View.OnClickListener {
    public boolean isWorking = false;

    public BaseHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBind(int position, Model model, List<Model> records, Listener listener);

    protected View bindView(View view, int id) {
        return view.findViewById(id);
    }

    public String initializePrettyPrintJson(Object object) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(object);
    }
}