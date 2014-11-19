package com.irewind.sdk.api.event;

import retrofit.RetrofitError;

public class UserListFailEvent {
    public RetrofitError error;
    public int page;

    public UserListFailEvent(RetrofitError error, int page) {
        this.error = error;
        this.page = page;
    }
}
