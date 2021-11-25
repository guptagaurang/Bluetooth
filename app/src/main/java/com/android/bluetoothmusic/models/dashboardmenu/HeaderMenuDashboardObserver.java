package com.android.bluetoothmusic.models.dashboardmenu;

import java.util.ArrayList;
import java.util.List;

public class HeaderMenuDashboardObserver extends DashboardMenuObserver {

    private List<HeaderMenuObserver> headerMenuObservers = new ArrayList<>();

    @Override
    public List<HeaderMenuObserver> getHeaderMenuObservers() {
        return headerMenuObservers;
    }

    @Override
    public void setHeaderMenuObservers(List<HeaderMenuObserver> headerMenuObservers) {
        this.headerMenuObservers = headerMenuObservers;
    }
}
