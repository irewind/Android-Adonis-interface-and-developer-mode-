package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;

public class AccessToken extends BaseResponse {
    @SerializedName("access_token")
    private String currentToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("expires_in")
    private Integer expiresIn;

    @SerializedName("scope")
    private String scope;

    public String getCurrentToken() {
        return currentToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        if (super.getError() != null) {
            return "AccessToken{" +
                    "error='" + super.getError() + '\'' +
                    ", errorDescription='" + super.getErrorDescription() + '\'' +
                    '}';
        }

        return "AccessToken{" +
                "currentToken='" + currentToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", scope='" + scope + '\'' +
                '}';
    }
}
