package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;

public class UserResponse extends BaseResponse {

    @SerializedName("_embedded")
    private EmbeddedUserResponse embedded;

    @Override
    public String toString() {
        if (super.getError() != null) {
            return "UserResponse{" +
                    "error='" + super.getError() + '\'' +
                    ", errorDescription='" + super.getErrorDescription() + '\'' +
                    '}';
        }

        return "UserResponse{" +
                "embedded=" + embedded +
                '}';
    }
}
