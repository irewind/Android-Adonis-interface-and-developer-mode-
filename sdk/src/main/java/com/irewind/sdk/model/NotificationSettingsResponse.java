package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationSettingsResponse extends BaseResponse {

    @SerializedName("content")
    private List<NotificationSettings> content;

    public List<NotificationSettings> getContent() {
        return content;
    }
}
