package com.irewind.sdk.api.event;

import retrofit.RetrofitError;

public class RestErrorEvent {
    public final RetrofitError error;

    public RestErrorEvent(RetrofitError error) {
        this.error = error;
    }
}
