package com.irewind.sdk.api.event;

import com.irewind.sdk.model.User;
import com.irewind.sdk.model.VideoPermission;

import java.util.List;

public class VideoPermissionListEvent {
    public VideoPermission videoPermission;
    public List<User> users;

    public VideoPermissionListEvent(VideoPermission videoPermission, List<User> users) {
        this.videoPermission = videoPermission;
        this.users = users;
    }
}
