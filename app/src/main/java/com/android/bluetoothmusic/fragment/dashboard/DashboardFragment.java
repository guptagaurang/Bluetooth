package com.android.bluetoothmusic.fragment.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.activity.DashboardObserverActivity;
import com.android.bluetoothmusic.adapter.BaseHolder;
import com.android.bluetoothmusic.adapter.GenericRecordsAdapter;
import com.android.bluetoothmusic.fragment.bluetooth.BluetoothFragment;
import com.android.bluetoothmusic.legacy.fragment.AttachmentViewFragment;
import com.android.bluetoothmusic.legacy.observer.OnMusicObserver;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;
import com.android.bluetoothmusic.models.dashboardmenu.ContentMenuClickObserver;
import com.android.bluetoothmusic.models.dashboardmenu.ContentMenuDashboardObserver;
import com.android.bluetoothmusic.models.dashboardmenu.ContentMenuObserver;
import com.android.bluetoothmusic.models.dashboardmenu.DashboardMenuObserver;
import com.android.bluetoothmusic.models.dashboardmenu.FooterMenuClickObserver;
import com.android.bluetoothmusic.models.dashboardmenu.FooterMenuDashboardObserver;
import com.android.bluetoothmusic.models.dashboardmenu.FooterMenuObserver;
import com.android.bluetoothmusic.models.dashboardmenu.HeaderMenuClickObserver;
import com.android.bluetoothmusic.models.dashboardmenu.HeaderMenuDashboardObserver;
import com.android.bluetoothmusic.models.dashboardmenu.HeaderMenuObserver;
import com.android.bluetoothmusic.models.music.RecentlyPlaylist;
import com.android.bluetoothmusic.models.music.RecentlyPlaylistClickObserver;
import com.android.bluetoothmusic.notification.NotificationObserver;
import com.android.bluetoothmusic.notification.RemoteMessage;
import com.android.bluetoothmusic.utility.Constants;
import com.android.bluetoothmusic.utility.VariableConstants;
import com.android.bluetoothmusic.widgets.EllipsizeTextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends AttachmentViewFragment implements NavController.OnDestinationChangedListener {

    private FragmentActivity context;
    private AppCompatImageView toolbarSlideMenuImage;
    private AppCompatImageView toolbarBluetoothDevice;
    private EllipsizeTextView toolbarTitleView;
    private DrawerLayout drawerLayout;
    private RecyclerView navigationViewRecyclerView;
    private List<DashboardMenuObserver> dashboardMenuObservers = new ArrayList<>();
    public static NavController homeNavController;
    private BottomNavigationView bottomNavigationView;
    private int bottomNavigationViewId;
    private LinearLayout layoutMediaPlayer;
    private SeekBar sliderMediaPlayer;
    private ConstraintLayout layoutContent;
    public static OnDashboardObserver onDashboardObserver;

    private OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
                callback.setEnabled(false);
            }
        }
    };
    private EllipsizeTextView durationSongPlaylistMusicPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        context = getActivity();
        initializeView(inflateView);
        initializeEvent();

        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.home_navigation_host_fragment);
        dashboardNavController = navHostFragment.getNavController();
        dashboardNavController.addOnDestinationChangedListener(this);

        return inflateView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        callback.setEnabled(false);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

    }

    @Override
    protected void initializeSession() {

    }

    @Override
    protected void initializeView(View view) {
        toolbarSlideMenuImage = (AppCompatImageView) bindView(view, R.id.toolbarSlideMenuImage);
        toolbarBluetoothDevice = (AppCompatImageView) bindView(view, R.id.toolbarBluetoothDevice);
        toolbarTitleView = (EllipsizeTextView) bindView(view, R.id.toolbarTitleView);
        drawerLayout = (DrawerLayout) bindView(view, R.id.drawerLayout);
        layoutContent = (ConstraintLayout) bindView(view, R.id.layoutContent);
        layoutMediaPlayer = (LinearLayout) bindView(view, R.id.layoutMediaPlayer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(context, drawerLayout, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;

                if (callback != null && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    callback.setEnabled(true);
                }

                //layoutContent.setTranslationX(slideX);

                /*layoutContent.setTranslationX(slideX);
                layoutContent.setScaleX(1 - slideOffset);
                layoutContent.setScaleY(1 - slideOffset);*/

                layoutContent.setTranslationX(slideX);
                float scaleFactor = 6f;
                layoutContent.setScaleX(1 - (slideOffset / scaleFactor));
                layoutContent.setScaleY(1 - (slideOffset / scaleFactor));
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        bottomNavigationView = (BottomNavigationView) bindView(view, R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                NavOptions.Builder builder = new NavOptions.Builder();
                builder.setEnterAnim(R.anim.animation_translate_enter_slide);
                builder.setExitAnim(R.anim.animation_translate_exit_slide);
                builder.setPopEnterAnim(R.anim.animation_translate_enter_slide);
                builder.setPopExitAnim(R.anim.animation_translate_exit_slide);
                builder.setPopUpTo(R.id.homeFragment, false);

                NavOptions options = builder.build();
                Bundle bundle = new Bundle();

                bottomNavigationViewId = menuItem.getItemId();

                if (bottomNavigationViewId == R.id.navigation_home) {
                    if (navigatorHomeId != R.id.homeFragment) {
                        /**
                         * {@link HomeFragment}
                         */
                        dashboardNavController.navigate(R.id.homeFragment, bundle, options);
                    }

                } else if (bottomNavigationViewId == R.id.navigation_search) {
                    if (navigatorHomeId != R.id.searchPlaylistFragment) {
                        /**
                         * {@link SearchPlaylistFragment}
                         */
                        dashboardNavController.navigate(R.id.searchPlaylistFragment, bundle, options);

                    }
                } else if (bottomNavigationViewId == R.id.navigation_playlist) {
                    if (navigatorHomeId != R.id.playlistFragment) {
                        /**
                         * {@link PlaylistFragment}
                         */
                        dashboardNavController.navigate(R.id.playlistFragment, bundle, options);

                    }
                } else if (bottomNavigationViewId == R.id.navigation_premium) {
                    if (navigatorHomeId != R.id.premiumPlaylistFragment) {
                        /**
                         * {@link PremiumPlaylistFragment}
                         */
                        dashboardNavController.navigate(R.id.premiumPlaylistFragment, bundle, options);
                    }
                }

                return true;
            }
        });


        navigationViewRecyclerView = (RecyclerView) bindView(view, R.id.navigationViewRecyclerView);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(context);
        navigationViewRecyclerView.setLayoutManager(linearLayoutManager);


        dashboardMenuObservers = new ArrayList<>();
        dashboardMenuObservers.add(headerMenuDashboard(VariableConstants.username, VariableConstants.email, ""));
        dashboardMenuObservers.add(contentMenuDashboardObserver("Profile", VariableConstants.email, R.drawable.ic_profile, ContentMenuClickObserver.PROFILE));
        //dashboardMenuObservers.add(contentMenuDashboardObserver("Bluetooth", VariableConstants.email, R.drawable.ic_bluetooth_audio, ContentMenuClickObserver.BLUETOOTH));
        dashboardMenuObservers.add(contentMenuDashboardObserver("Change Password", VariableConstants.email, R.drawable.ic_change_password, ContentMenuClickObserver.CHANGE_PASSWORD));
        dashboardMenuObservers.add(footerMenuDashboardObserver("Logout", VariableConstants.email, R.drawable.ic_logout, FooterMenuClickObserver.LOGOUT));

        NavigationViewMenuAdapter navigationViewMenuAdapter = new NavigationViewMenuAdapter();
        navigationViewMenuAdapter.addItemsObserver(dashboardMenuObservers);
        navigationViewRecyclerView.setAdapter(navigationViewMenuAdapter);
        navigationViewMenuAdapter.setOnClickObserver(new OnNavigationObserver() {
            @Override
            public void selectHeaderMenu(HeaderMenuClickObserver headerMenuClickObserver) {
                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        headerMenuClickObserver.name());
            }

            @Override
            public void selectContentMenu(ContentMenuClickObserver contentMenuClickObserver) {
                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        contentMenuClickObserver.name());

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                NavOptions.Builder builder = new NavOptions.Builder();
                builder.setEnterAnim(R.anim.animation_translate_enter_slide);
                builder.setExitAnim(R.anim.animation_translate_exit_slide);
                builder.setPopEnterAnim(R.anim.animation_translate_pop_enter_slide);
                builder.setPopExitAnim(R.anim.animation_translate_pop_exit_slide);
                builder.setPopUpTo(R.id.homeFragment, false);

                NavOptions options = builder.build();
                Bundle bundle = new Bundle();

                if (contentMenuClickObserver == ContentMenuClickObserver.PROFILE) {
                    if (navigatorHomeId != R.id.profileFragment) {
                        /**
                         * {@link com.android.bluetoothmusic.fragment.profile.ProfileFragment}
                         */
                        dashboardNavController.navigate(R.id.profileFragment, bundle, options);
                    }

                } else if (contentMenuClickObserver == ContentMenuClickObserver.BLUETOOTH) {
                    if (navigatorHomeId != R.id.bluetoothFragment) {

                        /**
                         * {@link com.android.bluetoothmusic.fragment.bluetooth.BluetoothFragment}
                         */
                        dashboardNavController.navigate(R.id.bluetoothFragment, bundle, options);
                    }
                } else if (contentMenuClickObserver == ContentMenuClickObserver.CHANGE_PASSWORD) {
                    if (navigatorHomeId != R.id.changePasswordFragment) {

                        /**
                         * {@link com.android.bluetoothmusic.fragment.profile.ChangePasswordFragment}
                         */
                        dashboardNavController.navigate(R.id.changePasswordFragment, bundle, options);
                    }
                }

            }

            @Override
            public void selectFooterMenu(FooterMenuClickObserver footerMenuClickObserver) {
                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        footerMenuClickObserver.name());
                try {

                    if (DashboardObserverActivity.instance().getMediaPlayer() != null) {
                        DashboardObserverActivity.instance().getMediaPlayer().stop();
                    }

                    RemoteMessage remoteMessage = new RemoteMessage();
                    remoteMessage.setTitle("Hii, " + VariableConstants.username);
                    remoteMessage.setBody("Logout Successful");

                    NotificationObserver.showNotification(context, remoteMessage);
                } catch (JSONException exception) {
                    Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            exception);
                } finally {
                    logout();
                }
            }

        });

        toolbarBluetoothDevice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onDashboardObserver.onBluetooth();
                return false;
            }
        });

        initializeClicker(toolbarSlideMenuImage);
        initializeClicker(toolbarBluetoothDevice);
    }

    private DashboardMenuObserver headerMenuDashboard(String title, String notifications, String images) {
        HeaderMenuDashboardObserver headerMenuDashboard = new HeaderMenuDashboardObserver();
        List<HeaderMenuObserver> headerMenuObservers = new ArrayList<>();
        HeaderMenuObserver headerMenuObserver = new HeaderMenuObserver();
        headerMenuObserver.setTitle(title);
        headerMenuObserver.setNotifications(notifications);
        headerMenuObserver.setImages(images);
        headerMenuObservers.add(headerMenuObserver);
        headerMenuDashboard.setHeaderMenuObservers(headerMenuObservers);
        return headerMenuDashboard;
    }

    private DashboardMenuObserver contentMenuDashboardObserver(String title, String notifications, int images, ContentMenuClickObserver contentMenuClickObserver) {
        ContentMenuDashboardObserver contentMenuDashboardObserver = new ContentMenuDashboardObserver();
        List<ContentMenuObserver> contentMenuObservers = new ArrayList<>();
        ContentMenuObserver contentMenuObserver = new ContentMenuObserver();
        contentMenuObserver.setTitle(title);
        contentMenuObserver.setNotifications(notifications);
        contentMenuObserver.setImages(images);
        contentMenuObserver.setContentMenuClickObserver(contentMenuClickObserver);
        contentMenuObservers.add(contentMenuObserver);
        contentMenuDashboardObserver.setContentMenuObservers(contentMenuObservers);
        return contentMenuDashboardObserver;
    }

    private DashboardMenuObserver footerMenuDashboardObserver(String title, String notifications, int images, FooterMenuClickObserver footerMenuClickObserver) {
        FooterMenuDashboardObserver footerMenuDashboardObserver = new FooterMenuDashboardObserver();
        List<FooterMenuObserver> footerMenuObservers = new ArrayList<>();
        FooterMenuObserver footerMenuObserver = new FooterMenuObserver();
        footerMenuObserver.setTitle(title);
        footerMenuObserver.setNotifications(notifications);
        footerMenuObserver.setImages(images);
        footerMenuObserver.setFooterMenuClickObserver(footerMenuClickObserver);
        footerMenuObservers.add(footerMenuObserver);
        footerMenuDashboardObserver.setFooterMenuObservers(footerMenuObservers);
        return footerMenuDashboardObserver;
    }

    @Override
    protected void initializeEvent() {
        DashboardObserverActivity.instance().setOnMusicObserver(new OnMusicObserver<RecentlyPlaylist>() {
            @Override
            public void startPlaying(RecentlyPlaylist recentlyPlaylist) {
                if (layoutMediaPlayer.getVisibility() == View.GONE) {
                    layoutMediaPlayer.setVisibility(View.VISIBLE);
                }

                View layout_media_player = LayoutInflater.from(context).inflate(R.layout.layout_media_player, layoutMediaPlayer, false);
                EllipsizeTextView titleSongPlaylistMusicPlayer = (EllipsizeTextView) bindView(layout_media_player, R.id.titleSongPlaylistMusicPlayer);
                durationSongPlaylistMusicPlayer = (EllipsizeTextView) bindView(layout_media_player, R.id.durationSongPlaylistMusicPlayer);
                sliderMediaPlayer = (SeekBar) bindView(layout_media_player, R.id.sliderMediaPlayer);
                LinearLayout layoutContainer = (LinearLayout) bindView(layout_media_player, R.id.layoutContainer);
                AppCompatImageButton previousMediaPlayer = (AppCompatImageButton) bindView(layout_media_player, R.id.previousMediaPlayer);
                AppCompatImageButton playMediaPlayer = (AppCompatImageButton) bindView(layout_media_player, R.id.playMediaPlayer);
                AppCompatImageButton nextMediaPlayer = (AppCompatImageButton) bindView(layout_media_player, R.id.nextMediaPlayer);
                AppCompatImageButton backwardMediaPlayer = (AppCompatImageButton) bindView(layout_media_player, R.id.backwardMediaPlayer);
                AppCompatImageButton forwardMediaPlayer = (AppCompatImageButton) bindView(layout_media_player, R.id.forwardMediaPlayer);
                AppCompatImageButton shareMediaPlayer = (AppCompatImageButton) bindView(layout_media_player, R.id.shareMediaPlayer);
                AppCompatImageButton toggleMediaPlayer = (AppCompatImageButton) bindView(layout_media_player, R.id.toggleMediaPlayer);

                File file = new File(recentlyPlaylist.getTitle());
                Uri uri = Uri.parse(file.getPath());

                MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                metaRetriever.setDataSource(context, uri);
                String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                metaRetriever.release();

                String strFileName = file.getName();
                titleSongPlaylistMusicPlayer.setText(strFileName);
                sliderMediaPlayer.setMax(DashboardObserverActivity.instance().getMediaPlayer().getDuration());
                sliderMediaPlayer.setProgress(DashboardObserverActivity.instance().getMediaPlayer().getCurrentPosition());
                sliderMediaPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            seekBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        int progress = seekBar.getProgress();
                        DashboardObserverActivity.instance().getMediaPlayer().seekTo(progress);

                    }
                });
                toggleMediaPlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutContainer.setVisibility(layoutContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                        toggleMediaPlayer.setImageResource(layoutContainer.getVisibility() == View.VISIBLE ? R.drawable.ic_bottom : R.drawable.ic_above);
                    }
                });
                previousMediaPlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DashboardObserverActivity.instance().previousMediaPlayer();
                    }
                });
                forwardMediaPlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        DashboardObserverActivity.instance().forwardMediaPlayer();
                    }
                });
                backwardMediaPlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        DashboardObserverActivity.instance().backwardMediaPlayer();
                    }
                });

                playMediaPlayer.setImageResource(R.drawable.ic_pause);
                playMediaPlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (DashboardObserverActivity.instance().getMediaPlayer().isPlaying()) {
                            playMediaPlayer.setImageResource(R.drawable.ic_play);
                            DashboardObserverActivity.instance().getMediaPlayer().pause();
                        } else {
                            playMediaPlayer.setImageResource(R.drawable.ic_pause);
                            DashboardObserverActivity.instance().getMediaPlayer().start();
                        }

                    }
                });

                nextMediaPlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DashboardObserverActivity.instance().nextMediaPlayer();
                    }
                });

                shareMediaPlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*if (BluetoothFragment.handlerBluetoothMusic != null && BluetoothFragment.context != null) {
                            DashboardObserverActivity.fileBluetoothMusic = new File(recentlyPlaylist.getTitle());

                            Message message = Message.obtain();
                            message.what = BluetoothFragment.STATE_MESSAGE_TRANSFER;
                            BluetoothFragment.handlerBluetoothMusic.sendMessage(message);
                        } else {
                            Toast.makeText(context, "Device not connected", Toast.LENGTH_SHORT).show();
                        }*/
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());

                        /*Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("");
                        DashboardObserverActivity.fileBluetoothMusic = new File(recentlyPlaylist.getTitle());

                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(DashboardObserverActivity.fileBluetoothMusic));

                        List<ResolveInfo> appsList = context.getPackageManager().queryIntentActivities(intent, 0);
                        Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, true, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                appsList.size());
                        if (appsList.size() > 0) {
                            String packageName = null;
                            String className = null;
                            boolean found = false;

                            for (ResolveInfo info : appsList) {
                                packageName = info.activityInfo.packageName;
                                if (packageName.equals("com.android.bluetooth")) {
                                    className = info.activityInfo.name;
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) {
                                Toast.makeText(context, "Bluetooth haven't been found", Toast.LENGTH_LONG).show();
                            } else {
                                intent.setClassName(packageName, className);
                                startActivity(intent);
                            }
                        }*/
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("*/*");
                        List<ResolveInfo> appsList = context.getPackageManager().queryIntentActivities(intent, 0);

                        DashboardObserverActivity.fileBluetoothMusic = new File(recentlyPlaylist.getTitle());
                        for (ResolveInfo info:
                             appsList) {
                            if (info.activityInfo.packageName.contains("bluetooth")) {
                                Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, true, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                        info.activityInfo.packageName,
                                        info.activityInfo.name);
                                intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                            }

                        }

                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(DashboardObserverActivity.fileBluetoothMusic));
                        startActivity(intent);
                    }
                });

                MediaPlayer mediaPlayer = DashboardObserverActivity.instance().getMediaPlayer();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (DashboardObserverActivity.instance().songCurrentPosition < (DashboardObserverActivity.instance().playlistsMusicPlayer.size() - 1)) {
                            DashboardObserverActivity.instance().songCurrentPosition = DashboardObserverActivity.instance().songCurrentPosition + 1;
                        } else {
                            DashboardObserverActivity.instance().songCurrentPosition = 0;
                        }

                        try {
                            RecentlyPlaylist playlist = DashboardObserverActivity.instance().playlistsMusicPlayer.get(DashboardObserverActivity.instance().songCurrentPosition);
                            Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, true, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                    playlist);

                            RecentlyPlaylist recentlyPlaylist = new RecentlyPlaylist(playlist.getImages(), playlist.getTitle(), playlist.getNotifications(), RecentlyPlaylistClickObserver.PROFILE);
                            DashboardObserverActivity.instance().onMusicPlayer(recentlyPlaylist);
                        } catch (IOException e) {
                            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                    e);
                        }

                    }
                });

                layoutMediaPlayer.removeAllViews();
                layoutMediaPlayer.addView(layout_media_player);
                updateMusicTime.run();
            }

            @Override
            public void stopPlaying(RecentlyPlaylist recentlyPlaylist) {

            }
        });
    }

    private Handler durationHandler = new Handler();
    private Runnable updateMusicTime = new Runnable() {
        public void run() {
            MediaPlayer mediaPlayer = DashboardObserverActivity.instance().getMediaPlayer();

            if (mediaPlayer != null) {
                int timeElapsed = mediaPlayer.getCurrentPosition();
                if (sliderMediaPlayer != null) {
                    sliderMediaPlayer.setProgress(timeElapsed);
                }

                String durations = Constants.duration(timeElapsed) + "/" + Constants.duration(mediaPlayer.getDuration());
                durationSongPlaylistMusicPlayer.setText(durations);

                durationHandler.postDelayed(this, 100);
            }
        }
    };

    @Override
    protected void toolbar() {
    }

    @Override
    public void onClick(View view) {
        if (view == toolbarBluetoothDevice) {
            onDashboardObserver.onBluetoothDevice();
        }

        if (view == toolbarSlideMenuImage) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }

    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        if (controller.getCurrentDestination() != null && controller.getCurrentDestination().getLabel() != null) {
            boolean check = navigatorHomeId == controller.getCurrentDestination().getId();
            homeNavController = controller;
            Logger.log(LoggerMessage.NONE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    check,
                    navigatorHomeId == R.id.homeFragment);

            if (!check) {
                navigatorHomeId = controller.getCurrentDestination().getId();
            }

            if (navigatorHomeId != R.id.bluetoothFragment) {
                toolbarBluetoothDevice.setVisibility(View.GONE);
            } else {
                toolbarBluetoothDevice.setVisibility(View.VISIBLE);
            }
        }
    }

    public class NavigationViewMenuAdapter extends GenericRecordsAdapter<DashboardMenuObserver, OnNavigationObserver> {

        private static final int HEADER = 0;
        private static final int CONTENTS = 1;
        private static final int FOOTER = 2;

        @Override
        public BaseHolder<DashboardMenuObserver, OnNavigationObserver> setViewHolder(ViewGroup parent, int typeOfView, OnNavigationObserver onNavigationObserver) {
            if (typeOfView == HEADER) {
                int layout_profile_header_menu = R.layout.layout_profile_header_menu;
                return new HeaderMenuViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_profile_header_menu, parent, false));
            } else if (typeOfView == CONTENTS) {
                int layout_content_menu = R.layout.layout_contents;
                return new ContentsMenuViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_content_menu, parent, false));
            } else {
                int layout_footer_menu = R.layout.layout_footer_menu;
                return new FooterMenuViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_footer_menu, parent, false));
            }
        }

        @Override
        protected void onBindData(BaseHolder<DashboardMenuObserver, OnNavigationObserver> holder, int position, DashboardMenuObserver dashboardMenuObserver, List<DashboardMenuObserver> dashboardMenuObservers, OnNavigationObserver onNavigationObserver) {
            if (holder instanceof HeaderMenuViewHolder) {
                HeaderMenuViewHolder headerMenuViewHolder = (HeaderMenuViewHolder) holder;
                headerMenuViewHolder.initialize(context, position, dashboardMenuObservers);

            } else if (holder instanceof ContentsMenuViewHolder) {
                ContentsMenuViewHolder contentsMenuViewHolder = (ContentsMenuViewHolder) holder;
                contentsMenuViewHolder.initialize(context, position, dashboardMenuObservers);

            } else if (holder instanceof FooterMenuViewHolder) {
                FooterMenuViewHolder footerMenuViewHolder = (FooterMenuViewHolder) holder;
                footerMenuViewHolder.initialize(context, position, dashboardMenuObservers);

            }
        }

        @Override
        public int getItemViewType(int position) {
            if (dashboardMenuObservers.get(position) instanceof HeaderMenuDashboardObserver) {
                return HEADER;
            } else if (dashboardMenuObservers.get(position) instanceof ContentMenuDashboardObserver) {
                return CONTENTS;
            } else {
                return FOOTER;
            }
        }

        private class HeaderMenuViewHolder extends BaseHolder<DashboardMenuObserver, OnNavigationObserver> {

            private TextView titleItems;
            private TextView notificationItems;
            private OnNavigationObserver navigationObserver;

            public HeaderMenuViewHolder(View itemView) {
                super(itemView);

                titleItems = (TextView) itemView.findViewById(R.id.titleItems);
                notificationItems = (TextView) itemView.findViewById(R.id.notificationItems);
            }

            @Override
            public void onBind(int position, DashboardMenuObserver dashboardMenuObserver, List<DashboardMenuObserver> records, OnNavigationObserver navigationObserver) {
                this.navigationObserver = navigationObserver;
                titleItems.setText(dashboardMenuObserver.getHeaderMenuObservers().get(0).getTitle());
                notificationItems.setText(dashboardMenuObserver.getHeaderMenuObservers().get(0).getNotifications());
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                navigationObserver.selectHeaderMenu(HeaderMenuClickObserver.PROFILE);
            }

            public void initialize(Context context, int position, List<DashboardMenuObserver> dashboardMenuObservers) {

            }
        }

        private class ContentsMenuViewHolder extends BaseHolder<DashboardMenuObserver, OnNavigationObserver> {

            private RecyclerView contentRecyclerView;

            private OnNavigationObserver navigationObserver;

            public ContentsMenuViewHolder(View itemView) {
                super(itemView);

                contentRecyclerView = (RecyclerView) itemView.findViewById(R.id.contentRecyclerView);

                RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(context);
                contentRecyclerView.setLayoutManager(linearLayoutManager);

            }

            @Override
            public void onBind(int position, DashboardMenuObserver dashboardMenuObserver, List<DashboardMenuObserver> records, OnNavigationObserver navigationObserver) {
                this.navigationObserver = navigationObserver;

                GenericRecordsAdapter<ContentMenuObserver, OnContentMenuObserver> contentMenuAdapter = new GenericRecordsAdapter<ContentMenuObserver, OnContentMenuObserver>() {
                    class ContentMenuViewHolder extends BaseHolder<ContentMenuObserver, OnContentMenuObserver> {

                        private AppCompatImageView logoItems;
                        private TextView titleItems;
                        private TextView notificationItems;
                        private OnContentMenuObserver contentMenuObserver;

                        public ContentMenuViewHolder(View itemView) {
                            super(itemView);

                            logoItems = (AppCompatImageView) itemView.findViewById(R.id.logoItems);
                            titleItems = (TextView) itemView.findViewById(R.id.titleItems);
                            notificationItems = (TextView) itemView.findViewById(R.id.notificationItems);

                        }

                        @Override
                        public void onBind(int position, ContentMenuObserver contentMenu, List<ContentMenuObserver> records, OnContentMenuObserver contentMenuObserver) {
                            this.contentMenuObserver = contentMenuObserver;

                            logoItems.setImageResource(contentMenu.getImages());
                            titleItems.setText(dashboardMenuObserver.getContentMenuObservers().get(0).getTitle());

                            itemView.setOnClickListener(this);
                        }

                        @Override
                        public void onClick(View view) {
                            contentMenuObserver.onContentMenu(getAdapterPosition());
                        }
                    }

                    @Override
                    public BaseHolder<ContentMenuObserver, OnContentMenuObserver> setViewHolder(ViewGroup parent, int typeOfView, OnContentMenuObserver onContentMenuObserver) {
                        int layout_content_menu = R.layout.layout_content_menu;
                        return new ContentMenuViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_content_menu, parent, false));
                    }

                    @Override
                    protected void onBindData(BaseHolder<ContentMenuObserver, OnContentMenuObserver> holder, int position, ContentMenuObserver contentMenu, List<ContentMenuObserver> records, OnContentMenuObserver onContentMenuObserver) {

                    }
                };
                contentMenuAdapter.addItemsObserver(dashboardMenuObservers.get(position).getContentMenuObservers());
                contentRecyclerView.setAdapter(contentMenuAdapter);
                contentMenuAdapter.setOnClickObserver(new OnContentMenuObserver() {
                    @Override
                    public void onContentMenu() {

                    }

                    @Override
                    public void onContentMenu(int adapterPosition) {
                        Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                contentMenuAdapter.getRecord(adapterPosition).getTitle(),
                                contentMenuAdapter.getRecord(adapterPosition).getContentMenuClickObserver().name());

                        navigationObserver.selectContentMenu(contentMenuAdapter.getRecord(adapterPosition).getContentMenuClickObserver());

                    }
                });

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

            }

            public void initialize(Context context, int position, List<DashboardMenuObserver> dashboardMenuObservers) {

            }
        }

        private class FooterMenuViewHolder extends BaseHolder<DashboardMenuObserver, OnNavigationObserver> {

            private AppCompatImageView logoItems;
            private TextView titleItems;
            private TextView notificationItems;
            private OnNavigationObserver navigationObserver;

            public FooterMenuViewHolder(View itemView) {
                super(itemView);

                logoItems = (AppCompatImageView) itemView.findViewById(R.id.logoItems);
                titleItems = (TextView) itemView.findViewById(R.id.titleItems);
                notificationItems = (TextView) itemView.findViewById(R.id.notificationItems);
            }

            @Override
            public void onBind(int position, DashboardMenuObserver dashboardMenuObserver, List<DashboardMenuObserver> records, OnNavigationObserver navigationObserver) {
                this.navigationObserver = navigationObserver;

                logoItems.setImageResource(dashboardMenuObserver.getFooterMenuObservers().get(0).getImages());
                titleItems.setText(dashboardMenuObserver.getFooterMenuObservers().get(0).getTitle());

                itemView.setOnClickListener(this);
            }

            public void initialize(Context context, int position, List<DashboardMenuObserver> dashboardMenuObservers) {

            }

            @Override
            public void onClick(View view) {
                navigationObserver.selectFooterMenu(FooterMenuClickObserver.LOGOUT);
            }
        }

    }

    public interface OnDashboardObserver {

        void onBluetooth();

        void onBluetoothDevice();

    }

    public static void setOnDashboardObserver(OnDashboardObserver onDashboardObserver) {
        DashboardFragment.onDashboardObserver = onDashboardObserver;
    }

    public interface OnContentMenuObserver {
        void onContentMenu();

        void onContentMenu(int adapterPosition);
    }

    public interface OnNavigationObserver {
        void selectHeaderMenu(HeaderMenuClickObserver headerMenuClickObserver);

        void selectContentMenu(ContentMenuClickObserver contentMenuClickObserver);

        void selectFooterMenu(FooterMenuClickObserver footerMenuClickObserver);

    }


}
