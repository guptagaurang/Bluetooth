package com.android.bluetoothmusic.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.utility.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class NotificationObserver {

    private static String channelId = Constants.CHANNEL_ID;

    public static void showNotification(Context context, RemoteMessage notifications) throws JSONException {

        JSONObject jsonObject = new JSONObject("{ \"title\":\" " + notifications.getTitle() + "\", \"message\":\" " + notifications.getBody() + " \"}");
        String title = jsonObject.optString("title");
        String message = jsonObject.optString("message");

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_notification_remainder);
        remoteViews.setTextViewText(R.id.txtTitle, title);
        remoteViews.setTextViewText(R.id.txtMsg, message);
        remoteViews.setImageViewBitmap(R.id.ivLogo, icon);

        //String channelId = String.valueOf(((new Date(System.currentTimeMillis()).getTime() / 1000L) % Integer.MAX_VALUE));

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setCustomBigContentView(remoteViews);
        notificationBuilder.setCustomContentView(remoteViews);
        notificationBuilder.setCustomHeadsUpContentView(remoteViews);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setPriority(android.app.Notification.PRIORITY_DEFAULT);
        notificationBuilder.setAutoCancel(true);

        notificationBuilder.setLargeIcon(icon);

        Notification notification = notificationBuilder.build();
        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.vibrate = new long[]{0, 400, 200, 400};

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        int notificationId = (int) ((new Date(System.currentTimeMillis()).getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManager.notify(notificationId, notification);
    }

}
