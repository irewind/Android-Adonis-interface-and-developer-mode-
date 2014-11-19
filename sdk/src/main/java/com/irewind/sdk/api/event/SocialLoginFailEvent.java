package com.irewind.sdk.api.event;

public class SocialLoginFailEvent {
    public enum Reason {
        Unknown
    }

    public Reason reason;

    public SocialLoginFailEvent(Reason reason) {
        this.reason = reason;
    }
}
