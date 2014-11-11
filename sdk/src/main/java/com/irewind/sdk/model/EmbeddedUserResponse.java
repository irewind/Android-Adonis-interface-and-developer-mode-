package com.irewind.sdk.model;

public class EmbeddedUserResponse {
    private User user;

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "EmbeddedUserResponse{" +
                "user=" + user +
                '}';
    }
}
