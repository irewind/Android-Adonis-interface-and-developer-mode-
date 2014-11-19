package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private Integer status;

    @SerializedName("error")
    private String error;

    @SerializedName("error_description")
    private String errorDescription;

    public String getMessage() {
        return message;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", errorDescription='" + errorDescription + '\'' +
                '}';
    }
}
