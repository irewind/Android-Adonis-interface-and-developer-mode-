package com.irewind.sdk.api.event;

public class LoginFailedEvent {
    public enum Reason {
        Unknown,
        BadCredentials
    }

    public Reason reason;

    public LoginFailedEvent(Reason reason) {
        this.reason = reason;
    }
}
