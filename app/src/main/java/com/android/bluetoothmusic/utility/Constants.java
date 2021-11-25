package com.android.bluetoothmusic.utility;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Constants {

    public static final UUID uuid = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
    public static final String CHANNEL_ID = "bluetooth_music";
    public static final int TOKEN_ID = 200;
    public static String createDate = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH).format(new Date());
    public static final int seekForwardTime = 5000;
    public static final int seekBackwardTime = 5000;

    public static String getFileSizeLabel(File file) {
        double size = file.length() / 1024.0;
        if (size >= 1024) {
            return new DecimalFormat("##.##").format((size / 1024)) + " MB";
        } else {
            return new DecimalFormat("##.##").format(size) + " KB";
        }
    }

    public static String formatMilliSecond(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }

    public static String duration(long milliseconds) {
        return String.format(Locale.ENGLISH, "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    public interface ACTION {
        String PREVIOUS_ACTION = "com.android.bluetoothmusic.action.prev";
        String PLAY_ACTION = "com.android.bluetoothmusic.action.play";
        String PAUSE_ACTION = "com.android.bluetoothmusic.action.pause";
        String NEXT_ACTION = "com.android.bluetoothmusic.action.next";
        String START_FOREGROUND_ACTION = "com.android.bluetoothmusic.action.startforeground";
        String STOP_FOREGROUND_ACTION = "com.android.bluetoothmusic.action.stopforeground";

    }
}
