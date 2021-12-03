package com.android.bluetoothmusic.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.fragment.authenticate.LoginFragment;
import com.android.bluetoothmusic.fragment.bluetooth.BluetoothFragment;
import com.android.bluetoothmusic.legacy.observer.OnMusicObserver;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;
import com.android.bluetoothmusic.models.music.RecentlyPlaylist;
import com.android.bluetoothmusic.models.music.RecentlyPlaylistClickObserver;
import com.android.bluetoothmusic.notification.MediaPlayerService;
import com.android.bluetoothmusic.session.SessionManager;
import com.android.bluetoothmusic.session.SessionPreferences;
import com.android.bluetoothmusic.utility.BluetoothSocketReceiver;
import com.android.bluetoothmusic.utility.Constants;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DashboardObserverActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {

    //this is hte testing of the changes on the dev-branch to merge

    //This is branch 2


    private static final int REQUIRE_PERMISSIONS_RESULT = 901;
    public static DashboardObserverActivity context;

    //declaring instance variables
    private static MediaPlayer mediaPlayer;
    private NavController dashboardNavController;
    private int navigatorId;
    private RelativeLayout layoutContainer;
    private OnMusicObserver<RecentlyPlaylist> onMusicObserver;
    public int songCurrentPosition;
    public boolean isWorking = false;

    //Lists to get the playlists and the permission status
    public List<RecentlyPlaylist> playlistsMusicPlayer = new ArrayList<>();
    private ArrayList<String> permissionsToRequest = new ArrayList<>();
    private ArrayList<String> permissionsRejected = new ArrayList<>();

    public static BluetoothSocketReceiver bluetoothSocketReceiver;
    public static File fileBluetoothMusic;
    private Intent serviceIntent;


    public static DashboardObserverActivity instance() {
        return context;
    }
    //list to store the recently played list
    public void setPlaylistsMusicPlayer(List<RecentlyPlaylist> playlistsMusicPlayer) {
        this.playlistsMusicPlayer = new ArrayList<>();
        this.playlistsMusicPlayer = playlistsMusicPlayer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_observer);

        context = DashboardObserverActivity.this;
        //creates ths instance of mediaplayer to be called later
        serviceIntent = new Intent(context, MediaPlayerService.class);

        layoutContainer = (RelativeLayout) findViewById(R.id.layoutContainer);

        dashboardNavController = Navigation.findNavController(context, R.id.dashboard_navigation_host_fragment);
        dashboardNavController.addOnDestinationChangedListener(this);

        askPermissions();
    }
    // Passes the start and play of the song
    public void setOnMusicObserver(OnMusicObserver<RecentlyPlaylist> onMusicObserver) {
        this.onMusicObserver = onMusicObserver;
    }

    //Prepares the player for playback, synchronously.
    public void onMusicPlayer(RecentlyPlaylist playlists) throws IOException {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(playlists.getTitle());
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        });
        mediaPlayer.prepare();

        onMusicObserver.startPlaying(playlists);

        fileBluetoothMusic = new File(playlists.getTitle());
        String strFileName = fileBluetoothMusic.getName();
        Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                playlists.getTitle());
        //showNotification(strFileName);
        playBackgroundSound();

        if (isWorking && BluetoothFragment.handlerBluetoothMusic != null && BluetoothFragment.context != null) {

            Message message = Message.obtain();
            message.what = BluetoothFragment.STATE_MESSAGE_TRANSFER;
            BluetoothFragment.handlerBluetoothMusic.sendMessage(message);
        }
    }
    //controls the functions for media player in this case forward playing
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    // When presses forward the song goes forward using this method
    // The constants is the duration the song will go forward by
    public void forwardMediaPlayer() {
        MediaPlayer mediaPlayer = DashboardObserverActivity.instance().getMediaPlayer();
        int currentPosition = mediaPlayer.getCurrentPosition();
        if (currentPosition + Constants.seekForwardTime <= mediaPlayer.getDuration()) {
            mediaPlayer.seekTo(currentPosition + Constants.seekForwardTime);
        } else {
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }


    // When presses forward the song goes backward using this method
    // The constants is the duration the song will go backward by 5 seconds

    public void backwardMediaPlayer() {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            if (currentPosition - Constants.seekBackwardTime >= 0) {
                mediaPlayer.seekTo(currentPosition - Constants.seekBackwardTime);
            } else {
                mediaPlayer.seekTo(0);
            }
        }
    }
    // Changes the current song position to next song position
    public void nextMediaPlayer() {
        if ((DashboardObserverActivity.instance().playlistsMusicPlayer.size() - 1) > DashboardObserverActivity.instance().songCurrentPosition) {
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
    // Changes the current song position to the song one before.

    public void previousMediaPlayer() {
        if (DashboardObserverActivity.instance().songCurrentPosition > 0) {
            DashboardObserverActivity.instance().songCurrentPosition = DashboardObserverActivity.instance().songCurrentPosition - 1;
        } else {
            DashboardObserverActivity.instance().songCurrentPosition = DashboardObserverActivity.instance().playlistsMusicPlayer.size() - 1;
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

    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        if (controller.getCurrentDestination() != null && controller.getCurrentDestination().getLabel() != null) {
            boolean check = navigatorId == controller.getCurrentDestination().getId();
            Logger.logger(LoggerMessage.THROWABLE, false, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    check,
                    navigatorId == R.id.dashboardFragment);
            if (check) {

            } else {
                navigatorId = controller.getCurrentDestination().getId();
            }
        }
    }
    //start of the application ask for the device access permissions.
    // If the permissions are not granted message is displayed for the requirement.
    private void askPermissions() {
        ArrayList<String> requirePermissions = new ArrayList<>();
        permissionsToRequest = new ArrayList<>();
        permissionsRejected = new ArrayList<>();

        requirePermissions.add(Manifest.permission.CAMERA);
        requirePermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        requirePermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        requirePermissions.add(Manifest.permission.READ_PHONE_STATE);
        requirePermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        requirePermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        for (String permission : requirePermissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        //Represents the local device bluetooth adaptor .
        // the adaptor allows the perform task on the device bluetooth.
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }

        if (permissionsToRequest.size() > 0) { // if the permission was granted
            ActivityCompat.requestPermissions(context, permissionsToRequest.toArray(new String[0]), REQUIRE_PERMISSIONS_RESULT);
        } else {
            start();
        }

    }

    private void start() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                NavOptions.Builder builder = new NavOptions.Builder();
                builder.setEnterAnim(R.anim.animation_translate_enter_slide);
                builder.setExitAnim(R.anim.animation_translate_exit_slide);
                builder.setPopEnterAnim(R.anim.animation_translate_pop_enter_slide);
                builder.setPopExitAnim(R.anim.animation_translate_pop_exit_slide);
                builder.setPopUpTo(R.id.splashScreenFragment, true);

                NavOptions options = builder.build();
                Bundle bundle = new Bundle();
                SessionManager sessionManager = SessionManager.getInstance(context);
                boolean isLogin = sessionManager.booleanKey(SessionPreferences.LOGIN.name());

                if (isLogin) {
                    /**
                     * {@link com.android.bluetoothmusic.fragment.dashboard.DashboardFragment}
                     */
                    dashboardNavController.navigate(R.id.dashboardFragment, bundle, options);
                } else {
                    /**
                     * {@link LoginFragment}
                     */
                    dashboardNavController.navigate(R.id.loginFragment, bundle, options);
                }

            }
        }, 1000);

    }
    // Checks the permission results and deals with the user input if the permission is not granted .
    // Sends the  info to the user that the permissions are required.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsRejected.clear();
        if (requestCode == REQUIRE_PERMISSIONS_RESULT) {
            if (permissionsToRequest != null) {

                for (String permission : permissionsToRequest) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                            permissionsRejected.add(permission);
                        }
                    }
                }
            }

            if (permissionsRejected.size() > 0) {
                Snackbar.make(layoutContainer, "These permissions are mandatory for this application. Please allow access.", Snackbar.LENGTH_SHORT).show();

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("These permissions are mandatory for this application. Please allow access.");
                alert.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            ActivityCompat.requestPermissions((Activity) context, permissionsRejected.toArray(new String[permissionsRejected.size()]), REQUIRE_PERMISSIONS_RESULT);
                        }
                    }
                });
                alert.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            } else {
                start();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public void playBackgroundSound() {
        serviceIntent.setAction(Constants.ACTION.START_FOREGROUND_ACTION);
        startService(serviceIntent);
    }

    // Closes the activity if the intent is destroyed.
    @Override
    protected void onDestroy() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (serviceIntent != null) {
            serviceIntent.setAction(Constants.ACTION.STOP_FOREGROUND_ACTION);
            stopService(serviceIntent);
        }
        super.onDestroy();
    }
}