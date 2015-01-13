package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;

public class PasswordChangeResponse {
    @SerializedName("result")
    private boolean result;

    public boolean getResult() {
        return result;
    }
}
