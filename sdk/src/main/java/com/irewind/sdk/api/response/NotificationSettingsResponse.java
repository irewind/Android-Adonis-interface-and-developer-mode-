package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;
import com.irewind.sdk.model.NotificationSettings;

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
