package com.irewind.sdk.api.event;

import com.irewind.sdk.model.User;

public class UserInfoLoadedEvent {
    public User user;
    public UserInfoLoadedEvent(User user) {
        this.user = user;
    }
}
