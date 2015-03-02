package com.irewind.sdk.api.event;

import com.irewind.sdk.model.User;

public class UserResponseEvent {
    public final User user;

    public UserResponseEvent(User user) {
        this.user = user;
    }
}
