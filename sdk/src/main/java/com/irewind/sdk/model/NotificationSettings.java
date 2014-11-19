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

    public boolean isShareNotification() {
        return shareNotification;
    }

    public boolean isCommentNotification() {
        return commentNotification;
    }

    public boolean isLikeNotification() {
        return likeNotification;
    }

    public boolean isMsgNotification() {
        return msgNotification;
    }
}
