package com.irewind.sdk.api;

import com.irewind.sdk.model.AccessToken;
import com.irewind.sdk.model.BaseResponse;
import com.irewind.sdk.model.ResetPasswordResponse;
import com.irewind.sdk.model.UserResponse;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface SessionService {

    @POST("/oauth/token?grant_type=password")
    @FormUrlEncoded
    AccessToken getAccessToken(@Field("username") String username,
                               @Field("password") String password);

    @POST("/oauth/token?grant_type=password")
    @FormUrlEncoded
    void getAccessToken(@Field("username")    String username,
                        @Field("password")    String password,
                        Callback<AccessToken> callback);

    @POST("/oauth/token?grant_type=refresh_token")
    @FormUrlEncoded
    void refreshAccessToken(@Field("refresh_token")  String refreshToken,
                            Callback<AccessToken> callback);

    // why is this used? not unsafe at all /s
    static final String DEFAULT_USERNAME = "tremend@mailinator.com";
    static final String DEFAUL_PASSWORD = "tremend.admin";
    @POST("/oauth/token?grant_type=password&username="+DEFAULT_USERNAME+"&password="+DEFAUL_PASSWORD)
    @FormUrlEncoded
    AccessToken getAccessToken();
    @POST("/oauth/token?grant_type=password&username="+DEFAULT_USERNAME+"&password="+DEFAUL_PASSWORD)
    @FormUrlEncoded
    void getAccessToken(Callback<AccessToken> callback);

    @POST("/registeriOS")
    @FormUrlEncoded
    void addUser(@Field("email") String email,
                 @Field("firstName") String firstName,
                 @Field("lastName") String lastName,
                 @Field("password") String password,
                 Callback<BaseResponse> cb);

    @POST("/rest/v2/socialMobileLogin")
    @FormUrlEncoded
    void socialLogin(@Field("email") String email,
                     @Field("socialId") String socialId,
                     @Field("provider") String socialIdProvider,
                     @Field("firstName") String firstName,
                     @Field("lastName") String lastName,
                     @Field("pictureUrl") String pictureURL,
                     Callback<BaseResponse> cb);

    @POST("/rest/v2/socialMobileLogin?provider=FACEBOOK")
    @FormUrlEncoded
    void socialLoginFacebook(@Field("email") String email,
                             @Field("socialId") String socialId,
                             @Field("firstName") String firstName,
                             @Field("lastName") String lastName,
                             @Field("pictureUrl") String pictureURL,
                             Callback<BaseResponse> cb);

    @POST("/rest/v2/socialMobileLogin?provider=GOOGLE")
    @FormUrlEncoded
    void socialLoginGoogle(@Field("email") String email,
                           @Field("socialId") String socialId,
                           @Field("firstName") String firstName,
                           @Field("lastName") String lastName,
                           @Field("pictureUrl") String pictureURL,
                           Callback<BaseResponse> cb);

    @POST("/reset")
    @FormUrlEncoded
    void resetPassword(@Field("email") String email,
                       Callback<ResetPasswordResponse> cb);
}
