package com.android.bluetoothmusic.legacy.observer;

public interface OnMusicObserver<RecentlyPlaylist> {

    void startPlaying(RecentlyPlaylist playlist);

    void stopPlaying(RecentlyPlaylist playlist);
}
