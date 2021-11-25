package com.android.bluetoothmusic.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.models.Audio;
import com.android.bluetoothmusic.models.music.RecentlyPlaylistClickObserver;
import com.android.bluetoothmusic.models.music.RecentlyPlaylist;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileUtils {

    private static final String MEDIA_PATH = Environment.getExternalStorageDirectory() + "";

    public static List<RecentlyPlaylist> getAllAudioFromDevice(Context context) {

        final List<RecentlyPlaylist> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.ArtistColumns.ARTIST,};
        Cursor c = context.getContentResolver().query(uri, projection, MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%yourFolderName%"}, null);

        if (c != null) {
            while (c.moveToNext()) {

                String path = c.getString(0);
                String album = c.getString(1);
                String artist = c.getString(2);

                String name = path.substring(path.lastIndexOf("/") + 1);

                RecentlyPlaylist audioModel = new RecentlyPlaylist(R.drawable.ic_bluetooth_audio, name, artist, RecentlyPlaylistClickObserver.PROFILE);

                Log.e("Name :" + name, " Album :" + album);
                Log.e("Path :" + path, " Artist :" + artist);

                tempAudioList.add(audioModel);
            }
            c.close();
        }

        return tempAudioList;
    }

    public static ArrayList<HashMap<String, String>> getPlayList() {
        ArrayList<HashMap<String, String>> songsPlaylist = new ArrayList<HashMap<String, String>>();

        File home = new File(MEDIA_PATH);
        FileExtensionFilter fileExtensionFilter = new FileExtensionFilter();

        //if (home.listFiles(new FileExtensionFilter()).length > 0) {
        if (home.listFiles(fileExtensionFilter) != null) {

            for (File file : home.listFiles(fileExtensionFilter)) {
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("song_title", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("song_path", file.getPath());

                // Adding each song to SongList
                songsPlaylist.add(song);
            }
        }
        // return songs list array
        return songsPlaylist;
    }

    public static List<Audio> loadAudio(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        List<Audio> audioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));

                // Save to audioList
                audioList.add(new Audio(data, title, album, artist));
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        return audioList;
    }
}
