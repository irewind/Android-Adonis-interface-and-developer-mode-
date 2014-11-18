package com.irewind.sdk.api;

import com.irewind.sdk.model.BaseResponse;
import com.irewind.sdk.model.Tag;
import com.irewind.sdk.model.UserResponse;
import com.irewind.sdk.model.Video;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Query;

public interface ApiService {

    // --- USER --- //

    @GET("/rest/user/")
    void userById(@Header("Authorization") String authorization,
                  @Query("id") String id,
                  Callback<UserResponse> cb);

    @GET("/rest/user/search/findByEmail")
    void userByEmail(@Header("Authorization") String authorization,
                     @Query("email") String email,
                     Callback<UserResponse> cb);

    @GET("/rest/user/search/getAllActiveUsersOrderedByLastLoginDate")
    void users(@Header("Authorization") String authorization,
               @Query("page") Integer page,
               @Query("size") Integer size,
               Callback<UserResponse> cb);

    @GET("/user/changePassword")
    void changePassword(@Header("Authorization") String authorization,
                        @Query("oldPassword") String oldPassword,
                        @Query("newPassword") String newPassword,
                        @Query("newPassword2") String newPassword2,
                        Callback<UserResponse> cb);

    @DELETE("/user/mobileUnregister")
    @FormUrlEncoded
    void deleteAccount(@Header("Authorization") String authorization,
                       @Field("id") String id,
                       Callback<UserResponse> cb);

    @PATCH("/user/save-info")
    @FormUrlEncoded
    void updateUser(@Header("Authorization") String authorization,
                    @Field("id") long userID,
                    @Field("firstName") String firstName,
                    @Field("lastName") String lastName,
                    @Field("email") String email,
                    @Field("password") String password,
                    Callback<UserResponse> cb);

    @PATCH("/user/save-info")
    @FormUrlEncoded
    void updateUser(@Header("Authorization") String authorization,
                    @Field("id") long userID,
                    @Field("firstName") String firstName,
                    @Field("lastName") String lastName,
                    @Field("email") String email,
                    Callback<UserResponse> cb);

    // --- Videos --- //

    @GET("/rest/video/search/findVideosWithPagination")
    void videosForUser(@Header("Authorization") String authorization,
                       @Query("user") long userID,
                       @Query("pageNo") Integer page,
                       @Query("pageSize") Integer size,
                       Callback<Video> cb);

    @GET("/rest/video/search/findVideosWithPagination")
    void relatedVideos(@Header("Authorization") String authorization,
                       @Query("videoId") long videoID,
                       @Query("pageNo") Integer page,
                       @Query("pageSize") Integer size,
                       Callback<Video> cb);

    @GET("/rest/v2/search-videos")
    void searchVideos(@Header("Authorization") String authorization,
                      @Query("pageNo") Integer page,
                      @Query("pageSize") Integer size,
                      Callback<Video> cb);

    // --- Tags --- //

    @GET("/rest/video-tag/search/findTagsByVideo")
    void tagsForVideo(@Header("Authorization") String authorization,
                      @Query("video") long videoID,
                      @Query("pageNo") Integer page,
                      @Query("pageSize") Integer size,
                      Callback<Tag> cb);

    // --- Permissions --- // TODO

    @GET("/rest/v2/get-video-access/?permission=VIEW")
    void videoPermissionView(@Header("Authorization") String authorization,
                             @Query("videoId") long videoID,
                             Callback cb);


    // --- Notifications --- //

    public final static String NOTIFICATION_TYPE_COMMENT = "comment";
    public final static String NOTIFICATION_TYPE_LIKE = "like";

    @GET("/user/updateUserNotification")
    void updateUserNotification(@Header("Authorization") String authorization,
                                @Query("notificationType") String notificationType,
                                @Query("status") boolean status,
                                Callback cb);

    // --- Comments --- //

    @GET("/rest/v2/video-comment/list")
    void videoComments(@Header("Authorization") String authorization,
                       @Query("videoId") long videoID,
                       @Query("pageNo") Integer page,
                       @Query("pageSize") Integer size);

    @PATCH("/rest/video-comment")
    @FormUrlEncoded
    void postVideoComment(@Header("Authorization") String authorization,
                          @Field("content") String content,
                          @Field("video") String videoURL);

    @PATCH("/rest/video-comment")
    @FormUrlEncoded
    void postVideoComment(@Header("Authorization") String authorization,
                          @Field("content") String content,
                          @Field("video") String videoURL,
                          @Field("parentVideoComment") String parentVideoCommentID);
}
