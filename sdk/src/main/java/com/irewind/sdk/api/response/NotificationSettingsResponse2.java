package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;
import com.irewind.sdk.model.NotificationSettings;

import java.util.List;

public class NotificationSettingsResponse2 extends BaseResponse {

    @SerializedName("notificationConfig")
    private NotificationSettings notificationSettings;

    public NotificationSettings getNotificationSettings() {
        return notificationSettings;
    }
}
