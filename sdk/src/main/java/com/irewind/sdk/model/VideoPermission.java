package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;

public class VideoPermission {
    public final static String PERMISSION_TYPE_VIEW = "VIEW";
    public final static String ACCESS_TYPE_PUBLIC = "PUBLIC";
    public final static String ACCESS_TYPE_PRIVATE = "PRIVATE";
    public final static String ACCESS_TYPE_CUSTOM = "CUSTOM";

    @SerializedName("id")
    private long id;

    @SerializedName("accessType")
    private String accessType;

    @SerializedName("permission")
    private String permission;

    public long getId() {
        return id;
    }

    public String getAccessType() {
        return accessType;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return "VideoPermission{" +
                "id=" + id +
                ", accessType='" + accessType + '\'' +
                ", permission='" + permission + '\'' +
                '}';
    }
}
