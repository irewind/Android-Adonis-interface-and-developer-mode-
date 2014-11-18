package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("error")
    private boolean error;

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return error;
    }
}
