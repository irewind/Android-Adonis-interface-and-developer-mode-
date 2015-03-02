package com.irewind.sdk.api.event;

public class PasswordChangeFailEvent {
    public enum Reason {
        Unknown,
        WrongPassword
    }

    public Reason reason;

    public PasswordChangeFailEvent(Reason reason) {
        this.reason = reason;
    }
}
