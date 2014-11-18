package com.irewind.sdk.api.event;

import com.irewind.sdk.model.User;

import java.util.List;

public class UserListEvent {
    public List<User> users;

    public UserListEvent(List<User> users) {
        this.users = users;
    }
}
