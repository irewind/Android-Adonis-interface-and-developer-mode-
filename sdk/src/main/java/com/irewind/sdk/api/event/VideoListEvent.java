package com.irewind.sdk.api.event;

import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.Video;

import java.util.List;

public class VideoListEvent {
    public List<Video> videos;

    public PageInfo pageInfo;

    public VideoListEvent(List<Video> videos, PageInfo info) {
        this.videos = videos;
        this.pageInfo = pageInfo;
    }
}
