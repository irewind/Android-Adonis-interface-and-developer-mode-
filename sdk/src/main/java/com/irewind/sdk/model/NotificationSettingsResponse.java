package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationSettingsResponse extends BaseResponse {

    @SerializedName("_embedded")
    private EmbeddedResponse content;

    public EmbeddedResponse getContent() {
        return content;
    }

    public class EmbeddedResponse {

        @SerializedName("user-notification")
        private List<NotificationSettings> notificationSettings;

        public List<NotificationSettings> getNotificationSettings() {
            return notificationSettings;
        }

        @Override
        public String toString() {
            return "NotificationSettingsResponse.EmbeddedResponse{" +
                    "notificationSettings=" + notificationSettings +
                    '}';
        }
    }
}
