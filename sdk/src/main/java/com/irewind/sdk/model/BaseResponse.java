package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    @SerializedName("status")
    private Integer status;

    @SerializedName("error")
    private String error;

    @SerializedName("error_description")
    private String errorDescription;

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
