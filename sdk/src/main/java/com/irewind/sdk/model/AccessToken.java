package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class AccessToken extends BaseResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("access_token")
    private String currentToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("scope")
    private String scope;

    @SerializedName("expires_in")
    private long expiresIn;

    private Date lastRefreshDate;

    public String getTokenType() {
        return tokenType;
    }

    public String getCurrentToken() {
        return currentToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public Date getLastRefreshDate() {
        return lastRefreshDate;
    }

    public void setLastRefreshDate(Date lastRefreshDate) {
        this.lastRefreshDate = lastRefreshDate;
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
                ", expiresIn='" + expiresIn + '\'' +
                ", scope='" + scope + '\'' +
                ", lastRefreshDate='" + lastRefreshDate + '\'' +
                '}';
    }

    public AccessToken(String tokenType, String currentToken, String refreshToken, String scope, long expiresIn, Date lastRefreshDate) {
        this.tokenType = tokenType;
        this.currentToken = currentToken;
        this.refreshToken = refreshToken;
        this.scope = scope;
        this.expiresIn = expiresIn;
        this.lastRefreshDate = lastRefreshDate;
    }

    public static AccessToken createEmptyToken() {
        return new AccessToken("", null, null, null, Long.MAX_VALUE, new Date());
    }
}
