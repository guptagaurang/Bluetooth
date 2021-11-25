package com.android.bluetoothmusic.models.music;

import java.util.ArrayList;
import java.util.List;

public class UserPlaylistObserver extends MusicPlaylistObserver {

    private List<UserPlaylist> userPlaylists = new ArrayList<>();

    @Override
    public List<UserPlaylist> getUserPlaylists() {
        return userPlaylists;
    }

    @Override
    public void setUserPlaylists(List<UserPlaylist> userPlaylists) {
        this.userPlaylists = userPlaylists;
    }
}
