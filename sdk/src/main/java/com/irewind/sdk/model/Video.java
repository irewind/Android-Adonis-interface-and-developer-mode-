package com.irewind.sdk.model;

import com.google.gson.annotations.SerializedName;
import com.irewind.sdk.api.response.BaseResponse;

public class Video extends BaseResponse {

    @SerializedName("videoId")
    private long id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("createdDate")
    private long createdDate;

    @SerializedName("views")
    private Integer views;

    @SerializedName("likes")
    private Integer likes;

    @SerializedName("dislikes")
    private Integer dislikes;

    @SerializedName("oggHighResolutionURL")
    private String oggHighResolutionURL;

    @SerializedName("oggLowResolutionURL")
    private String oggLowResolutionURL;

    @SerializedName("mp4HighResolutionURL")
    private String mp4HighResolutionURL;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("user")
    private User user;

    @SerializedName("authorName")
    private String authorName;

    @SerializedName("publicToken")
    private String publicToken;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public Integer getViews() {
        return views;
    }

    public Integer getLikes() {
        return likes;
    }

    public Integer getDislikes() {
        return dislikes;
    }

    public String getOggHighResolutionURL() {
        return oggHighResolutionURL;
    }

    public String getOggLowResolutionURL() {
        return oggLowResolutionURL;
    }

    public String getMp4HighResolutionURL() {
        return mp4HighResolutionURL;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public User getUser() {
        return user;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getPublicToken() {
        return publicToken;
    }

    @Override
    public String toString() {
        if (super.getError() != null) {
            return "Video{" +
                    "error='" + super.getError() + '\'' +
                    ", errorDescription='" + super.getErrorDescription() + '\'' +
                    '}';
        }
        return "Video{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", createdDate=" + createdDate +
                ", views=" + views +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", oggHighResolutionURL='" + oggHighResolutionURL + '\'' +
                ", oggLowResolutionURL='" + oggLowResolutionURL + '\'' +
                ", mp4HighResolutionURL='" + mp4HighResolutionURL + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", user=" + user +
                ", authorName='" + authorName + '\'' +
                ", publicToken='" + publicToken + '\'' +
                '}';
    }
}
