package com.irewind.sdk.api.response;

import com.google.gson.annotations.SerializedName;
import com.irewind.sdk.model.Comment;

import java.util.List;

public class CommentListResponse extends BaseResponse {
    @SerializedName("content")
    private List<Comment> content;

    @SerializedName("total")
    private int total;

    public List<Comment> getContent() {
        return content;
    }

    public int getTotal() {
        return total;
    }

    @Override
    public String toString() {
        if (super.getError() != null) {
            return "CommentListResponse{" +
                    "error='" + super.getError() + '\'' +
                    ", errorDescription='" + super.getErrorDescription() + '\'' +
                    '}';
        }
        return "CommentListResponse{" +
                "content=" + content +
                ", total=" + total +
                '}';
    }
}
