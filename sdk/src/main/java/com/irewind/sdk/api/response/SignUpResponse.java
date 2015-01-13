package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;

public class SignUpResponse {
    @SerializedName("success")
    private String success;

    @SerializedName("errors")
    private String error;

    public String getSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }
}
