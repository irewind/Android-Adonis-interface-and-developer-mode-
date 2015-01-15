package com.irewind.sdk.api.event;

public class VideoViewCountUpdateEvent {
    public long videoId;

    public VideoViewCountUpdateEvent(long videoId) {
        this.videoId = videoId;
    }
}
