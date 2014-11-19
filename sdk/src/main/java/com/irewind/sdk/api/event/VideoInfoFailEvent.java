package com.irewind.sdk.api.event;

import retrofit.RetrofitError;

public class VideoInfoFailEvent {
    public RetrofitError error;

    public VideoInfoFailEvent(RetrofitError error) {
        this.error = error;
    }
}
