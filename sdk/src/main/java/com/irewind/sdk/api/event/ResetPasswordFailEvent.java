package com.irewind.sdk.api.event;

public class ResetPasswordFailEvent {
    public enum Reason {
        Unknown,
        NoUser
    }

    public Reason reason;

    public ResetPasswordFailEvent(Reason reason) {
        this.reason = reason;
    }
}
