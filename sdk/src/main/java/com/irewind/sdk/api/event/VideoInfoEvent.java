package com.irewind.sdk.api.event;

import com.irewind.sdk.model.Video;

public class VideoInfoEvent {
    public Video video;

    public VideoInfoEvent(Video video) {
        this.video = video;
    }
}
