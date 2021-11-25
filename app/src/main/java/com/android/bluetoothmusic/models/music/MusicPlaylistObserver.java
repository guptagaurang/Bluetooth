package com.android.bluetoothmusic.models.music;

import java.util.ArrayList;
import java.util.List;

public class MusicPlaylistObserver {

    private List<RecentlyPlaylist> recentlyPlaylists = new ArrayList<>();
    private List<UserPlaylist> userPlaylists = new ArrayList<>();
    private List<BestArtistsPlaylist> bestArtistsPlaylists = new ArrayList<>();

    public List<RecentlyPlaylist> getRecentlyPlaylists() {
        return recentlyPlaylists;
    }

    public void setRecentlyPlaylists(List<RecentlyPlaylist> recentlyPlaylists) {
        this.recentlyPlaylists = recentlyPlaylists;
    }

    public List<UserPlaylist> getUserPlaylists() {
        return userPlaylists;
    }

    public void setUserPlaylists(List<UserPlaylist> userPlaylists) {
        this.userPlaylists = userPlaylists;
    }

    public List<BestArtistsPlaylist> getBestArtistsPlaylists() {
        return bestArtistsPlaylists;
    }

    public void setBestArtistsPlaylists(List<BestArtistsPlaylist> bestArtistsPlaylists) {
        this.bestArtistsPlaylists = bestArtistsPlaylists;
    }
}
