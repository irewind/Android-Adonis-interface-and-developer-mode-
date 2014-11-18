package com.irewind.sdk.api.event;

public class UserInfoUpdateFailEvent {
    public enum Reason {
        Unknown
    }

    public Reason reason;

    public UserInfoUpdateFailEvent(Reason reason) {
        this.reason = reason;
    }
}
