package com.android.bluetoothmusic.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class BluetoothDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bluetooth.db";

    public static final String BLUETOOTH_MUSIC_TABLE = "BLUETOOTH_MUSIC";
    public static final String USER_ID = "user_id";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String ADDRESS = "address";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String STATUS = "status";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";

    public static final String PLAYLIST_TABLE = "PLAYLIST";
    public static final String PLAYLIST_ID = "playlist_id";
    public static final String PLAYLIST_NAME = "playlist_name";
    public static final String PLAYLIST_IMAGE = "playlist_image";

    public static final String SONG_PLAYLIST_TABLE = "SONG_PLAYLIST";
    public static final String SONG_PLAYLIST_ID = "song_playlist_id";
    public static final String SONG_PLAYLIST_NAME = "song_playlist_name";
    public static final String SONG_PLAYLIST_IMAGE = "song_playlist_image";

    public static final String RECENTLY_PLAYED_SONG_TABLE = "RECENTLY_PLAYED_SONG";
    public static final String RECENTLY_PLAYED_SONG_ID = "recently_played_song_id";
    public static final String RECENTLY_PLAYED_SONG_NAME = "recently_played_song_name";
    public static final String RECENTLY_PLAYED_SONG_IMAGE = "recently_played_song_image";

    public BluetoothDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + BLUETOOTH_MUSIC_TABLE + " ( " + USER_ID + " integer primary key, " + USERNAME + " text," + EMAIL + " text," + PASSWORD + " text, " + ADDRESS + " text," + PHONE_NUMBER + " text, " + STATUS + " text, " + CREATED_AT + " text, " + UPDATED_AT + " text)");
        db.execSQL("CREATE TABLE " + PLAYLIST_TABLE + " ( " + PLAYLIST_ID + " integer primary key, " + USER_ID + " text," + PLAYLIST_NAME + " text," + PLAYLIST_IMAGE + " text," + STATUS + " text, " + CREATED_AT + " text, " + UPDATED_AT + " text)");
        db.execSQL("CREATE TABLE " + SONG_PLAYLIST_TABLE + " ( " + SONG_PLAYLIST_ID + " integer primary key, " + USER_ID + " integer," + PLAYLIST_ID + " integer," + SONG_PLAYLIST_NAME + " text," + SONG_PLAYLIST_IMAGE + " text," + STATUS + " text, " + CREATED_AT + " text, " + UPDATED_AT + " text)");
        db.execSQL("CREATE TABLE " + RECENTLY_PLAYED_SONG_TABLE + " ( " + RECENTLY_PLAYED_SONG_ID + " integer primary key, " + USER_ID + " integer," + RECENTLY_PLAYED_SONG_NAME + " text," + RECENTLY_PLAYED_SONG_IMAGE + " text," + STATUS + " text, " + CREATED_AT + " text, " + UPDATED_AT + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BLUETOOTH_MUSIC_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PLAYLIST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SONG_PLAYLIST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RECENTLY_PLAYED_SONG_TABLE);
        onCreate(db);
    }

}