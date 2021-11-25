package com.android.bluetoothmusic.models.music;

import java.util.ArrayList;
import java.util.List;

public class RecentlyPlaylistObserver extends MusicPlaylistObserver {

    private List<RecentlyPlaylist> recentlyPlaylists = new ArrayList<>();

    @Override
    public List<RecentlyPlaylist> getRecentlyPlaylists() {
        return recentlyPlaylists;
    }

    @Override
    public void setRecentlyPlaylists(List<RecentlyPlaylist> recentlyPlaylists) {
        this.recentlyPlaylists = recentlyPlaylists;
    }
}
