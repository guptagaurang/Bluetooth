package com.android.bluetoothmusic.models.dashboardmenu;

public class HeaderMenuObserver {

    private HeaderMenuClickObserver headerMenuClickObserver;
    private String title;
    private String notifications;
    private String images;

    public HeaderMenuClickObserver getHeaderMenuClickObserver() {
        return headerMenuClickObserver;
    }

    public void setHeaderMenuClickObserver(HeaderMenuClickObserver headerMenuClickObserver) {
        this.headerMenuClickObserver = headerMenuClickObserver;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotifications() {
        return notifications;
    }

    public void setNotifications(String notifications) {
        this.notifications = notifications;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
