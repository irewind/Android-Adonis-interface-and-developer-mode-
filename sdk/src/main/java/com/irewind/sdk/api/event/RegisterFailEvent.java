package com.irewind.sdk.api.event;

public class RegisterFailEvent {
    public enum Reason {
        Unknown,
        UserExists
    }

    public Reason reason;

    public RegisterFailEvent(Reason reason) {
        this.reason = reason;
    }
}
