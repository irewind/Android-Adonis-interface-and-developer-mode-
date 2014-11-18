package com.irewind.sdk.api.event;

public class SocialLoginFailedEvent {
    public enum Reason {
        Unknown
    }

    public Reason reason;

    public SocialLoginFailedEvent(Reason reason) {
        this.reason = reason;
    }
}
