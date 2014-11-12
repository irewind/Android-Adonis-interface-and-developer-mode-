package com.irewind.sdk;

import com.irewind.sdk.model.AccessToken;

public interface AccessTokenProvider {
    public AccessToken getAccessToken();
    public void setAccessToken(AccessToken accessToken);
}
