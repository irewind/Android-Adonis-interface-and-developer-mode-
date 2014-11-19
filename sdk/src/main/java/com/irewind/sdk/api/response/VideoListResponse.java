package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;
import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.Video;

import java.util.List;

public class VideoListResponse extends BaseResponse {
    @SerializedName("content")
    private List<Video> content;

    @SerializedName("page")
    private PageInfo pageInfo;

    public List<Video> getContent() {
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
        return "VideoListResponse{" +
                "content=" + content +
                ", pageInfo=" + pageInfo +
                '}';
    }
}
