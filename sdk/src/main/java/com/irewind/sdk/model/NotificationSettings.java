package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;

public class NotificationSettings {

    @SerializedName("id")
    private long id;

    @SerializedName("shareNotification")
    private boolean shareNotification;

    @SerializedName("commentNotification")
    private boolean commentNotification;

    @SerializedName("likeNotification")
    private boolean likeNotification;

    @SerializedName("msgNotification")
    private boolean msgNotification;

    public long getId() {
        return id;
    }

    public boolean shareNotificationEnabled() {
        return shareNotification;
    }

    public boolean commentNotificationEnabled() {
        return commentNotification;
    }

    public boolean likeNotificationEnabled() {
        return likeNotification;
    }

    public boolean msgNotificationEnabled() {
        return msgNotification;
    }

    @Override
    public String toString() {
        return "NotificationSettings{" +
                "id=" + id +
                ", shareNotification=" + shareNotification +
                ", commentNotification=" + commentNotification +
                ", likeNotification=" + likeNotification +
                ", msgNotification=" + msgNotification +
                '}';
    }
}
