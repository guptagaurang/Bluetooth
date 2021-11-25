package com.android.bluetoothmusic.fragment.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.adapter.BaseHolder;
import com.android.bluetoothmusic.adapter.GenericRecordsAdapter;
import com.android.bluetoothmusic.legacy.fragment.AttachmentViewFragment;
import com.android.bluetoothmusic.models.PremiumPlaylistViewer;

import java.util.ArrayList;
import java.util.List;

public class PremiumPlaylistFragment extends AttachmentViewFragment {

    private Context context;
    private ViewPager2 playlistContainerPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_premium_playlist, container, false);
        context = getActivity();
        initializeView(inflateView);
        return inflateView;
    }

    @Override
    protected void initializeSession() {

    }

    @Override
    protected void initializeView(View view) {
        playlistContainerPager = (ViewPager2) bindView(view, R.id.playlistContainerPager);
        List<PremiumPlaylistViewer> premiumPlaylistViewers = new ArrayList<>();
        premiumPlaylistViewers.add(new PremiumPlaylistViewer());
        premiumPlaylistViewers.add(new PremiumPlaylistViewer());
        premiumPlaylistViewers.add(new PremiumPlaylistViewer());
        premiumPlaylistViewers.add(new PremiumPlaylistViewer());
        adapter.addItemsObserver(premiumPlaylistViewers);
        playlistContainerPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnClickObserver(new OnPremiumPlaylistObserver() {
            @Override
            public void onPlayMusic() {

            }

            @Override
            public void onPlayMusic(int adapterPosition) {

            }
        });
    }

    @Override
    protected void initializeEvent() {

    }

    @Override
    protected void toolbar() {

    }

    @Override
    public void onClick(View view) {

    }

    GenericRecordsAdapter<PremiumPlaylistViewer, OnPremiumPlaylistObserver> adapter = new GenericRecordsAdapter<PremiumPlaylistViewer, OnPremiumPlaylistObserver>() {
        private OnPremiumPlaylistObserver observer;

        @Override
        public BaseHolder<PremiumPlaylistViewer, OnPremiumPlaylistObserver> setViewHolder(ViewGroup parent, int typeOfView, OnPremiumPlaylistObserver onRecentlyPlaylistObserver) {
            int layout_premium_playlist = R.layout.layout_premium_playlist;
            return new RecentlyPlaylistViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_premium_playlist, parent, false));
        }

        @Override
        protected void onBindData(BaseHolder<PremiumPlaylistViewer, OnPremiumPlaylistObserver> holder, int position, PremiumPlaylistViewer playlistViewer, List<PremiumPlaylistViewer> playlistViewers, OnPremiumPlaylistObserver observer) {
            this.observer = observer;
        }

        class RecentlyPlaylistViewHolder extends BaseHolder<PremiumPlaylistViewer, OnPremiumPlaylistObserver> {

            public RecentlyPlaylistViewHolder(View itemView) {
                super(itemView);
            }

            @Override
            public void onBind(int position, PremiumPlaylistViewer playlistViewer, List<PremiumPlaylistViewer> records, OnPremiumPlaylistObserver observer) {
            }

            @Override
            public void onClick(View view) {
                observer.onPlayMusic(getAdapterPosition());
            }
        }

    };

    public interface OnPremiumPlaylistObserver {

        void onPlayMusic();

        void onPlayMusic(int adapterPosition);
    }
}
