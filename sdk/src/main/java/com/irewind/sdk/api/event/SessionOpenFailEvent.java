package com.irewind.sdk.api.event;

public class SessionOpenFailEvent {
    public enum Reason {
        Unknown,
        BadCredentials
    }

    public Reason reason;

    public String message;

    public SessionOpenFailEvent(Reason reason, String message) {
        this.reason = reason;
        this.message = message;
    }
}
