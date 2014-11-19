package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;
import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.User;

import java.util.List;

public class UserListResponse extends BaseResponse {

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

        @SerializedName("user")
        private List<User> users;

        public List<User> getUsers() {
            return users;
        }

        @Override
        public String toString() {
            return "UserListResponse.EmbeddedResponse{" +
                    "users=" + users +
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
        return "UserListResponse{" +
                "embeddedResponse=" + embeddedResponse +
                ", pageInfo=" + pageInfo +
                '}';
    }
}
