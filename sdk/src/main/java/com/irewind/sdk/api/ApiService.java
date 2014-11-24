package com.irewind.sdk.api;

import com.irewind.sdk.api.response.BaseResponse;
import com.irewind.sdk.api.response.CommentListResponse;
import com.irewind.sdk.api.response.NotificationSettingsResponse;
import com.irewind.sdk.api.response.TagListResponse;
import com.irewind.sdk.api.response.UserListResponse;
import com.irewind.sdk.api.response.UserResponse;
import com.irewind.sdk.api.response.VideoListResponse;
import com.irewind.sdk.api.response.VideoResponse;
import com.irewind.sdk.api.response.VideoSearchResponse;
import com.irewind.sdk.model.AccessToken;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ApiService {

    @POST("/rest/v2/socialMobileLogin")
    @FormUrlEncoded
    void socialLogin(@Header("Authorization") String authorization,
                     @Field("email") String email,
                     @Field("socialId") String socialId,
                     @Field("provider") String socialIdProvider,
                     @Field("firstName") String firstName,
                     @Field("lastName") String lastName,
                     @Field("pictureUrl") String pictureURL,
                     Callback<BaseResponse> cb);

    @POST("/rest/v2/socialMobileLogin?provider=FACEBOOK")
    @FormUrlEncoded
    void socialLoginFacebook(@Header("Authorization") String authorization,
                             @Field("email") String email,
                             @Field("socialId") String socialId,
                             @Field("firstName") String firstName,
                             @Field("lastName") String lastName,
                             @Field("pictureUrl") String pictureURL,
                             Callback<AccessToken> cb);

    @POST("/rest/v2/socialMobileLogin?provider=GOOGLE")
    @FormUrlEncoded
    void socialLoginGoogle(@Header("Authorization") String authorization,
                           @Field("email") String email,
                           @Field("socialId") String socialId,
                           @Field("firstName") String firstName,
                           @Field("lastName") String lastName,
                           @Field("pictureUrl") String pictureURL,
                           Callback<AccessToken> cb);

    // --- USER --- //

    @GET("/rest/user/?sort=firstname")
    UserListResponse users(@Header("Authorization") String authorization,
                           @Query("page") Integer page,
                           @Query("size") Integer size);

    @GET("/rest/user/?sort=firstname")
    void users(@Header("Authorization") String authorization,
               @Query("page") Integer page,
               @Query("size") Integer size,
               Callback<UserListResponse> cb);

    @GET("/rest/user/search/searchUsers?sort=firstname")
    UserListResponse searchUsers(@Header("Authorization") String authorization,
                                 @Query("searchTerm") String query,
                                 @Query("page") Integer page,
                                 @Query("size") Integer size);

    @GET("/rest/user/search/searchUsers?sort=firstname")
    void searchUsers(@Header("Authorization") String authorization,
                     @Query("searchTerm") String query,
                     @Query("page") Integer page,
                     @Query("size") Integer size,
                     Callback<UserListResponse> cb);

    @GET("/rest/user/")
    void userById(@Header("Authorization") String authorization,
                  @Path("idKeyword") long id,
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
    VideoListResponse listVideos(@Header("Authorization") String authorization,
                                 @Query("pageNo") Integer page,
                                 @Query("pageSize") Integer size);

    @GET("/rest/video/search/findVideosWithPagination")
    void listVideos(@Header("Authorization") String authorization,
                    @Query("pageNo") Integer page,
                    @Query("pageSize") Integer size,
                    Callback<VideoListResponse> cb);

    @GET("/rest/v2/search-videos")
    VideoSearchResponse searchVideos(@Header("Authorization") String authorization,
                                     @Query("searchTerm") String query,
                                     @Query("pageNo") Integer page,
                                     @Query("pageSize") Integer size);

    @GET("/rest/v2/search-videos")
    void searchVideos(@Header("Authorization") String authorization,
                      @Query("pageNo") Integer page,
                      @Query("pageSize") Integer size,
                      Callback<VideoSearchResponse> cb);

    @GET("/rest/video/search/findByUser")
    VideoListResponse videosForUser(@Header("Authorization") String authorization,
                                    @Query("user") long userID,
                                    @Query("pageNo") Integer page,
                                    @Query("pageSize") Integer size);

    @GET("/rest/video/search/findByUser")
    void videosForUser(@Header("Authorization") String authorization,
                       @Query("user") long userID,
                       @Query("pageNo") Integer page,
                       @Query("pageSize") Integer size,
                       Callback<VideoListResponse> cb);

    @GET("/rest/video/search/findVideosWithPagination")
    VideoListResponse relatedVideos(@Header("Authorization") String authorization,
                                    @Query("videoId") long videoID,
                                    @Query("pageNo") Integer page,
                                    @Query("pageSize") Integer size);

    @GET("/rest/video/search/findVideosWithPagination")
    void relatedVideos(@Header("Authorization") String authorization,
                       @Query("videoId") long videoID,
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
    CommentListResponse videoComments(@Header("Authorization") String authorization,
                                      @Query("videoId") long videoID,
                                      @Query("pageNo") Integer page,
                                      @Query("pageSize") Integer size);

    @GET("/rest/v2/video-comment/list")
    void videoComments(@Header("Authorization") String authorization,
                       @Query("videoId") long videoID,
                       @Query("pageNo") Integer page,
                       @Query("pageSize") Integer size,
                       Callback<CommentListResponse> cb);

    @POST("/rest/video-comment")
    @FormUrlEncoded
    void postVideoComment(@Header("Authorization") String authorization,
                          @Field("video") String videoURL,
                          @Field("content") String content,
                          Callback<BaseResponse> cb);

    @POST("/rest/video-comment")
    @FormUrlEncoded
    void postVideoComment(@Header("Authorization") String authorization,
                          @Field("video") String videoURL,
                          @Field("content") String content,
                          @Field("parentVideoComment") long parentVideoCommentID,
                          Callback<BaseResponse> cb);
}
