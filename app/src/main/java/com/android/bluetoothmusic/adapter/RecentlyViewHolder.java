package com.android.bluetoothmusic.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.activity.DashboardObserverActivity;
import com.android.bluetoothmusic.database.PlaylistDatabaseHelper;
import com.android.bluetoothmusic.fragment.dashboard.HomeFragment;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;
import com.android.bluetoothmusic.models.ObjectObserver;
import com.android.bluetoothmusic.models.music.MusicPlaylistObserver;
import com.android.bluetoothmusic.models.music.RecentlyPlaylist;
import com.android.bluetoothmusic.models.music.RecentlyPlaylistClickObserver;
import com.android.bluetoothmusic.notification.NotificationObserver;
import com.android.bluetoothmusic.notification.RemoteMessage;
import com.android.bluetoothmusic.utility.Constants;
import com.android.bluetoothmusic.utility.DrawableUtils;
import com.android.bluetoothmusic.utility.VariableConstants;
import com.android.bluetoothmusic.utility.spinner.SpinnerDialog;
import com.android.bluetoothmusic.widgets.EllipsizeTextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecentlyViewHolder extends BaseHolder<MusicPlaylistObserver, HomeFragment.OnMusicPlaylistObserver> {

    private Context context;
    private TextView titleSong;
    private RecyclerView recentlyPlaylistRecyclerView;
    private HomeFragment.OnMusicPlaylistObserver observer;
    private int selectedPosition = -1;

    public RecentlyViewHolder(View itemView) {
        super(itemView);

        context = itemView.getContext();

        titleSong = (TextView) bindView(itemView, R.id.titleSong);
        recentlyPlaylistRecyclerView = (RecyclerView) bindView(itemView, R.id.recentlyPlaylistRecyclerView);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        recentlyPlaylistRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onBind(int position, MusicPlaylistObserver musicPlaylistObserver, List<MusicPlaylistObserver> records, HomeFragment.OnMusicPlaylistObserver observer) {
        this.observer = observer;

        titleSong.setVisibility(musicPlaylistObserver.getRecentlyPlaylists().size() != 0 ? View.VISIBLE : View.GONE);

        GenericRecordsAdapter<RecentlyPlaylist, HomeFragment.OnPlaylistObserver<RecentlyPlaylist>> adapter = new GenericRecordsAdapter<RecentlyPlaylist, HomeFragment.OnPlaylistObserver<RecentlyPlaylist>>() {
            private HomeFragment.OnPlaylistObserver<RecentlyPlaylist> recentlyPlaylistObserver;

            @Override
            public BaseHolder<RecentlyPlaylist, HomeFragment.OnPlaylistObserver<RecentlyPlaylist>> setViewHolder(ViewGroup parent, int typeOfView, HomeFragment.OnPlaylistObserver<RecentlyPlaylist> onRecentlyPlaylistObserver) {
                int layout_recently_playlist_song = R.layout.layout_recently_playlist_song;
                return new RecentlyPlaylistViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_recently_playlist_song, parent, false));
            }

            @Override
            protected void onBindData(BaseHolder<RecentlyPlaylist, HomeFragment.OnPlaylistObserver<RecentlyPlaylist>> holder, int position, RecentlyPlaylist recentlyPlaylist, List<RecentlyPlaylist> records, HomeFragment.OnPlaylistObserver<RecentlyPlaylist> recentlyPlaylistObserver) {
                this.recentlyPlaylistObserver = recentlyPlaylistObserver;
                Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, false, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        recentlyPlaylist.getTitle(),
                        recentlyPlaylist.getNotifications(),
                        recentlyPlaylist.getImages(),
                        recentlyPlaylist.getRecentlyPlaylistClickObserver()
                );

            }

            class RecentlyPlaylistViewHolder extends BaseHolder<RecentlyPlaylist, HomeFragment.OnPlaylistObserver<RecentlyPlaylist>> {
                private AppCompatImageView audioImagePlaylist;
                private AppCompatImageView favouriteImageView;
                private AppCompatImageView detailsImageView;
                private EllipsizeTextView titleSongPlaylist;
                private List<RecentlyPlaylist> recentlyPlaylists;

                public RecentlyPlaylistViewHolder(View itemView) {
                    super(itemView);

                    audioImagePlaylist = (AppCompatImageView) itemView.findViewById(R.id.audioImagePlaylist);
                    titleSongPlaylist = (EllipsizeTextView) itemView.findViewById(R.id.titleSongPlaylist);
                    detailsImageView = (AppCompatImageView) itemView.findViewById(R.id.detailsImageView);
                    favouriteImageView = (AppCompatImageView) itemView.findViewById(R.id.favouriteImageView);
                }

                @Override
                public void onBind(int position, RecentlyPlaylist recentlyPlaylist, List<RecentlyPlaylist> recentlyPlaylists, HomeFragment.OnPlaylistObserver<RecentlyPlaylist> onRecentlyPlaylistObserver) {
                    this.recentlyPlaylists = recentlyPlaylists;

                    File filePath = new File(recentlyPlaylist.getTitle());

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
        adapter.addItemsObserver(musicPlaylistObserver.getRecentlyPlaylists());
        recentlyPlaylistRecyclerView.setAdapter(adapter);
        adapter.setOnClickObserver(new HomeFragment.OnPlaylistObserver<RecentlyPlaylist>() {
            @Override
            public void onPlayMusic(int adapterPosition) {
                try {
                    DashboardObserverActivity.instance().songCurrentPosition = adapterPosition;

                    DashboardObserverActivity.instance().setPlaylistsMusicPlayer(musicPlaylistObserver.getRecentlyPlaylists());

                    RecentlyPlaylist playlist = musicPlaylistObserver.getRecentlyPlaylists().get(adapterPosition);
                    Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, true, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            playlist);

                    RecentlyPlaylist recentlyPlaylist = new RecentlyPlaylist(playlist.getImages(), playlist.getTitle(), playlist.getNotifications(), RecentlyPlaylistClickObserver.PROFILE);
                    DashboardObserverActivity.instance().onMusicPlayer(recentlyPlaylist);

                } catch (IOException e) {
                    Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            e);
                }

            }

            @Override
            public void onAddPlaylist(int adapterPosition, List<RecentlyPlaylist> recentlyPlaylists) {
                try {
                    PlaylistDatabaseHelper playlistDatabaseHelper = new PlaylistDatabaseHelper(context);
                    ObjectObserver playlistsObject = playlistDatabaseHelper.showPlaylists(String.valueOf(VariableConstants.id));

                    Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            initializePrettyPrintJson(recentlyPlaylists.get(adapterPosition)),
                            new File(recentlyPlaylists.get(adapterPosition).getTitle()).getName());

                    if (playlistsObject.isSuccess()) {
                        favourite(playlistsObject.getSongs(), recentlyPlaylists.get(adapterPosition));
                    }

                } catch (Exception e) {
                    Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            e);
                }
            }

            @Override
            public void onDetailsPlaylist(int adapterPosition, List<RecentlyPlaylist> recentlyPlaylists) {
                try {

                    Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            initializePrettyPrintJson(recentlyPlaylists.get(adapterPosition)),
                            new File(recentlyPlaylists.get(adapterPosition).getTitle()).getName());

                    File filePath = new File(recentlyPlaylists.get(adapterPosition).getTitle());
                    Uri uri = Uri.parse(filePath.getPath());

                    MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                    metaRetriever.setDataSource(context, uri);
                    String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    metaRetriever.release();

                    View popupView = LayoutInflater.from(context).inflate(R.layout.layout_details, null);

                    PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                    // Displays the details of the song by name , size and duration.
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

    private void favourite(List<ObjectObserver> objectObservers, RecentlyPlaylist recentlyPlaylist) {
        List<ObjectObserver> observers = new ArrayList<>(objectObservers);

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

    @Override
    public void onClick(View view) {
        observer.onRecentlyPlayer(RecentlyPlaylistClickObserver.PROFILE);
    }

    GenericRecordsAdapter<ObjectObserver, OnSongPlaylistObserver> adapterPlaylist = new GenericRecordsAdapter<ObjectObserver, OnSongPlaylistObserver>() {
        private OnSongPlaylistObserver observer;
        private int selectedPosition = -1;

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

    public interface OnSongPlaylistObserver {

        void addSongPlaylist(int adapterPosition) throws Exception;
    }
}
