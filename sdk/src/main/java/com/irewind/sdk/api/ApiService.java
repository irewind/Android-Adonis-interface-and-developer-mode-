package com.irewind.sdk.api;

import com.irewind.sdk.api.request.CreateCommentRequest;
import com.irewind.sdk.api.request.ReplyCommentRequest;
import com.irewind.sdk.api.request.VoteRequest;
import com.irewind.sdk.api.response.BaseResponse;
import com.irewind.sdk.api.response.CommentListResponse;
import com.irewind.sdk.api.response.NotificationSettingsResponse;
import com.irewind.sdk.api.response.TagListResponse;
import com.irewind.sdk.api.response.UserListResponse;
import com.irewind.sdk.api.response.UserResponse;
import com.irewind.sdk.api.response.VideoListResponse;
import com.irewind.sdk.api.response.VideoListResponse2;
import com.irewind.sdk.api.response.VideoPermissionResponse;
import com.irewind.sdk.model.AccessToken;
import com.irewind.sdk.model.Video;
import com.irewind.sdk.model.VideoPermission;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
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

    @GET("/rest/user/search/getAllActiveUsersOrderedByLastLoginDate")
    UserListResponse users(@Header("Authorization") String authorization,
                           @Query("page") Integer page,
                           @Query("size") Integer size);

    @GET("/rest/user/search/searchUsers?sort=firstname")
    UserListResponse searchUsers(@Header("Authorization") String authorization,
                                 @Query("searchTerm") String query,
                                 @Query("page") Integer page,
                                 @Query("size") Integer size);

    @GET("/rest/user/{id}")
    void userById(@Header("Authorization") String authorization,
                  @Path("id") long id,
                  Callback<UserResponse> cb);

    @GET("/rest/user/search/findByEmail")
    void userByEmail(@Header("Authorization") String authorization,
                     @Query("email") String email,
                     Callback<UserResponse> cb);

    @POST("/user/save-info")
    @FormUrlEncoded
    void updateUser(@Header("Authorization") String authorization,
                    @Field("id") long userID,
                    @Field("firstName") String firstName,
                    @Field("lastName") String lastName,
                    Callback<Boolean> cb);

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

    // --- Videos --- //

    @GET("/rest/video/{id}")
    void videoById(@Header("Authorization") String authorization,
                   @Path("id") long videoID,
                   Callback<Video> cb);

    @GET("/rest/video/search/findVideosWithPagination")
    VideoListResponse listVideos(@Header("Authorization") String authorization,
                                 @Query("pageNo") Integer page,
                                 @Query("pageSize") Integer size);

    @GET("/rest/v2/search-videos")
    VideoListResponse2 searchVideos(@Header("Authorization") String authorization,
                                    @Query("searchTerm") String query,
                                    @Query("pageNo") Integer page,
                                    @Query("pageSize") Integer size);

    @GET("/rest/video/search/findVideosWithPagination")
    VideoListResponse videosForUser(@Header("Authorization") String authorization,
                                    @Query("user") long userID,
                                    @Query("pageNo") Integer page,
                                    @Query("pageSize") Integer size);

    @GET("/rest/v2/related-videos")
    VideoListResponse2 relatedVideos(@Header("Authorization") String authorization,
                                     @Query("videoId") long videoID,
                                     @Query("pageNo") Integer page,
                                     @Query("pageSize") Integer size,
                                     @Query("tagList") String tagList);

    // --- Tags --- //

    @GET("/rest/video-tag/search/findTagsByVideo")
    void tagsForVideo(@Header("Authorization") String authorization,
                      @Query("video") long videoID,
                      @Query("pageNo") Integer page,
                      @Query("pageSize") Integer size,
                      Callback<TagListResponse> cb);

    // --- Permissions --- //

    @GET("/rest/v2/get-video-access/")
    void videoPermission(@Header("Authorization") String authorization,
                             @Query("videoId") long videoID,
                             @Query("permission") String accessType,
                             Callback<VideoPermissionResponse> cb);

    // --- Votes --- //

    @POST("/rest/user-video-vote")
    @Headers("Content-Type: application/json")
    void vote(@Header("Authorization") String authorization,
              @Body VoteRequest voteRequest,
              Callback<BaseResponse> cb);

    // --- Comments --- //

    @GET("/rest/v2/video-comment/list")
    CommentListResponse videoComments(@Header("Authorization") String authorization,
                                      @Query("videoId") long videoID,
                                      @Query("pageNo") Integer page,
                                      @Query("pageSize") Integer size);

    @POST("/rest/video-comment")
    @Headers("Content-Type: application/json")
    void postVideoComment(@Header("Authorization") String authorization,
                          @Body CreateCommentRequest commentRequest,
                          Callback<BaseResponse> cb);

    @POST("/rest/video-comment")
    @Headers("Content-Type: application/json")
    void replyVideoComment(@Header("Authorization") String authorization,
                           @Body ReplyCommentRequest commentRequest,
                           Callback<BaseResponse> cb);
}
