package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;

public class Tag extends BaseResponse {

    @SerializedName("tagId")
    private Integer tagId;

    @SerializedName("tagName")
    private String tagName;

    public Integer getTagId() {
        return tagId;
    }

    public String getTagName() {
        return tagName;
    }

    @Override
    public String toString() {
        if (super.getError() != null) {
            return "Tag{" +
                    "error='" + super.getError() + '\'' +
                    ", errorDescription='" + super.getErrorDescription() + '\'' +
                    '}';
        }
        return "Tag{" +
                "tagId=" + tagId +
                ", tagName='" + tagName + '\'' +
                '}';
    }
}
