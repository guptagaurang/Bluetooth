package com.android.bluetoothmusic.models.music;

public class BestArtistsPlaylist {

    private String id;
    private int images;
    private String title;
    private String notifications;
    private BestArtistsPlaylistClickObserver bestArtistsPlaylistClickObserver;

    public BestArtistsPlaylist(String id, int images, String title, String notifications, BestArtistsPlaylistClickObserver bestArtistsPlaylistClickObserver) {
        this.id = id;
        this.images = images;
        this.title = title;
        this.notifications = notifications;
        this.bestArtistsPlaylistClickObserver = bestArtistsPlaylistClickObserver;
    }

    public String getId() {
        return id;
    }

    public int getImages() {
        return images;
    }

    public String getTitle() {
        return title;
    }

    public String getNotifications() {
        return notifications;
    }

    public BestArtistsPlaylistClickObserver getBestArtistsPlaylistClickObserver() {
        return bestArtistsPlaylistClickObserver;
    }
}
