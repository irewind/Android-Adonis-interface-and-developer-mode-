package com.irewind.sdk.api.event;

public class SessionOpenFailedEvent {
    public enum Reason {
        Unknown,
        BadCredentials
    }

    public Reason reason;

    public SessionOpenFailedEvent(Reason reason) {
        this.reason = reason;
    }
}
