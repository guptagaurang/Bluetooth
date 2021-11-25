package com.android.bluetoothmusic.legacy.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.bluetoothmusic.R;

public abstract class AttachmentViewFragment extends ObserverFragment implements View.OnClickListener {

    protected abstract void initializeSession();

    protected abstract void initializeView(View view);

    protected abstract void initializeEvent();

    protected abstract void toolbar();

    protected void initializeClicker(View view, int id) {
        view.findViewById(id).setOnClickListener(this);
    }

    protected void initializeClicker(View view) {
        view.setOnClickListener(this);
    }

}
