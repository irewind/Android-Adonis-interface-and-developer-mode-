package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;
import com.irewind.sdk.model.User;
import com.irewind.sdk.model.Video;
import com.irewind.sdk.model.VideoPermission;

import java.util.List;

public class VideoPermissionResponse extends BaseResponse {
    @SerializedName("videoPermission")
    private VideoPermission videoPermission;

    @SerializedName("video")
    private Video video;

    @SerializedName("users")
    private List<User> users;

    public VideoPermission getVideoPermission() {
        return videoPermission;
    }

    public Video getVideo() {
        return video;
    }

    public List<User> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        if (super.getError() != null) {
            return "VideoPermissionResponse{" +
                    "error='" + super.getError() + '\'' +
                    ", errorDescription='" + super.getErrorDescription() + '\'' +
                    '}';
        }
        return "VideoPermissionResponse{" +
                "videoPermission=" + videoPermission +
                ", video=" + video +
                ", users=" + users +
                '}';
    }
}
