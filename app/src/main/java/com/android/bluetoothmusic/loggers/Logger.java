package com.android.bluetoothmusic.loggers;

import android.util.Log;

import java.util.Locale;

public class Logger {

    public static void logger(LoggerMessage loggerSchema, boolean isPrint, Object... objects) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 1; i < objects.length; i++) {
            if (i + 1 < objects.length) {
                stringBuilder.append(String.format(Locale.ENGLISH, "%s", objects[i])).append(" \n:: ");
            } else {
                stringBuilder.append(String.format(Locale.ENGLISH, "%s", objects[i])).append("\n||");
            }
        }
        if (isPrint) {
            Log.e(String.valueOf(loggerSchema), objects[0] + " ::\n " + stringBuilder.toString());
        }
    }

    public static void log(LoggerMessage loggerSchema, Object... objects) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 1; i < objects.length; i++) {
            if (i + 1 < objects.length) {
                stringBuilder.append(objects[i]).append(" ::\n");
                //stringBuilder.append(String.format(Locale.ENGLISH, "%s", objects[i])).append(" \n:: ");
            } else {
                //stringBuilder.append(String.format(Locale.ENGLISH, "%s", objects[i])).append("\n||");
                stringBuilder.append(objects[i]).append("\n||");

            }
        }
        if (loggerSchema != LoggerMessage.NONE) {
            Log.e(String.valueOf(loggerSchema), objects[0] + " ::\n " + stringBuilder.toString());
        }
    }

    public static String getThread(StackTraceElement stackTraceElement) {
        return String.format(Locale.ENGLISH, "(%s:%d)", stackTraceElement.getFileName(), stackTraceElement.getLineNumber());
    }

}