package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;
import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.Tag;

import java.util.List;

public class TagListResponse extends BaseResponse {
    @SerializedName("content")
    private List<Tag> content;

    @SerializedName("page")
    private PageInfo pageInfo;

    public List<Tag> getContent() {
        return content;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    @Override
    public String toString() {
        if (super.getError() != null) {
            return "VideoListResponse{" +
                    "error='" + super.getError() + '\'' +
                    ", errorDescription='" + super.getErrorDescription() + '\'' +
                    '}';
        }
        return "TagListResponse{" +
                "content=" + content +
                ", pageInfo=" + pageInfo +
                '}';
    }
}
