package com.android.bluetoothmusic.fragment.dashboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.navigation.NavOptions;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.adapter.BaseHolder;
import com.android.bluetoothmusic.adapter.GenericRecordsAdapter;
import com.android.bluetoothmusic.legacy.fragment.AttachmentViewFragment;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;
import com.android.bluetoothmusic.models.BundleObserver;
import com.android.bluetoothmusic.models.ObjectObserver;
import com.android.bluetoothmusic.utility.VariableConstants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends AttachmentViewFragment {

    private Context context;
    private TextView createPlaylistButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_playlist, container, false);
        context = getActivity();
        initializeView(inflateView);
        return inflateView;
    }

    @Override
    protected void initializeSession() {

    }

    @Override
    protected void initializeView(View view) {

        createPlaylistButton = (TextView) bindView(view, R.id.createPlaylistButton);

        SwipeRefreshLayout playlistsSwipeRefreshLayout = (SwipeRefreshLayout) bindView(view, R.id.playlistsSwipeRefreshLayout);
        RecyclerView playlistsRecyclerView = (RecyclerView) bindView(view, R.id.playlistsRecyclerView);

        RecyclerView.LayoutManager linearLayoutManager = new GridLayoutManager(context, 2);
        playlistsRecyclerView.setLayoutManager(linearLayoutManager);
        playlistsRecyclerView.setAdapter(adapter);
        adapter.setOnClickObserver(new OnPlaylistObserver() {
            @Override
            public void onPlayMusic(int adapterPosition) {
                String playlistId = adapter.getRecord(adapterPosition).getPlaylistId();
                Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, true, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        adapter.getRecord(adapterPosition).getPlaylistName(),
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

            }

            @Override
            public void onDeletePlaylist(int adapterPosition) {
                String playlistId = adapter.getRecord(adapterPosition).getPlaylistId();
                Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, true, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        playlistId
                );
                try {
                    ObjectObserver objectObserver = playlistDatabaseHelper.deletePlaylist(String.valueOf(VariableConstants.id), playlistId);
                    Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, true, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            initializePrettyPrintJson(objectObserver)
                    );
                    initializeEvent();
                } catch (Exception e) {
                    Logger.logger(LoggerMessage.THROWABLE, true, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            e
                    );
                }

            }

            @Override
            public void onUpdatePlaylist(int adapterPosition) {
                String playlistId = adapter.getRecord(adapterPosition).getPlaylistId();
                Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, true, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        playlistId
                );

                NavOptions.Builder builder = new NavOptions.Builder();
                builder.setEnterAnim(R.anim.animation_translate_enter_slide);
                builder.setExitAnim(R.anim.animation_translate_exit_slide);
                builder.setPopEnterAnim(R.anim.animation_translate_pop_enter_slide);
                builder.setPopExitAnim(R.anim.animation_translate_pop_exit_slide);

                NavOptions options = builder.build();
                Bundle bundle = new Bundle();
                bundle.putString(bundlePlaylistId, playlistId);

                /**
                 * {@link UpdatePlaylistFragment}
                 */
                DashboardFragment.homeNavController.navigate(R.id.updatePlaylistFragment, bundle, options);

            }
        });

        playlistsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                playlistsSwipeRefreshLayout.setRefreshing(false);
            }
        });

        initializeEvent();

        initializeClicker(createPlaylistButton);
    }

    @Override
    protected void initializeEvent() {
        try {
            ObjectObserver playlistsObject = playlistDatabaseHelper.showPlaylists(String.valueOf(VariableConstants.id));

            Logger.log(LoggerMessage.NONE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    initializePrettyPrintJson(playlistsObject));
            Toast.makeText(context, playlistsObject.getMessage(), Toast.LENGTH_SHORT).show();

            List<ObjectObserver> objectObservers;
            if (playlistsObject.isSuccess()) {
                objectObservers = playlistsObject.getSongs();
            } else {
                objectObservers = new ArrayList<>();
            }
            adapter.addItemsObserver(objectObservers);
        } catch (Exception e) {
            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    e);
        }
    }

    @Override
    protected void toolbar() {

    }

    @Override
    public void onClick(View view) {
        if (view == createPlaylistButton) {
            NavOptions.Builder builder = new NavOptions.Builder();
            builder.setEnterAnim(R.anim.animation_translate_enter_slide);
            builder.setExitAnim(R.anim.animation_translate_exit_slide);
            builder.setPopEnterAnim(R.anim.animation_translate_pop_enter_slide);
            builder.setPopExitAnim(R.anim.animation_translate_pop_exit_slide);

            NavOptions options = builder.build();
            Bundle bundle = new Bundle();
            if (navigatorHomeId != R.id.createPlaylistFragment) {

                /**
                 * {@link CreatePlaylistFragment}
                 */
                DashboardFragment.homeNavController.navigate(R.id.createPlaylistFragment, bundle, options);
            }

        }
    }

    GenericRecordsAdapter<ObjectObserver, OnPlaylistObserver> adapter = new GenericRecordsAdapter<ObjectObserver, OnPlaylistObserver>() {
        private OnPlaylistObserver observer;

        @Override
        public BaseHolder<ObjectObserver, OnPlaylistObserver> setViewHolder(ViewGroup parent, int typeOfView, OnPlaylistObserver onRecentlyPlaylistObserver) {
            int layout_playlist_song = R.layout.layout_playlist_song;
            return new PlaylistViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_playlist_song, parent, false));
        }

        @Override
        protected void onBindData(BaseHolder<ObjectObserver, OnPlaylistObserver> holder, int position, ObjectObserver objectObserver, List<ObjectObserver> objectObservers, OnPlaylistObserver observer) {
            this.observer = observer;
            Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, false, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    initializePrettyPrintJson(objectObserver)
            );

        }

        class PlaylistViewHolder extends BaseHolder<ObjectObserver, OnPlaylistObserver> {
            private AppCompatImageView logoImageView;
            private AppCompatTextView titleSongPlaylist;
            private AppCompatImageView deleteImageView;
            private AppCompatImageView editPlaylistImageView;

            public PlaylistViewHolder(View itemView) {
                super(itemView);

                logoImageView = itemView.findViewById(R.id.logoImageView);
                titleSongPlaylist = itemView.findViewById(R.id.titleSongPlaylist);
                deleteImageView = itemView.findViewById(R.id.deleteImageView);
                editPlaylistImageView = itemView.findViewById(R.id.editPlaylistImageView);
            }

            @Override
            public void onBind(int position, ObjectObserver objectObserver, List<ObjectObserver> records, OnPlaylistObserver observer) {
                titleSongPlaylist.setText(objectObserver.getPlaylistName());

                String base64String = objectObserver.getPlaylistImage();
                byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap bitmapResult = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Glide.with(context).load(decodedString).into(logoImageView);

                deleteImageView.setOnClickListener(this);
                editPlaylistImageView.setOnClickListener(this);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (view == itemView) {
                    observer.onPlayMusic(getAdapterPosition());
                }

                if (view == deleteImageView) {
                    observer.onDeletePlaylist(getAdapterPosition());
                }
                if (view == editPlaylistImageView) {
                    observer.onUpdatePlaylist(getAdapterPosition());
                }
            }
        }

    };

    public interface OnPlaylistObserver {

        void onPlayMusic(int adapterPosition);

        void onDeletePlaylist(int adapterPosition);

        void onUpdatePlaylist(int adapterPosition);
    }
}
