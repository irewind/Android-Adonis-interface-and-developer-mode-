package com.irewind.sdk.api.event;

import com.irewind.sdk.model.AccessToken;

public class AdminAccessTokenSuccessEvent {
    public AccessToken accessToken;

    public AdminAccessTokenSuccessEvent(AccessToken accessToken) {
        this.accessToken = accessToken;
    }
}
