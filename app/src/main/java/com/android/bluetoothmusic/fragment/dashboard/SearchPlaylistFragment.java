package com.android.bluetoothmusic.fragment.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.activity.DashboardObserverActivity;
import com.android.bluetoothmusic.adapter.BaseHolder;
import com.android.bluetoothmusic.adapter.GenericRecordsAdapter;
import com.android.bluetoothmusic.legacy.fragment.AttachmentViewFragment;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;
import com.android.bluetoothmusic.models.Audio;
import com.android.bluetoothmusic.models.music.BestArtistsPlaylist;
import com.android.bluetoothmusic.models.music.RecentlyPlaylist;
import com.android.bluetoothmusic.models.music.RecentlyPlaylistClickObserver;
import com.android.bluetoothmusic.utility.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchPlaylistFragment extends AttachmentViewFragment {

    private Context context;
    private List<RecentlyPlaylist> playlists = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_search_playlist, container, false);
        context = getActivity();
        initializeView(inflateView);
        return inflateView;
    }

    @Override
    protected void initializeSession() {

    }

    @Override
    protected void initializeView(View view) {
        SearchView playlistSearchView = (SearchView) bindView(view, R.id.playlistSearchView);
        SwipeRefreshLayout swipeRefreshLayoutPlaylistFragment = (SwipeRefreshLayout) bindView(view, R.id.swipeRefreshLayoutPlaylistFragment);
        RecyclerView recyclerViewPlaylistFragment = (RecyclerView) bindView(view, R.id.recyclerViewPlaylistFragment);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerViewPlaylistFragment.setLayoutManager(linearLayoutManager);

        swipeRefreshLayoutPlaylistFragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayoutPlaylistFragment.setRefreshing(false);
                onPlayMusic();
            }
        });

        playlistSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (TextUtils.isEmpty(query)) {
                    onPlayMusic();
                } else {
                    List<RecentlyPlaylist> recentlyPlaylists = new ArrayList<>();
                    for (RecentlyPlaylist recentlyPlaylist : adapter.getRecords()) {
                        if (recentlyPlaylist.getTitle().toLowerCase().contains(query.toLowerCase())) {
                            recentlyPlaylists.add(recentlyPlaylist);
                        }
                    }

                    adapter.addItemsObserver(recentlyPlaylists);
                }
                return false;
            }
        });

        onPlayMusic();
        recyclerViewPlaylistFragment.setAdapter(adapter);
        adapter.setOnClickObserver(new OnPlaylistObserver() {
            @Override
            public void onPlayMusic() {

            }

            @Override
            public void onPlayMusic(int adapterPosition) {
                try {
                    DashboardObserverActivity.instance().songCurrentPosition = adapterPosition;
                    DashboardObserverActivity.instance().setPlaylistsMusicPlayer(playlists);

                    RecentlyPlaylist playlist = playlists.get(adapterPosition);
                    Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, true, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            playlist.getTitle());

                    RecentlyPlaylist recentlyPlaylist = new RecentlyPlaylist(playlist.getImages(), playlist.getTitle(), playlist.getNotifications(), RecentlyPlaylistClickObserver.PROFILE);
                    DashboardObserverActivity.instance().onMusicPlayer(recentlyPlaylist);

                } catch (Exception e) {
                    Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            e);
                }

            }
        });
    }

    private void onPlayMusic() {
        List<Audio> loadAudio = FileUtils.loadAudio(context);
        Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, false, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                loadAudio.size());

        playlists.clear();
        for (int i = 0; i < loadAudio.size(); i++) {
            Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, false, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    loadAudio.get(i).getTitle());
            if (loadAudio.get(i).getData().endsWith(".mp3")) {
                playlists.add(new RecentlyPlaylist(R.drawable.ic_bluetooth_audio, loadAudio.get(i).getData(), loadAudio.get(i).getArtist(), RecentlyPlaylistClickObserver.PROFILE));
            }
        }
        adapter.addItemsObserver(playlists);

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

    GenericRecordsAdapter<RecentlyPlaylist, OnPlaylistObserver> adapter = new GenericRecordsAdapter<RecentlyPlaylist, OnPlaylistObserver>() {
        private OnPlaylistObserver recentlyPlaylistObserver;

        class RecentlyPlaylistViewHolder extends BaseHolder<RecentlyPlaylist, OnPlaylistObserver> {
            private AppCompatTextView titleSongPlaylist;

            public RecentlyPlaylistViewHolder(View itemView) {
                super(itemView);

                titleSongPlaylist = itemView.findViewById(R.id.titleSongPlaylist);
            }

            @Override
            public void onBind(int position, RecentlyPlaylist recentlyPlaylist, List<RecentlyPlaylist> records, OnPlaylistObserver onRecentlyPlaylistObserver) {
                File file = new File(recentlyPlaylist.getTitle());
                String strFileName = file.getName();
                titleSongPlaylist.setText(strFileName);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                recentlyPlaylistObserver.onPlayMusic(getAdapterPosition());
            }
        }

        @Override
        public BaseHolder<RecentlyPlaylist, OnPlaylistObserver> setViewHolder(ViewGroup parent, int typeOfView, OnPlaylistObserver onRecentlyPlaylistObserver) {
            int layout_search_playlist_song = R.layout.layout_search_playlist_song;
            return new RecentlyPlaylistViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_search_playlist_song, parent, false));
        }

        @Override
        protected void onBindData(BaseHolder<RecentlyPlaylist, OnPlaylistObserver> holder, int position, RecentlyPlaylist recentlyPlaylist, List<RecentlyPlaylist> records, OnPlaylistObserver recentlyPlaylistObserver) {
            this.recentlyPlaylistObserver = recentlyPlaylistObserver;
            Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, false, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    recentlyPlaylist.getTitle(),
                    recentlyPlaylist.getNotifications(),
                    recentlyPlaylist.getImages(),
                    recentlyPlaylist.getRecentlyPlaylistClickObserver()
            );

        }
    };

    public interface OnPlaylistObserver {

        void onPlayMusic();

        void onPlayMusic(int adapterPosition);
    }

}
