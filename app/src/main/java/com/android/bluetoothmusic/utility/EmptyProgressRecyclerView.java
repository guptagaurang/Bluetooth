package com.android.bluetoothmusic.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

public class EmptyProgressRecyclerView extends RecyclerView {

    /**
     * The view to show if the list is empty.
     */
    private View emptyView;
    /**
     * View shown during loading.
     */
    private ProgressBar progressView;

    public EmptyProgressRecyclerView(Context context) {
        super(context);
    }


    public EmptyProgressRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyProgressRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Observer for list data. Sets the empty view if the list is empty.
     */
    private AdapterDataObserver emptyObserver = new AdapterDataObserver() {

        @Override
        public void onChanged() {
            Adapter<?> adapter = getAdapter();
            if (adapter != null && emptyView != null) {
                if (adapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    EmptyProgressRecyclerView.this.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    EmptyProgressRecyclerView.this.setVisibility(View.VISIBLE);
                }
            }

        }
    };

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(emptyObserver);
        }

        emptyObserver.onChanged();
    }

    /**
     * Sets the empty view.
     *
     * @param emptyView the {@link #emptyView}
     */
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    /**
     * Sets the progress view.
     *
     * @param progressView the {@link #progressView}.
     */
    public void setProgressView(ProgressBar progressView) {
        this.progressView = progressView;
    }

    /**
     * Shows the progress view.
     */
    public void startLoading() {
        // Hides the empty view.
        if (this.emptyView != null) {
            this.emptyView.setVisibility(GONE);
        }
        // Shows the progress bar.
        if (this.progressView != null) {
            this.progressView.setVisibility(VISIBLE);
        }
    }

    /**
     * Hides the progress view.
     */
    public void endLoading() {
        // Hides the progress bar.
        if (this.progressView != null) {
            this.progressView.setVisibility(GONE);
        }

        // Forces the view refresh.
        emptyObserver.onChanged();
    }
}