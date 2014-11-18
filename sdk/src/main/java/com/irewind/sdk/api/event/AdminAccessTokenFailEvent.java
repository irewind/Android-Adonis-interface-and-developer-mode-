package com.irewind.sdk.api.event;

public class AdminAccessTokenFailEvent {
    public enum Reason {
        Unknown,
        BadCredentials
    }

    public Reason reason;

    public AdminAccessTokenFailEvent(Reason reason) {
        this.reason = reason;
    }
}
