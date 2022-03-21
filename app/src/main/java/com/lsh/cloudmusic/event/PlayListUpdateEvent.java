package com.lsh.cloudmusic.event;

public class PlayListUpdateEvent {
    public long updatePlayListId;

    public PlayListUpdateEvent(long playListId) {
        this.updatePlayListId = playListId;
    }
}
