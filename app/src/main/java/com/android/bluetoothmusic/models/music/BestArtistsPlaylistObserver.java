package com.android.bluetoothmusic.models.music;

import java.util.ArrayList;
import java.util.List;

public class BestArtistsPlaylistObserver extends MusicPlaylistObserver {

    private List<BestArtistsPlaylist> bestArtistsPlaylists = new ArrayList<>();

    @Override
    public List<BestArtistsPlaylist> getBestArtistsPlaylists() {
        return bestArtistsPlaylists;
    }

    @Override
    public void setBestArtistsPlaylists(List<BestArtistsPlaylist> bestArtistsPlaylists) {
        this.bestArtistsPlaylists = bestArtistsPlaylists;
    }
}
