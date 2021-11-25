package com.android.bluetoothmusic.models.dashboardmenu;

import java.util.ArrayList;
import java.util.List;

public class ContentMenuDashboardObserver extends DashboardMenuObserver {

    private List<ContentMenuObserver> contentMenuObservers = new ArrayList<>();

    @Override
    public List<ContentMenuObserver> getContentMenuObservers() {
        return contentMenuObservers;
    }

    @Override
    public void setContentMenuObservers(List<ContentMenuObserver> contentMenuObservers) {
        this.contentMenuObservers = contentMenuObservers;
    }

}
