package com.irewind.sdk.api;

import com.irewind.sdk.api.response.CommentListResponse;
import com.irewind.sdk.api.response.NotificationSettingsResponse;
import com.irewind.sdk.api.response.TagListResponse;
import com.irewind.sdk.api.response.UserListResponse;
import com.irewind.sdk.api.response.UserResponse;
import com.irewind.sdk.api.response.VideoListResponse;
import com.irewind.sdk.api.response.VideoResponse;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Query;

public interface ApiService {

    // --- USER --- //

    @GET("/rest/user/search/getAllActiveUsersOrderedByLastLoginDate")
    void users(@Header("Authorization") String authorization,
               @Query("page") Integer page,
               @Query("size") Integer size,
               Callback<UserListResponse> cb);

    @GET("/rest/user/")
    void userById(@Header("Authorization") String authorization,
                  @Query("id") long id,
                  Callback<UserResponse> cb);

    @GET("/rest/user/search/findByEmail")
    void userByEmail(@Header("Authorization") String authorization,
                     @Query("email") String email,
                     Callback<UserResponse> cb);

    @GET("/user/changePassword")
    void changePassword(@Header("Authorization") String authorization,
                        @Query("id") long id,
                        @Query("oldPassword") String oldPassword,
                        @Query("newPassword") String newPassword,
                        @Query("newPassword2") String newPassword2,
                        Callback<Boolean> cb);

    @POST("/user/mobileUnregister")
    @FormUrlEncoded
    void deleteAccount(@Header("Authorization") String authorization,
                       @Field("id") long id,
                       Callback<Boolean> cb);

    @POST("/user/save-info")
    @FormUrlEncoded
    void updateUser(@Header("Authorization") String authorization,
                    @Field("id") long userID,
                    @Field("firstName") String firstName,
                    @Field("lastName") String lastName,
                    Callback<Boolean> cb);

    // --- Videos --- //

    @GET("/rest/video/")
    void videoInfo(@Header("Authorization") String authorization,
                   @Query("id") long videoID,
                   Callback<VideoResponse> cb);

    @GET("/rest/video/search/findVideosWithPagination")
    void getVideos(@Header("Authorization") String authorization,
                   @Query("pageNo") Integer page,
                   @Query("pageSize") Integer size,
                   Callback<VideoListResponse> cb);

    @GET("/rest/video/search/findVideosWithPagination")
    void videosForUser(@Header("Authorization") String authorization,
                       @Query("user") long userID,
                       @Query("pageNo") Integer page,
                       @Query("pageSize") Integer size,
                       Callback<VideoListResponse> cb);

    @GET("/rest/video/search/findVideosWithPagination")
    void relatedVideos(@Header("Authorization") String authorization,
                       @Query("videoId") long videoID,
                       @Query("pageNo") Integer page,
                       @Query("pageSize") Integer size,
                       Callback<VideoListResponse> cb);

    @GET("/rest/v2/search-videos")
    void searchVideos(@Header("Authorization") String authorization,
                      @Query("pageNo") Integer page,
                      @Query("pageSize") Integer size,
                      Callback<VideoListResponse> cb);

    // --- Tags --- //

    @GET("/rest/video-tag/search/findTagsByVideo")
    void tagsForVideo(@Header("Authorization") String authorization,
                      @Query("video") long videoID,
                      @Query("pageNo") Integer page,
                      @Query("pageSize") Integer size,
                      Callback<TagListResponse> cb);

    // --- Permissions --- //

    @GET("/rest/v2/get-video-access/?permission=VIEW")
    void videoPermissionView(@Header("Authorization") String authorization,
                             @Query("videoId") long videoID,
                             Callback cb);

    // --- Notifications --- //

    @GET("/rest/user-notification/search/findByUser")
    void userNotificationSettings(@Header("Authorization") String authorization,
                                  @Query("user") long userID,
                                  Callback<NotificationSettingsResponse> cb);

    @POST("/user/updateUserNotification?notificationType=comment")
    @FormUrlEncoded
    void toggleCommentNotifications(@Header("Authorization") String authorization,
                                    @Field("status") boolean status,
                                    Callback<Boolean> cb);

    @POST("/user/updateUserNotification?notificationType=share")
    @FormUrlEncoded
    void toggleShareNotifications(@Header("Authorization") String authorization,
                                  @Field("status") boolean status,
                                  Callback<Boolean> cb);

    @POST("/user/updateUserNotification?notificationType=like")
    @FormUrlEncoded
    void toggleLikeNotifications(@Header("Authorization") String authorization,
                                 @Field("status") boolean status,
                                 Callback<Boolean> cb);

    @POST("/user/updateUserNotification?notificationType=msg")
    @FormUrlEncoded
    void toggleMessageNotifications(@Header("Authorization") String authorization,
                                    @Field("status") boolean status,
                                    Callback<Boolean> cb);

    // --- Comments --- //

    @GET("/rest/v2/video-comment/list")
    void videoComments(@Header("Authorization") String authorization,
                       @Query("videoId") long videoID,
                       @Query("pageNo") Integer page,
                       @Query("pageSize") Integer size,
                       Callback<CommentListResponse> cb);

    @PATCH("/rest/video-comment")
    @FormUrlEncoded
    void postVideoComment(@Header("Authorization") String authorization,
                          @Field("content") String content,
                          @Field("video") String videoURL,
                          Callback<Boolean> cb);

    @PATCH("/rest/video-comment")
    @FormUrlEncoded
    void postVideoComment(@Header("Authorization") String authorization,
                          @Field("content") String content,
                          @Field("video") String videoURL,
                          @Field("parentVideoComment") String parentVideoCommentID,
                          Callback<Boolean> cb);
}
