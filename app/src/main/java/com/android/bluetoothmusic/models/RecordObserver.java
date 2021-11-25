package com.android.bluetoothmusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecordObserver<Record> {

    @SerializedName("songs_name")
    @Expose
    private String songsName;
    @SerializedName("playlist_id")
    @Expose
    private String playlistId;
    @SerializedName("playlist_name")
    @Expose
    private String playlistName;
    @SerializedName("playlist_image")
    @Expose
    private String playlistImage;
    @SerializedName("songs")
    @Expose
    private List<Record> songs;
    private Record song;

    public String getSongsName() {
        return songsName;
    }

    public void setSongsName(String songsName) {
        this.songsName = songsName;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistImage() {
        return playlistImage;
    }

    public void setPlaylistImage(String playlistImage) {
        this.playlistImage = playlistImage;
    }

    public List<Record> getSongs() {
        return songs;
    }

    public void setSongs(List<Record> songs) {
        this.songs = songs;
    }

    public Record getSong() {
        return song;
    }

    public void setSong(Record song) {
        this.song = song;
    }
}
