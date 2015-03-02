package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Comment {
    @SerializedName("videoCommentId")
    private long id;

    @SerializedName("content")
    private String content;

    @SerializedName("createdDate")
    private long createdDate;

    @SerializedName("likes")
    private Integer likes;

    @SerializedName("dislikes")
    private Integer dislikes;

    @SerializedName("user")
    private User user;

    @SerializedName("children")
    private List<Comment> children;

    private boolean isChildComment;
    private long parentCommentId;

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public Integer getLikes() {
        return likes;
    }

    public Integer getDislikes() {
        return dislikes;
    }

    public User getUser() {
        return user;
    }

    public List<Comment> getChildren() {
        return children;
    }

    public boolean isChildComment() {
        return isChildComment;
    }

    public void setIsChildComment(boolean isChildComment) {
        this.isChildComment = isChildComment;
    }

    public long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createdDate=" + createdDate +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", user=" + user +
                ", children=" + children +
                ", isChildComment" + isChildComment +
                ", parentCommentId=" + parentCommentId +
                '}';
    }
}
