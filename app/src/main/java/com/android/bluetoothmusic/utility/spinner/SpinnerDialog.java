package com.android.bluetoothmusic.utility.spinner;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.utility.DimensionUtils;
import com.android.bluetoothmusic.utility.DrawableUtils;

import java.util.List;

public class SpinnerDialog<Observer> extends SpinnerDialogAccess implements SearchView.OnQueryTextListener {

    private Context context;

    public RecyclerView spinnerRecyclerView;
    public TextView titleSpinnerDialog;
    public Button allowButton;
    public Button dismissButton;
    public SearchView searchEditText;
    private SpinnerDialogObserver<Observer> spinnerDialogObserver;
    private boolean isClose = true;

    public SpinnerDialog(@NonNull Context context) {
        super(context, R.style.SpinnerDialogTheme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout layoutContainer = new RelativeLayout(context);
        layoutContainer.setBackgroundColor(DrawableUtils.white(context));
        layoutContainer.setId(View.generateViewId());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutContainer.setLayoutParams(layoutParams);

        int height = DimensionUtils.convertDensityPixel(context, 56);
        RelativeLayout.LayoutParams layoutParamsTitle = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        RelativeLayout.LayoutParams layoutParamsSearch = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        RelativeLayout.LayoutParams layoutParamsRecyclerView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams layoutParamsButton = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        int padding = DimensionUtils.convertDensityPixel(context, 10);

        titleSpinnerDialog = new TextView(context);
        titleSpinnerDialog.setGravity(Gravity.CENTER);
        titleSpinnerDialog.setId(View.generateViewId());
        titleSpinnerDialog.setTextColor(DrawableUtils.white(context));
        titleSpinnerDialog.setBackground(DrawableUtils.listDrawable(context, 0f));
        titleSpinnerDialog.setPadding(padding, padding, padding, padding);

        searchEditText = new SearchView(context);
        searchEditText.setId(View.generateViewId());
        View v = searchEditText.findViewById(R.id.search_plate);
        v.setBackgroundColor(DrawableUtils.color_warning(context));
        searchEditText.setIconified(true);
        searchEditText.setPadding(padding, padding, padding, padding);
        searchEditText.setBackgroundColor(DrawableUtils.color_warning(context));
        layoutParamsSearch.addRule(RelativeLayout.BELOW, titleSpinnerDialog.getId());

        spinnerRecyclerView = new RecyclerView(context);
        spinnerRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        layoutParamsRecyclerView.addRule(RelativeLayout.BELOW, searchEditText.getId());

        LinearLayout layoutButton = new LinearLayout(context);
        layoutButton.setId(View.generateViewId());
        layoutParamsRecyclerView.addRule(RelativeLayout.ABOVE, layoutButton.getId());
        layoutParamsButton.addRule(RelativeLayout.ALIGN_PARENT_START);
        layoutParamsButton.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        int margin = DimensionUtils.convertDensityPixel(context, 2);

        LinearLayout.LayoutParams layoutParamsAllowButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsAllowButton.setMargins(margin, margin, margin, margin);

        LinearLayout.LayoutParams layoutParamsDismissButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsDismissButton.setMargins(margin, margin, margin, margin);

        allowButton = new Button(context);
        allowButton.setBackgroundColor(DrawableUtils.color_success(context));
        allowButton.setId(View.generateViewId());
        layoutParamsAllowButton.weight = 1;
        allowButton.setLayoutParams(layoutParamsAllowButton);
        layoutButton.addView(allowButton);

        dismissButton = new Button(context);
        dismissButton.setBackgroundColor(DrawableUtils.color_danger(context));
        dismissButton.setId(View.generateViewId());
        layoutParamsDismissButton.weight = 1;
        dismissButton.setLayoutParams(layoutParamsDismissButton);
        layoutButton.addView(dismissButton);

        layoutContainer.addView(titleSpinnerDialog, layoutParamsTitle);
        layoutContainer.addView(searchEditText, layoutParamsSearch);
        layoutContainer.addView(spinnerRecyclerView, layoutParamsRecyclerView);

        layoutContainer.addView(layoutButton, layoutParamsButton);

        searchEditText.setOnQueryTextListener(this);
        allowButton.setOnClickListener(this);
        dismissButton.setOnClickListener(this);
        setOnKeyListener(this);

        setContentView(layoutContainer);

        initializeView();

    }

    @Override
    protected void initializeView() {
    }

    @Override
    public void setTitleText(String title) {
        titleSpinnerDialog.setText(title);

    }

    @Override
    public void setTitleTextSize(float size) {
        titleSpinnerDialog.setTextSize(size);
    }

    @Override
    public void setSearchHint(String name) {
        searchEditText.setQueryHint(name);
    }

    @Override
    public void isClosable(boolean isClose) {
        this.isClose = isClose;
    }

    @Override
    public void setAllowButton(String name) {
        allowButton.setText(name);
    }

    @Override
    public void setDismissButton(String name) {
        dismissButton.setText(name);
    }

    @Override
    public void setVisibilitySearch(int visibility) {
        searchEditText.setVisibility(visibility);
    }

    @Override
    public void setAllowVisibilityButton(int visibility) {
        allowButton.setVisibility(visibility);
    }

    @Override
    public void setDismissVisibilityButton(int visibility) {
        dismissButton.setVisibility(visibility);
    }

    @Override
    protected void filter(String text) {
        spinnerDialogObserver.filter(text);
    }

    public RecyclerView getSpinnerRecyclerView() {
        return spinnerRecyclerView;
    }

    private View bind(int id) {
        return findViewById(id);
    }

    public void display() {
        if (!isShowing()) {
            show();
        }
    }

    public void close() {
        if (isShowing()) {
            dismiss();
        }
    }

    @Override
    public void onClick(View view) {

        if (view == allowButton) {
            if (spinnerDialogObserver != null) {
                spinnerDialogObserver.success();
                if (isClose) {
                    close();
                }
            }
        }

        if (view == dismissButton) {
            if (spinnerDialogObserver != null) {
                spinnerDialogObserver.failure();
                close();
            }
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !event.isCanceled()) {
            //your logic here for back button pressed event
            dismiss();

            return true;
        }

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String text) {
        filter(text);
        return false;
    }

    public interface SpinnerDialogObserver<Observer> {
        void filter(String text);

        void selectedObserver(Observer observer);

        void selectedAdapterPosition(Observer observer, List<Observer> observers, int adapterPosition);

        void success();

        void failure();
    }

    public void setSpinnerDialogObserver(SpinnerDialogObserver<Observer> spinnerDialogObserver) {
        this.spinnerDialogObserver = spinnerDialogObserver;
    }
}
