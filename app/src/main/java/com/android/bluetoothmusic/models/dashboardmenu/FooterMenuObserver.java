package com.android.bluetoothmusic.models.dashboardmenu;

public class FooterMenuObserver {

    private FooterMenuClickObserver footerMenuClickObserver;
    private String title;
    private String notifications;
    private int images;

    public FooterMenuClickObserver getFooterMenuClickObserver() {
        return footerMenuClickObserver;
    }

    public void setFooterMenuClickObserver(FooterMenuClickObserver footerMenuClickObserver) {
        this.footerMenuClickObserver = footerMenuClickObserver;
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
