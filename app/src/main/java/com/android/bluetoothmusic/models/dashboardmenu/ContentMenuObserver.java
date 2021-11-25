package com.android.bluetoothmusic.models.dashboardmenu;

public class ContentMenuObserver {

    private ContentMenuClickObserver contentMenuClickObserver;
    private String title;
    private String notifications;
    private int images;

    public ContentMenuClickObserver getContentMenuClickObserver() {
        return contentMenuClickObserver;
    }

    public void setContentMenuClickObserver(ContentMenuClickObserver contentMenuClickObserver) {
        this.contentMenuClickObserver = contentMenuClickObserver;
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

    public int getImages() {
        return images;
    }

    public void setImages(int images) {
        this.images = images;
    }

}
