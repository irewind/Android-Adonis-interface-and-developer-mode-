package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;
import com.irewind.sdk.model.Video;

import java.util.List;

public class VideoSearchResponse extends BaseResponse {
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
            return "VideoSearchResponse{" +
                    "error='" + super.getError() + '\'' +
                    ", errorDescription='" + super.getErrorDescription() + '\'' +
                    '}';
        }
        return "VideoSearchResponse{" +
                "content=" + content +
                ", total=" + total +
                '}';
    }
}
