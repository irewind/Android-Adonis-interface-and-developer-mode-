package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;
import com.irewind.sdk.model.Video;

import java.util.List;

public class VideoListResponse2 extends BaseResponse {
    @SerializedName("content")
    private List<Video> content;

    @SerializedName("total")
    private int total;

    public List<Video> getContent() {
        return content;
    }

    public int getTotal() {
        return total;
    }

    @Override
    public String toString() {
        if (super.getError() != null) {
            return "VideoListResponse2{" +
                    "error='" + super.getError() + '\'' +
                    ", errorDescription='" + super.getErrorDescription() + '\'' +
                    '}';
        }
        return "VideoListResponse2{" +
                "content=" + content +
                ", total=" + total +
                '}';
    }
}
