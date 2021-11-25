package com.android.bluetoothmusic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.android.bluetoothmusic.models.ObjectObserver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDatabaseHelper extends BluetoothDatabaseHelper {

    public PlaylistDatabaseHelper(Context context) {
        super(context);
    }

    public JSONObject showPlaylist(String userId) throws Exception {
        JSONObject playlistObject = new JSONObject();

        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + PLAYLIST_TABLE + " WHERE " + USER_ID + " = ?", new String[]{userId})) {
            if (cursor.moveToFirst()) {
                JSONArray playlistObjects = new JSONArray();

                while (!cursor.isAfterLast()) {
                    int totalColumn = cursor.getColumnCount();
                    JSONObject playlist = new JSONObject();

                    for (int i = 0; i < totalColumn; i++) {
                        if (cursor.getColumnName(i) != null && cursor.getString(i) != null) {
                            playlist.put(cursor.getColumnName(i), cursor.getString(i));
                        }
                    }

                    playlistObjects.put(playlist);

                    cursor.moveToNext();
                }
                cursor.close();

                playlistObject.put("message", "Playlist found!!");
                playlistObject.put("success", true);
                playlistObject.put("songs_length", playlistObjects.length());
                playlistObject.put("songs", playlistObjects);
                return playlistObject;
            }
        }

        playlistObject.put("message", "Playlist not available");
        playlistObject.put("success", false);
        return playlistObject;
    }

    public ObjectObserver showPlaylists(String userId) {
        ObjectObserver playlistObject = new ObjectObserver();

        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + PLAYLIST_TABLE + " WHERE " + USER_ID + " = ?", new String[]{userId})) {
            if (cursor.moveToFirst()) {
                List<ObjectObserver> playlistObjects = new ArrayList<>();

                while (!cursor.isAfterLast()) {
                    ObjectObserver playlist = new ObjectObserver();

                    playlist.setPlaylistId(cursor.getString(cursor.getColumnIndex("playlist_id")));
                    playlist.setPlaylistName(cursor.getString(cursor.getColumnIndex("playlist_name")));
                    playlist.setPlaylistImage(cursor.getString(cursor.getColumnIndex("playlist_image")));
                    playlistObjects.add(playlist);
                    cursor.moveToNext();
                }
                cursor.close();

                playlistObject.setMessage("Playlist found!!");
                playlistObject.setSuccess(true);
                playlistObject.setSongsLength(playlistObjects.size());
                playlistObject.setSongs(playlistObjects);
                return playlistObject;
            }
        }

        List<ObjectObserver> playlistObjects = new ArrayList<>();
        playlistObject.setSongs(playlistObjects);
        playlistObject.setMessage("Playlist not available");
        playlistObject.setSuccess(false);
        return playlistObject;
    }

    public ObjectObserver showPlaylistById(int userId, String playlistId) {
        ObjectObserver playlistObject = new ObjectObserver();

        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + PLAYLIST_TABLE + " WHERE " + USER_ID + " = ? AND " + PLAYLIST_ID + " = ?", new String[]{String.valueOf(userId), playlistId})) {
            if (cursor.moveToFirst()) {

                ObjectObserver playlist = new ObjectObserver();
                while (!cursor.isAfterLast()) {
                    playlist.setPlaylistId(cursor.getString(cursor.getColumnIndex("playlist_id")));
                    playlist.setPlaylistName(cursor.getString(cursor.getColumnIndex("playlist_name")));
                    playlist.setPlaylistImage(cursor.getString(cursor.getColumnIndex("playlist_image")));
                    cursor.moveToNext();
                }
                cursor.close();

                playlistObject.setMessage("Playlist found!!");
                playlistObject.setSuccess(true);
                playlistObject.setSong(playlist);
                return playlistObject;
            }
        }

        playlistObject.setMessage("Playlist not available");
        playlistObject.setSuccess(false);
        return playlistObject;
    }

    public ObjectObserver deletePlaylist(String userId, String playlistId) throws Exception {
        ObjectObserver objectObserver = deleteAllSongParticularPlaylist(userId, playlistId);

        ObjectObserver playlistObject = new ObjectObserver();
        playlistObject.setObjectObserver(objectObserver);

        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + PLAYLIST_TABLE + " WHERE " + USER_ID + " = ? AND " + PLAYLIST_ID + " = ?", new String[]{userId, playlistId})) {
            if (cursor.moveToFirst()) {
                boolean isDeleted = getWritableDatabase().delete(PLAYLIST_TABLE, PLAYLIST_ID + " = ? ", new String[]{playlistId}) > 0;
                cursor.close();
                playlistObject.setMessage("Playlist Deleted Successful!!");
                playlistObject.setSuccess(isDeleted);
                return playlistObject;
            }
        }

        playlistObject.setMessage("Playlist not available");
        playlistObject.setSuccess(false);
        return playlistObject;
    }

    public JSONObject createPlaylist(String userId, String playlistName, String playlistImage, String status, String created_at, String updated_at) throws Exception {
        JSONObject playlistObject = new JSONObject();

        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + PLAYLIST_TABLE + " WHERE " + PLAYLIST_NAME + " = ?", new String[]{playlistName})) {
            if (cursor.moveToFirst()) {
                playlistObject.put("message", "Playlist already exist!");
                playlistObject.put("success", false);
                return playlistObject;
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_ID, userId);
        contentValues.put(PLAYLIST_NAME, playlistName);
        contentValues.put(PLAYLIST_IMAGE, playlistImage);
        contentValues.put(STATUS, status);
        contentValues.put(CREATED_AT, created_at);
        contentValues.put(UPDATED_AT, updated_at);
        boolean createSuccessful = getWritableDatabase().insert(PLAYLIST_TABLE, null, contentValues) > 0;
        getWritableDatabase().close();

        if (createSuccessful) {
            String[] selectionArgs = {playlistName};
            Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + PLAYLIST_TABLE + " WHERE " + PLAYLIST_NAME + "= ?", selectionArgs);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int totalColumn = cursor.getColumnCount();

                for (int i = 0; i < totalColumn; i++) {
                    if (cursor.getColumnName(i) != null && cursor.getString(i) != null) {
                        playlistObject.put(cursor.getColumnName(i), cursor.getString(i));
                    }
                    playlistObject.put("message", "Playlist created Successful");
                    playlistObject.put("success", true);
                }
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            playlistObject.put("message", "Playlist Failed");
            playlistObject.put("success", false);
        }
        return playlistObject;
    }

    public ObjectObserver updatePlaylist(int userId, String playlistId, String playlistName, String playlistImage, String status, String updated_at) {
        ObjectObserver playlistObject = new ObjectObserver();

        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + PLAYLIST_TABLE + " WHERE " + PLAYLIST_NAME + " = ?", new String[]{playlistName})) {
            if (cursor.moveToFirst()) {
                playlistObject.setMessage("Playlist already exist!");
                playlistObject.setSuccess(true);
                return playlistObject;
            }
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_ID, userId);
        contentValues.put(PLAYLIST_NAME, playlistName);
        contentValues.put(PLAYLIST_IMAGE, playlistImage);
        contentValues.put(STATUS, status);
        contentValues.put(UPDATED_AT, updated_at);
        boolean createSuccessful = getWritableDatabase().update(PLAYLIST_TABLE, contentValues, PLAYLIST_ID + " = ?", new String[]{playlistId}) > 0;
        getWritableDatabase().close();

        if (createSuccessful) {
            String[] selectionArgs = {playlistName};
            Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + PLAYLIST_TABLE + " WHERE " + PLAYLIST_NAME + "= ?", selectionArgs);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                List<ObjectObserver> playlistObjects = new ArrayList<>();

                while (!cursor.isAfterLast()) {
                    ObjectObserver playlist = new ObjectObserver();

                    playlist.setPlaylistId(cursor.getString(cursor.getColumnIndex("playlist_id")));
                    playlist.setPlaylistName(cursor.getString(cursor.getColumnIndex("playlist_name")));
                    playlist.setPlaylistImage(cursor.getString(cursor.getColumnIndex("playlist_image")));
                    playlistObjects.add(playlist);

                    cursor.moveToNext();
                }
                cursor.close();

                playlistObject.setSongs(playlistObjects);
                playlistObject.setMessage("Playlist updated Successful");
                playlistObject.setSuccess(true);
            }
        } else {
            playlistObject.setMessage("Playlist Failed");
            playlistObject.setSuccess(true);
        }
        return playlistObject;
    }

    public ObjectObserver addSongPlaylist(int userId, String playlistId, String playlistName, String playlistImage, String status, String created_at, String updated_at) throws Exception {
        ObjectObserver playlistObject = new ObjectObserver();

        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + SONG_PLAYLIST_TABLE + " WHERE " + SONG_PLAYLIST_NAME + " = ? AND "+ PLAYLIST_ID + " = ?", new String[]{playlistName, playlistId})) {
            if (cursor.moveToFirst()) {
                playlistObject.setMessage("Song Playlist already exist!");
                playlistObject.setSuccess(false);
                return playlistObject;
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_ID, userId);
        contentValues.put(PLAYLIST_ID, playlistId);
        contentValues.put(SONG_PLAYLIST_NAME, playlistName);
        contentValues.put(SONG_PLAYLIST_IMAGE, "playlistImage");
        contentValues.put(STATUS, status);
        contentValues.put(CREATED_AT, created_at);
        contentValues.put(UPDATED_AT, updated_at);
        boolean createSuccessful = getWritableDatabase().insert(SONG_PLAYLIST_TABLE, null, contentValues) > 0;
        getWritableDatabase().close();

        if (createSuccessful) {
            String[] selectionArgs = {playlistId};
            Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + SONG_PLAYLIST_TABLE + " WHERE " + SONG_PLAYLIST_ID + "= ?", selectionArgs);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ObjectObserver playlist = new ObjectObserver();
                while (!cursor.isAfterLast()) {
                    playlist.setPlaylistId(cursor.getString(cursor.getColumnIndex(SONG_PLAYLIST_ID)));
                    playlist.setPlaylistName(cursor.getString(cursor.getColumnIndex(SONG_PLAYLIST_NAME)));
                    //playlist.setPlaylistImage(cursor.getString(cursor.getColumnIndex(SONG_PLAYLIST_IMAGE)));
                    cursor.moveToNext();
                }
                playlistObject.setMessage("Song playlist created Successful");
                playlistObject.setSuccess(true);
                playlistObject.setSong(playlist);
            }
            cursor.close();
        } else {
            playlistObject.setMessage("Song Playlist Failed");
            playlistObject.setSuccess(false);
        }
        return playlistObject;
    }

    public ObjectObserver showSongPlaylist(int userId, String playlistId) {
        ObjectObserver playlistObject = new ObjectObserver();

        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + SONG_PLAYLIST_TABLE + " WHERE " + USER_ID + " = ? AND " + PLAYLIST_ID + " = ?", new String[]{String.valueOf(userId), playlistId})) {
            if (cursor.moveToFirst()) {

                List<ObjectObserver> playlistObjects = new ArrayList<>();
                while (!cursor.isAfterLast()) {
                    ObjectObserver playlist = new ObjectObserver();

                    playlist.setPlaylistId(cursor.getString(cursor.getColumnIndex(SONG_PLAYLIST_ID)));
                    playlist.setPlaylistName(cursor.getString(cursor.getColumnIndex(SONG_PLAYLIST_NAME)));
                    playlist.setPlaylistImage(cursor.getString(cursor.getColumnIndex(SONG_PLAYLIST_IMAGE)));
                    playlistObjects.add(playlist);
                    cursor.moveToNext();
                }
                cursor.close();

                playlistObject.setMessage("Song Playlist found!!");
                playlistObject.setSuccess(true);
                playlistObject.setSongs(playlistObjects);
                return playlistObject;
            }
        }

        playlistObject.setMessage("Playlist not available");
        playlistObject.setSuccess(false);
        return playlistObject;
    }

    public ObjectObserver deleteAllSongParticularPlaylist(String userId, String playlistId) throws Exception {
        ObjectObserver playlistObject = new ObjectObserver();

        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + SONG_PLAYLIST_TABLE + " WHERE " + USER_ID + " = ? AND " + PLAYLIST_ID + " = ?", new String[]{userId, playlistId})) {
            if (cursor.moveToFirst()) {
                boolean isDeleted = getWritableDatabase().delete(SONG_PLAYLIST_TABLE, PLAYLIST_ID + " = ? ", new String[]{playlistId}) > 0;
                cursor.close();
                playlistObject.setMessage("Playlist Deleted Successful!!");
                playlistObject.setSuccess(isDeleted);
                return playlistObject;
            }
        }

        playlistObject.setMessage("Playlist not available");
        playlistObject.setSuccess(false);
        return playlistObject;
    }

    public ObjectObserver deleteSongPlaylist(String userId, String songPlaylistId) throws Exception {
        ObjectObserver playlistObject = new ObjectObserver();

        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + SONG_PLAYLIST_TABLE + " WHERE " + USER_ID + " = ? AND " + SONG_PLAYLIST_ID + " = ?", new String[]{userId, songPlaylistId})) {
            if (cursor.moveToFirst()) {
                boolean isDeleted = getWritableDatabase().delete(SONG_PLAYLIST_TABLE, SONG_PLAYLIST_ID + " = ? ", new String[]{songPlaylistId}) > 0;
                cursor.close();
                playlistObject.setMessage("Song remove from playlist!!");
                playlistObject.setSuccess(isDeleted);
                return playlistObject;
            }
        }

        playlistObject.setMessage("Playlist not available");
        playlistObject.setSuccess(false);
        return playlistObject;
    }

    public ObjectObserver addRecentlyPlayedSong(int userId,
                                                  String recentlyPlayedSongName,
                                                  String playlistImage,
                                                  String status,
                                                  String created_at,
                                                  String updated_at) throws Exception {
        ObjectObserver playlistObject = new ObjectObserver();

        String query = "SELECT * FROM " + RECENTLY_PLAYED_SONG_TABLE + " WHERE " + RECENTLY_PLAYED_SONG_NAME + " = ?";
        try (Cursor cursor = getReadableDatabase().rawQuery(query, new String[]{recentlyPlayedSongName})) {
            if (cursor.moveToFirst()) {
                playlistObject.setSuccess(false);
                return playlistObject;
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_ID, userId);
        contentValues.put(RECENTLY_PLAYED_SONG_NAME, recentlyPlayedSongName);
        contentValues.put(RECENTLY_PLAYED_SONG_IMAGE, "playlistImage");
        contentValues.put(STATUS, status);
        contentValues.put(CREATED_AT, created_at);
        contentValues.put(UPDATED_AT, updated_at);
        boolean createSuccessful = getWritableDatabase().insert(RECENTLY_PLAYED_SONG_TABLE, null, contentValues) > 0;
        getWritableDatabase().close();

        if (createSuccessful) {
            String query2 = "SELECT * FROM " + RECENTLY_PLAYED_SONG_TABLE;
            Cursor cursor = getReadableDatabase().rawQuery(query2, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ObjectObserver playlist = new ObjectObserver();
                while (!cursor.isAfterLast()) {
                    playlist.setPlaylistId(cursor.getString(cursor.getColumnIndex(RECENTLY_PLAYED_SONG_ID)));
                    playlist.setPlaylistName(cursor.getString(cursor.getColumnIndex(RECENTLY_PLAYED_SONG_NAME)));
                    //playlist.setPlaylistImage(cursor.getString(cursor.getColumnIndex(SONG_PLAYLIST_IMAGE)));
                    cursor.moveToNext();
                }
                playlistObject.setSuccess(true);
                playlistObject.setSong(playlist);
            }
            cursor.close();
        } else {
            playlistObject.setSuccess(false);
        }
        return playlistObject;
    }

    public ObjectObserver showRecentlyPlayedSong(int userId) {
        ObjectObserver playlistObject = new ObjectObserver();

        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + RECENTLY_PLAYED_SONG_TABLE + " WHERE " + USER_ID + " = ?", new String[]{String.valueOf(userId)})) {
            if (cursor.moveToFirst()) {

                List<ObjectObserver> playlistObjects = new ArrayList<>();
                while (!cursor.isAfterLast()) {
                    ObjectObserver playlist = new ObjectObserver();

                    playlist.setPlaylistId(cursor.getString(cursor.getColumnIndex(RECENTLY_PLAYED_SONG_ID)));
                    playlist.setPlaylistName(cursor.getString(cursor.getColumnIndex(RECENTLY_PLAYED_SONG_NAME)));
                    playlist.setPlaylistImage(cursor.getString(cursor.getColumnIndex(RECENTLY_PLAYED_SONG_NAME)));
                    playlistObjects.add(playlist);
                    cursor.moveToNext();
                }
                cursor.close();
                playlistObject.setSuccess(true);
                playlistObject.setSongs(playlistObjects);
                return playlistObject;
            }
        }
        playlistObject.setSuccess(false);
        List<ObjectObserver> playlistObjects = new ArrayList<>();
        playlistObject.setSongs(playlistObjects);
        return playlistObject;
    }

}
