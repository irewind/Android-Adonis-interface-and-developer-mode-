package com.irewind.sdk.api.event;

import retrofit.RetrofitError;

public class NotificationSettingsListFailedEvent {
    public RetrofitError error;

    public NotificationSettingsListFailedEvent(RetrofitError error) {
        this.error = error;
    }
}
