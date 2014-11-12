package com.irewind.sdk;

import com.irewind.sdk.model.AccessToken;

public class SharedPreferencesAccessTokenProvider implements AccessTokenProvider {
    private AccessToken accessToken;

    @Override
    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }
}
