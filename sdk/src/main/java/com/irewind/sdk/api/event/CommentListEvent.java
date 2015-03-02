package com.irewind.sdk.api.event;

import com.irewind.sdk.model.Comment;
import com.irewind.sdk.model.PageInfo;

import java.util.List;

public class CommentListEvent {
    public List<Comment> comments;
    public PageInfo pageInfo;

    public CommentListEvent(List<Comment> comments, PageInfo pageInfo) {
        this.comments = comments;
        this.pageInfo = pageInfo;
    }
}
