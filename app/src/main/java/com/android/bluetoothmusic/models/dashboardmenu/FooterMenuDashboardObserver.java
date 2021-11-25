package com.android.bluetoothmusic.models.dashboardmenu;

import java.util.ArrayList;
import java.util.List;

public class FooterMenuDashboardObserver extends DashboardMenuObserver {

    private List<FooterMenuObserver> footerMenuObservers = new ArrayList<>();

    @Override
    public List<FooterMenuObserver> getFooterMenuObservers() {
        return footerMenuObservers;
    }

    @Override
    public void setFooterMenuObservers(List<FooterMenuObserver> footerMenuObservers) {
        this.footerMenuObservers = footerMenuObservers;
    }
}
