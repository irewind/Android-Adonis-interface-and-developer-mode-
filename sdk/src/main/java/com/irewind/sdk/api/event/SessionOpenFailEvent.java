package com.irewind.sdk.api.event;

public class SessionOpenFailEvent {
    public enum Reason {
        Unknown,
        BadCredentials
    }

    public Reason reason;

    public SessionOpenFailEvent(Reason reason) {
        this.reason = reason;
    }
}
