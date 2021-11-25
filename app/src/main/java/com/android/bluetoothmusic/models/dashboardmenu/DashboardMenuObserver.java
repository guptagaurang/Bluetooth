package com.android.bluetoothmusic.models.dashboardmenu;

import java.util.ArrayList;
import java.util.List;

public class DashboardMenuObserver {

    private List<HeaderMenuObserver> headerMenuObservers = new ArrayList<>();
    private List<ContentMenuObserver> contentMenuObservers = new ArrayList<>();
    private List<FooterMenuObserver> footerMenuObservers = new ArrayList<>();

    public List<HeaderMenuObserver> getHeaderMenuObservers() {
        return headerMenuObservers;
    }

    public void setHeaderMenuObservers(List<HeaderMenuObserver> headerMenuObservers) {
        this.headerMenuObservers = headerMenuObservers;
    }

    public List<ContentMenuObserver> getContentMenuObservers() {
        return contentMenuObservers;
    }

    public void setContentMenuObservers(List<ContentMenuObserver> contentMenuObservers) {
        this.contentMenuObservers = contentMenuObservers;
    }

    public List<FooterMenuObserver> getFooterMenuObservers() {
        return footerMenuObservers;
    }

    public void setFooterMenuObservers(List<FooterMenuObserver> footerMenuObservers) {
        this.footerMenuObservers = footerMenuObservers;
    }
}
