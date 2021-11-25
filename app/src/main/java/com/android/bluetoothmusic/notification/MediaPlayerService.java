package com.android.bluetoothmusic.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.activity.DashboardObserverActivity;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;
import com.android.bluetoothmusic.utility.Constants;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private static final String channelId = Constants.CHANNEL_ID;
    private final IBinder mBinder = new LocalBinder();

    private MediaPlayer mediaPlayer;
    private RemoteViews remoteViews;
    private NotificationCompat.Builder notificationBuilder;

    public class LocalBinder extends Binder {
        MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.ACTION.START_FOREGROUND_ACTION)) {
            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    "Service Started");
            showNotifications();
        } else if (intent.getAction().equals(Constants.ACTION.PREVIOUS_ACTION)) {
            DashboardObserverActivity.instance().previousMediaPlayer();

            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    "Clicked Previous");
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            if (remoteViews != null) {
                remoteViews.setViewVisibility(R.id.playMediaPlayer, View.GONE);
                remoteViews.setViewVisibility(R.id.pauseMediaPlayer, View.VISIBLE);
                notificationBuilder.setCustomContentView(remoteViews);
                Notification notification = notificationBuilder.build();
                startForeground(Constants.TOKEN_ID, notification);
            }

            mediaPlayer = DashboardObserverActivity.instance().getMediaPlayer();
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    "Clicked Play");
        } else if (intent.getAction().equals(Constants.ACTION.PAUSE_ACTION)) {
            if (remoteViews != null) {
                remoteViews.setViewVisibility(R.id.playMediaPlayer, View.VISIBLE);
                remoteViews.setViewVisibility(R.id.pauseMediaPlayer, View.GONE);
                notificationBuilder.setCustomContentView(remoteViews);
                Notification notification = notificationBuilder.build();
                startForeground(Constants.TOKEN_ID, notification);
            }

            mediaPlayer = DashboardObserverActivity.instance().getMediaPlayer();
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }

            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    "Clicked Pause");
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            DashboardObserverActivity.instance().nextMediaPlayer();
            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    "Clicked Next");
        } else if (intent.getAction().equals(Constants.ACTION.STOP_FOREGROUND_ACTION)) {
            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    "Service stop");
            mediaPlayer = DashboardObserverActivity.instance().getMediaPlayer();
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    private void showNotifications() {
        remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notification);

        remoteViews.setViewVisibility(R.id.previousMediaPlayer, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.playMediaPlayer, View.GONE);
        remoteViews.setViewVisibility(R.id.pauseMediaPlayer, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.nextMediaPlayer, View.VISIBLE);

        remoteViews.setTextViewText(R.id.titleSongPlaylist, DashboardObserverActivity.fileBluetoothMusic.getName());

        Intent previousIntent = new Intent(this, MediaPlayerService.class);
        previousIntent.setAction(Constants.ACTION.PREVIOUS_ACTION);
        PendingIntent pendingPreviousIntent = PendingIntent.getService(this, 0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.previousMediaPlayer, pendingPreviousIntent);

        Intent playIntent = new Intent(this, MediaPlayerService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.playMediaPlayer, pendingPlayIntent);

        Intent pauseIntent = new Intent(this, MediaPlayerService.class);
        pauseIntent.setAction(Constants.ACTION.PAUSE_ACTION);
        PendingIntent pendingPauseIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.pauseMediaPlayer, pendingPauseIntent);

        Intent nextIntent = new Intent(this, MediaPlayerService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pendingNextIntent = PendingIntent.getService(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.nextMediaPlayer, pendingNextIntent);

        Intent cancelPlayerIntent = new Intent(this, MediaPlayerService.class);
        cancelPlayerIntent.setAction(Constants.ACTION.STOP_FOREGROUND_ACTION);
        PendingIntent pendingCancelPlayerIntent = PendingIntent.getService(this, 0, cancelPlayerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.cancelMediaPlayer, pendingCancelPlayerIntent);

        notificationBuilder = new NotificationCompat.Builder(this, channelId);
        notificationBuilder.setCustomBigContentView(remoteViews);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setPriority(android.app.Notification.PRIORITY_DEFAULT);
        notificationBuilder.setAutoCancel(true);
        Notification notification = notificationBuilder.build();
        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.vibrate = new long[]{0, 400, 200, 400};

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        //notificationManager.notify(0, notification);
        startForeground(Constants.TOKEN_ID, notification);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopPlaying();
    }

    private void stopPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }
}