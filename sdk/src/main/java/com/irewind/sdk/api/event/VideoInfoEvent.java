package com.irewind.sdk.api.event;

import com.irewind.sdk.api.response.VideoResponse;

public class VideoInfoEvent {
    public VideoResponse videoResponse;

    public VideoInfoEvent(VideoResponse videoResponse) {
        this.videoResponse = videoResponse;
    }
}
