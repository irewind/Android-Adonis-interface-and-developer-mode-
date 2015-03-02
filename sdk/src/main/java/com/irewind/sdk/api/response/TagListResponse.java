package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;
import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.Tag;

import java.util.List;

public class TagListResponse extends BaseResponse {


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

        @SerializedName("tag")
        private List<Tag> tags;

        public List<Tag> getTags() {
            return tags;
        }

        @Override
        public String toString() {
            return "EmbeddedResponse{" +
                    "tags=" + tags +
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
        return "TagListResponse{" +
                "embeddedResponse=" + embeddedResponse +
                ", pageInfo=" + pageInfo +
                '}';
    }
}
