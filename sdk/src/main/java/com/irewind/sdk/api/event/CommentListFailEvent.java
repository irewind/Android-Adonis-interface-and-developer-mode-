package com.irewind.sdk.api.event;

import retrofit.RetrofitError;

public class CommentListFailEvent {
    public RetrofitError error;
    public int page;

    public CommentListFailEvent(RetrofitError error, int page) {
        this.page = page;
    }
}
