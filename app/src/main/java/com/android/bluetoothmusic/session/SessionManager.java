package com.android.bluetoothmusic.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static SharedPreferences sharedPreferences;
    private static SessionManager sessionManager;

    public static SessionManager getInstance(Context context) {
        if (sessionManager == null) {
            sessionManager = new SessionManager(context);
        }
        return sessionManager;
    }

    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SessionPreferences.SESSION_MANAGER.name(), Context.MODE_PRIVATE);
    }


    public void save(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String stringKey(String key) {
        return sharedPreferences.getString(key, "");
    }


    public void save(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean booleanKey(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void save(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int numberKey(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
