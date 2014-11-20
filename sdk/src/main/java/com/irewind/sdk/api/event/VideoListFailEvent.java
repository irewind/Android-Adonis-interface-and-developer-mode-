package com.irewind.sdk.api.event;

import retrofit.RetrofitError;

public class VideoListFailEvent {
    public RetrofitError error;
    public int page;

    public VideoListFailEvent(RetrofitError error, int page) {
        this.page = page;
    }
}
