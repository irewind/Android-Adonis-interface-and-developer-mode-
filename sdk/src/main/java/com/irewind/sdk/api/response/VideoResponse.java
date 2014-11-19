package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;

public class VideoResponse extends BaseResponse {
    @SerializedName("accessType")
    private String accessType;
    private String permission;

    public String getAccessType() {
        return accessType;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        if (super.getError() != null) {
            return "AccessToken{" +
                    "error='" + super.getError() + '\'' +
                    ", errorDescription='" + super.getErrorDescription() + '\'' +
                    '}';
        }
        return "VideoResponse{" +
                "accessType='" + accessType + '\'' +
                ", permission='" + permission + '\'' +
                '}';
    }
}
