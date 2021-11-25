package com.android.bluetoothmusic.models.music;

public class UserPlaylist {

    private int images;
    private String title;
    private String notifications;
    private UserPlaylistClickObserver userPlaylistClickObserver;

    public UserPlaylist(int images, String title, String notifications, UserPlaylistClickObserver userPlaylistClickObserver) {
        this.images = images;
        this.title = title;
        this.notifications = notifications;
        this.userPlaylistClickObserver = userPlaylistClickObserver;
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

    public UserPlaylistClickObserver getUserPlaylistClickObserver() {
        return userPlaylistClickObserver;
    }
}
