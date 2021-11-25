package com.android.bluetoothmusic.fragment.dashboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.arch.core.internal.SafeIterableMap;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavOptions;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.activity.DashboardObserverActivity;
import com.android.bluetoothmusic.adapter.BaseHolder;
import com.android.bluetoothmusic.adapter.GenericRecordsAdapter;
import com.android.bluetoothmusic.adapter.RecentlyViewHolder;
import com.android.bluetoothmusic.database.PlaylistDatabaseHelper;
import com.android.bluetoothmusic.legacy.fragment.AttachmentViewFragment;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;
import com.android.bluetoothmusic.models.Audio;
import com.android.bluetoothmusic.models.ObjectObserver;
import com.android.bluetoothmusic.models.music.BestArtistsPlaylist;
import com.android.bluetoothmusic.models.music.BestArtistsPlaylistClickObserver;
import com.android.bluetoothmusic.models.music.BestArtistsPlaylistObserver;
import com.android.bluetoothmusic.models.music.MusicPlaylistObserver;
import com.android.bluetoothmusic.models.music.RecentlyPlaylist;
import com.android.bluetoothmusic.models.music.RecentlyPlaylistClickObserver;
import com.android.bluetoothmusic.models.music.RecentlyPlaylistObserver;
import com.android.bluetoothmusic.models.music.UserPlaylist;
import com.android.bluetoothmusic.models.music.UserPlaylistClickObserver;
import com.android.bluetoothmusic.models.music.UserPlaylistObserver;
import com.android.bluetoothmusic.notification.NotificationObserver;
import com.android.bluetoothmusic.notification.RemoteMessage;
import com.android.bluetoothmusic.utility.Constants;
import com.android.bluetoothmusic.utility.DrawableUtils;
import com.android.bluetoothmusic.utility.FileUtils;
import com.android.bluetoothmusic.utility.VariableConstants;
import com.android.bluetoothmusic.utility.spinner.SpinnerDialog;
import com.android.bluetoothmusic.widgets.EllipsizeTextView;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends AttachmentViewFragment {

    private Context context;
    private List<MusicPlaylistObserver> musicPlaylistObservers = new ArrayList<>();
    private RecyclerView recyclerViewPlayerFragment;
    private MusicPlaylistAdapter musicPlaylistAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        initializeView(inflateView);
        initializeEvent();
        return inflateView;
    }

    @Override
    protected void initializeSession() {

    }

    @Override
    protected void initializeView(View view) {

        SwipeRefreshLayout swipeRefreshLayoutPlayerFragment = (SwipeRefreshLayout) bindView(view, R.id.swipeRefreshLayoutPlayerFragment);
        recyclerViewPlayerFragment = (RecyclerView) bindView(view, R.id.recyclerViewPlayerFragment);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerViewPlayerFragment.setLayoutManager(linearLayoutManager);
        recyclerViewPlayerFragment.setHasFixedSize(true);
        swipeRefreshLayoutPlayerFragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayoutPlayerFragment.setRefreshing(false);
            }
        });
    }

    @Override
    protected void initializeEvent() {
        musicPlaylistObservers = new ArrayList<>();
        musicPlaylistObservers.add(0, recentlyPlaylistObserver());
        musicPlaylistObservers.add(1, userPlaylistObserver());
        musicPlaylistObservers.add(2, bestArtistsPlaylistObserver());

        musicPlaylistAdapter = new MusicPlaylistAdapter();
        musicPlaylistAdapter.addItemsObserver(musicPlaylistObservers);
        recyclerViewPlayerFragment.setAdapter(musicPlaylistAdapter);

        musicPlaylistAdapter.setOnClickObserver(new OnMusicPlaylistObserver() {
            @Override
            public void onRecentlyPlayer(RecentlyPlaylistClickObserver recentlyPlaylistClickObserver) {
                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        recentlyPlaylistClickObserver.name());
            }

            @Override
            public void onUserPlayer(UserPlaylistClickObserver userPlaylistClickObserver) {
                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        userPlaylistClickObserver.name());
            }

            @Override
            public void onBestArtistsPlayer(BestArtistsPlaylistClickObserver bestArtistsPlaylistClickObserver) {
                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        bestArtistsPlaylistClickObserver.name());
            }
        });

    }

    private MusicPlaylistObserver recentlyPlaylistObserver() {
        RecentlyPlaylistObserver recentlyPlaylistObserver = new RecentlyPlaylistObserver();
        List<RecentlyPlaylist> recentlyPlaylists = new ArrayList<>();

        ObjectObserver objectObserver = playlistDatabaseHelper.showRecentlyPlayedSong(VariableConstants.id);

        List<ObjectObserver> loadAudio = objectObserver.getSongs();
        for (int i = 0; i < loadAudio.size(); i++) {
            Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, false, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    loadAudio.get(i).getPlaylistName());
            recentlyPlaylists.add(new RecentlyPlaylist(R.drawable.ic_bluetooth_audio, loadAudio.get(i).getPlaylistName(), loadAudio.get(i).getPlaylistImage(), RecentlyPlaylistClickObserver.PROFILE));

        }
        recentlyPlaylistObserver.setRecentlyPlaylists(recentlyPlaylists);
        return recentlyPlaylistObserver;
    }

    private MusicPlaylistObserver userPlaylistObserver() {
        UserPlaylistObserver userPlaylistObserver = new UserPlaylistObserver();
        List<UserPlaylist> userPlaylists = new ArrayList<>();

        List<Audio> loadAudio = FileUtils.loadAudio(context);

        Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, false, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                loadAudio.size());
        for (int i = 0; i < loadAudio.size(); i++) {
            Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, false, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    loadAudio.get(i).getTitle());
            if (loadAudio.get(i).getData().endsWith(".mp3")) {
                userPlaylists.add(new UserPlaylist(R.drawable.ic_bluetooth_audio, loadAudio.get(i).getData(), loadAudio.get(i).getArtist(), UserPlaylistClickObserver.PROFILE));
            }
        }

        userPlaylistObserver.setUserPlaylists(userPlaylists);
        return userPlaylistObserver;
    }

    private MusicPlaylistObserver bestArtistsPlaylistObserver() {
        BestArtistsPlaylistObserver bestArtistsPlaylistObserver = new BestArtistsPlaylistObserver();
        List<BestArtistsPlaylist> bestArtistsPlaylists = new ArrayList<>();

        ObjectObserver playlistsObject = playlistDatabaseHelper.showPlaylists(String.valueOf(VariableConstants.id));
        List<ObjectObserver> loadAudio = playlistsObject.getSongs();
        Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, false, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                loadAudio.size());
        for (int i = 0; i < loadAudio.size(); i++) {
            Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, false, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    loadAudio.get(i).getPlaylistName());
            bestArtistsPlaylists.add(new BestArtistsPlaylist(loadAudio.get(i).getPlaylistId(), R.drawable.ic_bluetooth_audio, loadAudio.get(i).getPlaylistName(), loadAudio.get(i).getPlaylistImage(), BestArtistsPlaylistClickObserver.ARTISTS));
        }

        bestArtistsPlaylistObserver.setBestArtistsPlaylists(bestArtistsPlaylists);
        return bestArtistsPlaylistObserver;
    }

    @Override
    protected void toolbar() {

    }

    @Override
    public void onClick(View view) {

    }

    public class MusicPlaylistAdapter extends GenericRecordsAdapter<MusicPlaylistObserver, OnMusicPlaylistObserver> {

        private static final int RECENTLY = 0;
        private static final int USER_PLAYLIST = 1;
        private static final int BEST_ARTISTS = 2;

        @Override
        public BaseHolder<MusicPlaylistObserver, OnMusicPlaylistObserver> setViewHolder(ViewGroup parent, int typeOfView, OnMusicPlaylistObserver onNavigationObserver) {
            if (typeOfView == RECENTLY) {
                int layout_recently_playlist = R.layout.layout_recently_playlist;
                return new RecentlyViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_recently_playlist, parent, false));
            } else if (typeOfView == USER_PLAYLIST) {
                int layout_user_playlist = R.layout.layout_user_playlist;
                return new UserPlaylistViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_user_playlist, parent, false));
            } else {
                int layout_artists_playlist = R.layout.layout_artists_playlist;
                return new ArtistsPlaylistViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_artists_playlist, parent, false));
            }
        }

        @Override
        protected void onBindData(BaseHolder<MusicPlaylistObserver, OnMusicPlaylistObserver> holder, int position, MusicPlaylistObserver dashboardMenuObserver, List<MusicPlaylistObserver> musicPlaylistObservers, OnMusicPlaylistObserver onNavigationObserver) {
            if (holder instanceof RecentlyViewHolder) {
                RecentlyViewHolder headerMenuViewHolder = (RecentlyViewHolder) holder;

            } else if (holder instanceof UserPlaylistViewHolder) {
                UserPlaylistViewHolder userPlaylistViewHolder = (UserPlaylistViewHolder) holder;

            } else if (holder instanceof ArtistsPlaylistViewHolder) {
                ArtistsPlaylistViewHolder artistsPlaylistViewHolder = (ArtistsPlaylistViewHolder) holder;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (musicPlaylistObservers.get(position) instanceof RecentlyPlaylistObserver) {
                return RECENTLY;
            } else if (musicPlaylistObservers.get(position) instanceof UserPlaylistObserver) {
                return USER_PLAYLIST;
            } else {
                return BEST_ARTISTS;
            }
        }

        private class UserPlaylistViewHolder extends BaseHolder<MusicPlaylistObserver, OnMusicPlaylistObserver> {

            private RecyclerView userPlaylistRecyclerView;
            private OnMusicPlaylistObserver observer;
            private int selectedPosition = -1;

            public UserPlaylistViewHolder(View itemView) {
                super(itemView);
                userPlaylistRecyclerView = (RecyclerView) bindView(itemView, R.id.userPlaylistRecyclerView);
                RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
                userPlaylistRecyclerView.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onBind(int position, MusicPlaylistObserver musicPlaylistObserver, List<MusicPlaylistObserver> records, OnMusicPlaylistObserver observer) {
                this.observer = observer;

                GenericRecordsAdapter<UserPlaylist, OnPlaylistObserver<UserPlaylist>> adapter = new GenericRecordsAdapter<UserPlaylist, OnPlaylistObserver<UserPlaylist>>() {

                    @Override
                    public BaseHolder<UserPlaylist, OnPlaylistObserver<UserPlaylist>> setViewHolder(ViewGroup parent, int typeOfView, OnPlaylistObserver<UserPlaylist> onRecentlyPlaylistObserver) {
                        int layout_recently_playlist_song = R.layout.layout_recently_playlist_song;
                        return new RecentlyPlaylistViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_recently_playlist_song, parent, false));
                    }

                    @Override
                    protected void onBindData(BaseHolder<UserPlaylist, OnPlaylistObserver<UserPlaylist>> holder, int position, UserPlaylist userPlaylist, List<UserPlaylist> records, OnPlaylistObserver<UserPlaylist> recentlyPlaylistObserver) {
                        Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, false, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                userPlaylist.getTitle(),
                                userPlaylist.getNotifications(),
                                userPlaylist.getImages(),
                                userPlaylist.getUserPlaylistClickObserver()
                        );

                    }

                    class RecentlyPlaylistViewHolder extends BaseHolder<UserPlaylist, OnPlaylistObserver<UserPlaylist>> {
                        private AppCompatImageView audioImagePlaylist;
                        private EllipsizeTextView titleSongPlaylist;
                        private AppCompatImageView favouriteImageView;
                        private AppCompatImageView detailsImageView;
                        private List<UserPlaylist> recentlyPlaylists;
                        private OnPlaylistObserver<UserPlaylist> recentlyPlaylistObserver;

                        public RecentlyPlaylistViewHolder(View itemView) {
                            super(itemView);

                            audioImagePlaylist = (AppCompatImageView) itemView.findViewById(R.id.audioImagePlaylist);
                            titleSongPlaylist = (EllipsizeTextView) itemView.findViewById(R.id.titleSongPlaylist);
                            detailsImageView = (AppCompatImageView) itemView.findViewById(R.id.detailsImageView);
                            favouriteImageView = (AppCompatImageView) itemView.findViewById(R.id.favouriteImageView);

                        }

                        @Override
                        public void onBind(int position, UserPlaylist userPlaylist, List<UserPlaylist> records, OnPlaylistObserver<UserPlaylist> recentlyPlaylistObserver) {
                            this.recentlyPlaylistObserver = recentlyPlaylistObserver;

                            this.recentlyPlaylists = records;

                            File filePath = new File(userPlaylist.getTitle());

                            String strFileName = filePath.getName();
                            Logger.log(LoggerMessage.SONG_NAME, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                    strFileName,
                                    filePath.length());

                            titleSongPlaylist.setText(strFileName);

                            playlist();

                            itemView.setOnClickListener(this);
                            detailsImageView.setOnClickListener(this);
                            favouriteImageView.setOnClickListener(this);
                        }

                        private void playlist() {
                            try {
                                PlaylistDatabaseHelper playlistDatabaseHelper = new PlaylistDatabaseHelper(context);
                                ObjectObserver playlistsObject = playlistDatabaseHelper.showPlaylists(String.valueOf(VariableConstants.id));
                                Logger.log(LoggerMessage.NONE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                        initializePrettyPrintJson(playlistsObject));
                                if (playlistsObject.isSuccess()) {
                                    favouriteImageView.setVisibility(View.VISIBLE);
                                } else {
                                    favouriteImageView.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                        e);
                            }
                        }

                        @Override
                        public void onClick(View view) {
                            if (view == detailsImageView) {
                                recentlyPlaylistObserver.onDetailsPlaylist(getAdapterPosition(), recentlyPlaylists);
                            }

                            if (view == favouriteImageView) {
                                recentlyPlaylistObserver.onAddPlaylist(getAdapterPosition(), recentlyPlaylists);
                            }

                            if (view == itemView) {
                                recentlyPlaylistObserver.onPlayMusic(getAdapterPosition());
                            }
                        }
                    }

                };
                adapter.addItemsObserver(musicPlaylistObserver.getUserPlaylists());
                userPlaylistRecyclerView.setAdapter(adapter);
                adapter.setOnClickObserver(new OnPlaylistObserver<UserPlaylist>() {
                    @Override
                    public void onPlayMusic(int adapterPosition) {
                        try {
                            DashboardObserverActivity.instance().songCurrentPosition = adapterPosition;

                            DashboardObserverActivity.instance().playlistsMusicPlayer.clear();

                            for (UserPlaylist bestArtistsPlaylist : musicPlaylistObserver.getUserPlaylists()) {
                                DashboardObserverActivity.instance().playlistsMusicPlayer.add(new RecentlyPlaylist(bestArtistsPlaylist.getImages(), bestArtistsPlaylist.getTitle(), bestArtistsPlaylist.getNotifications(), RecentlyPlaylistClickObserver.PROFILE));

                            }

                            UserPlaylist playlist = musicPlaylistObserver.getUserPlaylists().get(adapterPosition);
                            Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, true, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                    initializePrettyPrintJson(playlist));

                            RecentlyPlaylist recentlyPlaylist = new RecentlyPlaylist(playlist.getImages(), playlist.getTitle(), playlist.getNotifications(), RecentlyPlaylistClickObserver.PROFILE);
                            ObjectObserver objectObserver = playlistDatabaseHelper.addRecentlyPlayedSong(VariableConstants.id, playlist.getTitle(), playlist.getTitle(), "0", Constants.createDate, Constants.createDate);
                            musicPlaylistObservers.set(0, recentlyPlaylistObserver());
                            musicPlaylistAdapter.notifyDataSetChanged();

                            DashboardObserverActivity.instance().onMusicPlayer(recentlyPlaylist);

                        } catch (Exception e) {
                            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                    e);
                        }

                    }

                    @Override
                    public void onAddPlaylist(int adapterPosition, List<UserPlaylist> userPlaylists) {
                        try {
                            PlaylistDatabaseHelper playlistDatabaseHelper = new PlaylistDatabaseHelper(context);
                            ObjectObserver playlistsObject = playlistDatabaseHelper.showPlaylists(String.valueOf(VariableConstants.id));

                            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                    initializePrettyPrintJson(userPlaylists.get(adapterPosition)),
                                    new File(userPlaylists.get(adapterPosition).getTitle()).getName());

                            if (playlistsObject.isSuccess()) {
                                favourite(playlistsObject.getSongs(), userPlaylists.get(adapterPosition));
                            }

                        } catch (Exception e) {
                            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                    e);
                        }
                    }

                    @Override
                    public void onDetailsPlaylist(int adapterPosition, List<UserPlaylist> userPlaylists) {
                        try {

                            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                    initializePrettyPrintJson(userPlaylists.get(adapterPosition)),
                                    new File(userPlaylists.get(adapterPosition).getTitle()).getName());

                            File filePath = new File(userPlaylists.get(adapterPosition).getTitle());
                            Uri uri = Uri.parse(filePath.getPath());

                            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                            metaRetriever.setDataSource(context, uri);
                            String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            metaRetriever.release();

                            View popupView = LayoutInflater.from(context).inflate(R.layout.layout_details, null);

                            PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                            ((TextView) popupView.findViewById(R.id.titleSongPlaylist)).setText("Name : " + filePath.getName());
                            ((TextView) popupView.findViewById(R.id.sizeSongPlaylist)).setText("Size : " + Constants.getFileSizeLabel(filePath));
                            ((TextView) popupView.findViewById(R.id.durationSongPlaylist)).setText("Duration : " + Constants.duration(Long.parseLong(duration)));

                        } catch (Exception e) {
                            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                    e);
                        }
                    }
                });
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                observer.onUserPlayer(UserPlaylistClickObserver.PROFILE);
            }

            GenericRecordsAdapter<ObjectObserver, OnSongPlaylistObserver> adapterPlaylist = new GenericRecordsAdapter<ObjectObserver, OnSongPlaylistObserver>() {
                private OnSongPlaylistObserver observer;

                @Override
                public BaseHolder<ObjectObserver, OnSongPlaylistObserver> setViewHolder(ViewGroup parent, int typeOfView, OnSongPlaylistObserver onRecentlyPlaylistObserver) {
                    int layout_recently_song_playlist = R.layout.layout_recently_song_playlist;
                    return new RecentlyPlaylistViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_recently_song_playlist, parent, false));
                }

                @Override
                protected void onBindData(BaseHolder<ObjectObserver, OnSongPlaylistObserver> holder, int position, ObjectObserver objectObserver, List<ObjectObserver> objectObservers, OnSongPlaylistObserver observer) {
                    this.observer = observer;
                    Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, false, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            initializePrettyPrintJson(objectObserver)
                    );

                }

                class RecentlyPlaylistViewHolder extends BaseHolder<ObjectObserver, OnSongPlaylistObserver> {

                    private AppCompatTextView titleSongPlaylist;
                    private OnSongPlaylistObserver observer;
                    private int selectedPosition = -1;

                    public RecentlyPlaylistViewHolder(View itemView) {
                        super(itemView);

                        titleSongPlaylist = itemView.findViewById(R.id.titleSongPlaylist);
                    }

                    @Override
                    public void onBind(int position, ObjectObserver objectObserver, List<ObjectObserver> records, OnSongPlaylistObserver observer) {
                        this.observer = observer;
                        titleSongPlaylist.setText(objectObserver.getPlaylistName());

                        if (selectedPosition == position) {
                            titleSongPlaylist.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSeaGreen));
                        } else {
                            titleSongPlaylist.setBackgroundColor(ContextCompat.getColor(context, R.color.colorIndianRed));
                        }

                        itemView.setOnClickListener(this);
                    }

                    @Override
                    public void onClick(View view) {
                        try {
                            selectedPosition = getAdapterPosition();
                            observer.addSongPlaylist(getAdapterPosition());
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                    e);
                        }
                    }
                }

            };

            private void favourite(List<ObjectObserver> objectObservers, UserPlaylist recentlyPlaylist) {
                List<ObjectObserver> observers = new ArrayList<>(objectObservers);
                selectedPosition = -1;

                SpinnerDialog<ObjectObserver> dialog = new SpinnerDialog<>(context);
                dialog.show();
                dialog.isClosable(false);
                dialog.setAllowButton("Ok");
                dialog.setDismissButton("Cancel");
                dialog.setTitleText(new File(recentlyPlaylist.getTitle()).getName());
                dialog.titleSpinnerDialog.setBackground(DrawableUtils.listDrawable(context, 0f));
                dialog.allowButton.setBackground(DrawableUtils.listDrawable(context));
                dialog.dismissButton.setBackground(DrawableUtils.listDrawable(context));
                dialog.allowButton.setTextColor(DrawableUtils.white(context));
                dialog.dismissButton.setTextColor(DrawableUtils.white(context));

                adapterPlaylist.addItemsObserver(observers);
                dialog.spinnerRecyclerView.setAdapter(adapterPlaylist);
                adapterPlaylist.setOnClickObserver(new OnSongPlaylistObserver() {

                    @Override
                    public void addSongPlaylist(int adapterPosition) {
                        selectedPosition = adapterPosition;
                    }
                });
                dialog.setSpinnerDialogObserver(new SpinnerDialog.SpinnerDialogObserver<ObjectObserver>() {
                    @Override
                    public void filter(String text) {

                    }

                    @Override
                    public void selectedObserver(ObjectObserver objectObserver) {

                    }

                    @Override
                    public void selectedAdapterPosition(ObjectObserver objectObserver, List<ObjectObserver> objectObservers, int adapterPosition) {

                    }

                    @Override
                    public void success() {
                        try {
                            if (selectedPosition != -1) {
                                ObjectObserver playlist = adapterPlaylist.getRecord(selectedPosition);
                                playlist.setPlaylistImage("");
                                Logger.log(LoggerMessage.NONE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                        initializePrettyPrintJson(playlist));

                                PlaylistDatabaseHelper playlistDatabaseHelper = new PlaylistDatabaseHelper(context);
                                ObjectObserver objectObserver = playlistDatabaseHelper.addSongPlaylist(VariableConstants.id, playlist.getPlaylistId(), recentlyPlaylist.getTitle(), playlist.getPlaylistName(), "0", Constants.createDate, Constants.createDate);

                                Logger.log(LoggerMessage.SONG_NAME, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                        initializePrettyPrintJson(playlist),
                                        initializePrettyPrintJson(objectObserver),
                                        recentlyPlaylist.getTitle());

                                Toast.makeText(context, objectObserver.getMessage(), Toast.LENGTH_SHORT).show();

                                if (objectObserver.isSuccess()) {
                                    RemoteMessage remoteMessage = new RemoteMessage();
                                    remoteMessage.setTitle("Hii, " + playlist.getPlaylistName());
                                    remoteMessage.setBody(objectObserver.getMessage());

                                    NotificationObserver.showNotification(context, remoteMessage);

                                }
                                dialog.close();
                            }
                        } catch (Exception exception) {
                            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                    exception);
                        } finally {

                        }
                    }

                    @Override
                    public void failure() {

                    }
                });

            }


        }


        private class ArtistsPlaylistViewHolder extends BaseHolder<MusicPlaylistObserver, OnMusicPlaylistObserver> {

            private TextView titleSong;
            private RecyclerView bestArtistPlaylistRecyclerView;
            private OnMusicPlaylistObserver observer;

            public ArtistsPlaylistViewHolder(View itemView) {
                super(itemView);
                titleSong = (TextView) bindView(itemView, R.id.titleSong);
                bestArtistPlaylistRecyclerView = (RecyclerView) bindView(itemView, R.id.bestArtistPlaylistRecyclerView);
                RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
                bestArtistPlaylistRecyclerView.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onBind(int position, MusicPlaylistObserver musicPlaylistObserver, List<MusicPlaylistObserver> records, OnMusicPlaylistObserver observer) {
                this.observer = observer;

                titleSong.setVisibility(musicPlaylistObserver.getBestArtistsPlaylists().size() != 0 ? View.VISIBLE : View.GONE);

                GenericRecordsAdapter<BestArtistsPlaylist, OnPlaylistObserver<BestArtistsPlaylist>> adapter = new GenericRecordsAdapter<BestArtistsPlaylist, OnPlaylistObserver<BestArtistsPlaylist>>() {
                    private OnPlaylistObserver<BestArtistsPlaylist> observer;

                    @Override
                    public BaseHolder<BestArtistsPlaylist, OnPlaylistObserver<BestArtistsPlaylist>> setViewHolder(ViewGroup parent, int typeOfView, OnPlaylistObserver<BestArtistsPlaylist> onPlaylistObserver) {
                        int layout_recently_playlist_song = R.layout.layout_recently_playlist_song;
                        return new RecentlyPlaylistViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_recently_playlist_song, parent, false));
                    }

                    @Override
                    protected void onBindData(BaseHolder<BestArtistsPlaylist, OnPlaylistObserver<BestArtistsPlaylist>> holder, int position, BestArtistsPlaylist bestArtistsPlaylist, List<BestArtistsPlaylist> records, OnPlaylistObserver<BestArtistsPlaylist> observer) {
                        this.observer = observer;
                        Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, false, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                bestArtistsPlaylist.getTitle(),
                                bestArtistsPlaylist.getNotifications(),
                                bestArtistsPlaylist.getImages(),
                                bestArtistsPlaylist.getBestArtistsPlaylistClickObserver()
                        );

                    }

                    class RecentlyPlaylistViewHolder extends BaseHolder<BestArtistsPlaylist, OnPlaylistObserver<BestArtistsPlaylist>> {
                        private AppCompatImageView audioImagePlaylist;
                        private AppCompatImageView detailsImageView;
                        private AppCompatImageView favouriteImageView;
                        private EllipsizeTextView titleSongPlaylist;

                        public RecentlyPlaylistViewHolder(View itemView) {
                            super(itemView);

                            audioImagePlaylist = (AppCompatImageView) itemView.findViewById(R.id.audioImagePlaylist);
                            detailsImageView = (AppCompatImageView) itemView.findViewById(R.id.detailsImageView);
                            favouriteImageView = (AppCompatImageView) itemView.findViewById(R.id.favouriteImageView);
                            titleSongPlaylist = (EllipsizeTextView) itemView.findViewById(R.id.titleSongPlaylist);
                        }

                        @Override
                        public void onBind(int position, BestArtistsPlaylist recentlyPlaylist, List<BestArtistsPlaylist> records, OnPlaylistObserver<BestArtistsPlaylist> onPlaylistObserver) {
                            detailsImageView.setVisibility(View.GONE);
                            favouriteImageView.setVisibility(View.GONE);
                            titleSongPlaylist.setText(recentlyPlaylist.getTitle());

                            String base64String = recentlyPlaylist.getNotifications();
                            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                            Bitmap bitmapResult = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            Glide.with(context).load(decodedString).placeholder(R.mipmap.ic_launcher).into(audioImagePlaylist);

                            itemView.setOnClickListener(this);
                        }

                        @Override
                        public void onClick(View view) {
                            observer.onPlayMusic(getAdapterPosition());
                        }
                    }

                };
                adapter.addItemsObserver(musicPlaylistObserver.getBestArtistsPlaylists());
                bestArtistPlaylistRecyclerView.setAdapter(adapter);
                adapter.setOnClickObserver(new OnPlaylistObserver<BestArtistsPlaylist>() {

                    @Override
                    public void onPlayMusic(int adapterPosition) {
                        try {
                            String playlistId = adapter.getRecord(adapterPosition).getId();
                            Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, true, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                    adapter.getRecord(adapterPosition).getTitle(),
                                    playlistId
                            );

                            NavOptions.Builder builder = new NavOptions.Builder();
                            builder.setEnterAnim(R.anim.animation_translate_enter_slide);
                            builder.setExitAnim(R.anim.animation_translate_exit_slide);
                            builder.setPopEnterAnim(R.anim.animation_translate_enter_slide);
                            builder.setPopExitAnim(R.anim.animation_translate_exit_slide);

                            NavOptions options = builder.build();
                            Bundle bundle = new Bundle();
                            bundle.putString(bundlePlaylistId, playlistId);
                            /**
                             * {@link SongPlaylistFragment}
                             */
                            DashboardFragment.homeNavController.navigate(R.id.songPlaylistFragment, bundle, options);

                        } catch (Exception e) {
                            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                    e);
                        }

                    }

                    @Override
                    public void onAddPlaylist(int adapterPosition, List<BestArtistsPlaylist> bestArtistsPlaylists) {

                    }

                    @Override
                    public void onDetailsPlaylist(int adapterPosition, List<BestArtistsPlaylist> bestArtistsPlaylists) {

                    }
                });

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                observer.onBestArtistsPlayer(BestArtistsPlaylistClickObserver.ARTISTS);
            }

        }

    }

    public interface OnSongPlaylistObserver {
        void addSongPlaylist(int adapterPosition) throws Exception;
    }

    public interface OnPlaylistObserver<Record> {

        void onPlayMusic(int adapterPosition);

        void onAddPlaylist(int adapterPosition, List<Record> records);

        void onDetailsPlaylist(int adapterPosition, List<Record> records);
    }

    public interface OnMusicPlaylistObserver {
        void onRecentlyPlayer(RecentlyPlaylistClickObserver recentlyPlaylistClickObserver);

        void onUserPlayer(UserPlaylistClickObserver userPlaylistClickObserver);

        void onBestArtistsPlayer(BestArtistsPlaylistClickObserver bestArtistsPlaylistClickObserver);
    }
}
