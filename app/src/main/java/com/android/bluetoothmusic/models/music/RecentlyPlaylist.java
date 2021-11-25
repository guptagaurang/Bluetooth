package com.android.bluetoothmusic.models.music;

public class RecentlyPlaylist {

    private int images;
    private String title;
    private String notifications;
    private RecentlyPlaylistClickObserver recentlyPlaylistClickObserver;

    public RecentlyPlaylist(int images, String title, String notifications, RecentlyPlaylistClickObserver recentlyPlaylistClickObserver) {
        this.images = images;
        this.title = title;
        this.notifications = notifications;
        this.recentlyPlaylistClickObserver = recentlyPlaylistClickObserver;
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

    public RecentlyPlaylistClickObserver getRecentlyPlaylistClickObserver() {
        return recentlyPlaylistClickObserver;
    }
}
