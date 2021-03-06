package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;

public class Tag {

    @SerializedName("tagId")
    private long tagId;

    @SerializedName("tagName")
    private String tagName;

    public long getTagId() {
        return tagId;
    }

    public String getTagName() {
        return tagName;
    }

    @Override
    public String toString() {

        return "Tag{" +
                "tagId=" + tagId +
                ", tagName='" + tagName + '\'' +
                '}';
    }
}
