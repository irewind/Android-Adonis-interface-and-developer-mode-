package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;
import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.Video;

import java.util.List;

public class VideoListResponse extends BaseResponse {
    @SerializedName("_embedded")
    private EmbeddedResponse embeddedResponse;

    @SerializedName("page")
    private PageInfo pageInfo;

    public EmbeddedResponse getEmbeddedResponse() {
        return embeddedResponse;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public class EmbeddedResponse {

        @SerializedName("video")
        private List<Video> videos;

        public List<Video> getVideos() {
            return videos;
        }

        @Override
        public String toString() {
            return "EmbeddedResponse.EmbeddedResponse{" +
                    "videos=" + videos +
                    '}';
        }
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
                "embeddedResponse=" + embeddedResponse +
                ", pageInfo=" + pageInfo +
                '}';
    }
}
