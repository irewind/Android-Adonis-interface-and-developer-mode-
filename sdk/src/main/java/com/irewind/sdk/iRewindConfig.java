package com.irewind.sdk;

public class iRewindConfig {
    private String baseURL = "";
    private String clientID = "";
    private String clientSecret = "";

    public iRewindConfig(String baseURL, String clientID, String clientSecret) {
        this.baseURL = baseURL;
        this.clientID = clientID;
        this.clientSecret = clientSecret;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
