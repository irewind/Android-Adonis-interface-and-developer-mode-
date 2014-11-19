package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;
import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.User;

import java.util.List;

public class UserListResponse extends BaseResponse {

    @SerializedName("content")
    private List<User> content;

    @SerializedName("page")
    private PageInfo pageInfo;

    public List<User> getContent() {
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
        return "UserListResponse{" +
                "content=" + content +
                ", pageInfo=" + pageInfo +
                '}';
    }
}
