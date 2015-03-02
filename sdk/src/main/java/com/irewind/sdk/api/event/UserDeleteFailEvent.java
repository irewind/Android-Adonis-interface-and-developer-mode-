package com.irewind.sdk.api.event;

public class UserDeleteFailEvent {
    public enum Reason {
        Unknown
    }

    public Reason reason;

    public UserDeleteFailEvent(Reason reason) {
        this.reason = reason;
    }
}
