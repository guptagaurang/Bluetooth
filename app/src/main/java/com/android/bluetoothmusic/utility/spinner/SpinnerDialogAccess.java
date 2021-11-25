package com.android.bluetoothmusic.utility.spinner;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;

public abstract class SpinnerDialogAccess extends Dialog implements View.OnClickListener, DialogInterface.OnKeyListener {

    public SpinnerDialogAccess(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected abstract void initializeView();

    protected abstract void setTitleText(String title);

    protected abstract void setTitleTextSize(float size);

    protected abstract void setSearchHint(String name);

    protected abstract void isClosable(boolean isClose);

    protected abstract void setAllowButton(String name);

    protected abstract void setDismissButton(String name);

    protected abstract void setVisibilitySearch(int visibility);

    protected abstract void setAllowVisibilityButton(int visibility);

    protected abstract void setDismissVisibilityButton(int visibility);

    protected abstract void filter(String text);

}
