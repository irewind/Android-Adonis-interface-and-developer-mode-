package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;
import com.irewind.sdk.model.Video;

import java.util.List;

public class VideoSearchResponse extends BaseResponse {
    @SerializedName("content")
    private List<Video> content;

    @SerializedName("more")
    private int more;

    public List<Video> getContent() {
        return content;
    }

    public int getMore() {
        return more;
    }

    @Override
    public String toString() {
        if (super.getError() != null) {
            return "VideoListResponse{" +
                    "error='" + super.getError() + '\'' +
                    ", errorDescription='" + super.getErrorDescription() + '\'' +
                    '}';
        }
        return "VideoSearchResponse{" +
                "content=" + content +
                ", more=" + more +
                '}';
    }
}
