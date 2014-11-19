package com.irewind.sdk.api.event;

import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.User;

import java.util.List;

public class UserListEvent {
    public List<User> users;

    public PageInfo pageInfo;

    public UserListEvent(List<User> users, PageInfo info) {
        this.users = users;
        this.pageInfo = pageInfo;
    }
}
