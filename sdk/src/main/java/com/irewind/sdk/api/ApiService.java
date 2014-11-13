package com.irewind.sdk.api;

import com.irewind.sdk.model.Tag;
import com.irewind.sdk.model.UserResponse;
import com.irewind.sdk.model.Video;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.PATCH;
import retrofit.http.POST;

public interface ApiService {

    // --- USER --- //

    @POST("/registeriOS")
    void addUser(@Header("Authorization") String authorization,
                 @Field("email") String email,
                 @Field("firstName") String firstName,
                 @Field("lastName") String lastName,
                 @Field("password") String password,
                 Callback<UserResponse> cb);

    @POST("/rest/v2/socialMobileLogin")
    void socialLogin(@Header("Authorization") String authorization,
                     @Field("email") String email,
                     @Field("socialId") String socialId,
                     @Field("provider") String socialIdProvider,
                     @Field("firstName") String firstName,
                     @Field("lastName") String lastName,
                     @Field("pictureUrl") String pictureURL,
                     Callback<UserResponse> cb);

    @POST("/rest/v2/socialMobileLogin?provider=FACEBOOK")
    void socialLoginFacebook(@Header("Authorization") String authorization,
                             @Field("email") String email,
                             @Field("socialId") String socialId,
                             @Field("firstName") String firstName,
                             @Field("lastName") String lastName,
                             @Field("pictureUrl") String pictureURL,
                             Callback<UserResponse> cb);

    @POST("/rest/v2/socialMobileLogin?provider=GOOGLE")
    void socialLoginGoogle(@Header("Authorization") String authorization,
                           @Field("email") String email,
                           @Field("socialId") String socialId,
                           @Field("firstName") String firstName,
                           @Field("lastName") String lastName,
                           @Field("pictureUrl") String pictureURL,
                           Callback<UserResponse> cb);

    @POST("/reset")
    void resetPassword(@Header("Authorization") String authorization,
                       @Field("email") String email,
                       Callback<UserResponse> cb);

    @GET("/rest/user/")
    void userById(@Header("Authorization") String authorization,
                  @Field("id") String id,
                  Callback<UserResponse> cb);

    @GET("/rest/user/search/findByEmail")
    void userByEmail(@Header("Authorization") String authorization,
                     @Field("email") String email,
                     Callback<UserResponse> cb);

    @GET("/rest/user/search/getAllActiveUsersOrderedByLastLoginDate")
    void users(@Header("Authorization") String authorization,
               @Field("page") Integer page,
               @Field("size") Integer size,
               Callback<UserResponse> cb);

    @GET("/user/changePassword")
    void changePassword(@Header("Authorization") String authorization,
                        @Field("oldPassword") String oldPassword,
                        @Field("newPassword") String newPassword,
                        @Field("newPassword2") String newPassword2,
                        Callback<UserResponse> cb);

    @DELETE("/user/mobileUnregister")
    void deleteAccount(@Header("Authorization") String authorization,
                       @Field("id") String id,
                       Callback<UserResponse> cb);

    @PATCH("/user/save-info")
    void updateUser(@Header("Authorization") String authorization,
                    @Field("id") long userID,
                    @Field("firstName") String firstName,
                    @Field("lastName") String lastName,
                    @Field("email") String email,
                    @Field("password") String password,
                    Callback<UserResponse> cb);

    @PATCH("/user/save-info")
    void updateUser(@Header("Authorization") String authorization,
                    @Field("id") long userID,
                    @Field("firstName") String firstName,
                    @Field("lastName") String lastName,
                    @Field("email") String email,
                    Callback<UserResponse> cb);

    // --- Videos --- //

    @GET("/rest/video/search/findVideosWithPagination")
    void videosForUser(@Header("Authorization") String authorization,
                       @Field("user") long userID,
                       @Field("pageNo") Integer page,
                       @Field("pageSize") Integer size,
                       Callback<Video> cb);

    @GET("/rest/video/search/findVideosWithPagination")
    void relatedVideos(@Header("Authorization") String authorization,
                       @Field("videoId") long videoID,
                       @Field("pageNo") Integer page,
                       @Field("pageSize") Integer size,
                       Callback<Video> cb);

    @GET("/rest/v2/search-videos")
    void searchVideos(@Header("Authorization") String authorization,
                      @Field("pageNo") Integer page,
                      @Field("pageSize") Integer size,
                      Callback<Video> cb);

    // --- Tags --- //

    @GET("/rest/video-tag/search/findTagsByVideo")
    void tagsForVideo(@Header("Authorization") String authorization,
                      @Field("video") long videoID,
                      @Field("pageNo") Integer page,
                      @Field("pageSize") Integer size,
                      Callback<Tag> cb);

    // --- Permissions --- // TODO

    @GET("/rest/v2/get-video-access/?permission=VIEW")
    void videoPermissionView(@Header("Authorization") String authorization,
                             @Field("videoId") long videoID,
                             Callback cb);


    // --- Notifications --- //

    public final static String NOTIFICATION_TYPE_COMMENT = "comment";
    public final static String NOTIFICATION_TYPE_LIKE = "like";

    @GET("/user/updateUserNotification")
    void updateUserNotification(@Header("Authorization") String authorization,
                                @Field("notificationType") String notificationType,
                                @Field("status") boolean status,
                                Callback cb);

    // --- Comments --- //

    @GET("/rest/v2/video-comment/list")
    void videoComments(@Header("Authorization") String authorization,
                       @Field("videoId") long videoID,
                       @Field("pageNo") Integer page,
                       @Field("pageSize") Integer size);

    @PATCH("/rest/video-comment")
    void postVideoComment(@Header("Authorization") String authorization,
                          @Field("content") String content,
                          @Field("video") String videoURL);

    @PATCH("/rest/video-comment")
    void postVideoComment(@Header("Authorization") String authorization,
                          @Field("content") String content,
                          @Field("video") String videoURL,
                          @Field("parentVideoComment") String parentVideoCommentID);
}
