package com.irewind.sdk.api;

import com.irewind.sdk.api.response.SignUpResponse;
import com.irewind.sdk.model.AccessToken;
import com.irewind.sdk.api.response.BaseResponse;
import com.irewind.sdk.api.response.ResetPasswordResponse;

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

    @POST("/registeriOS")
    @FormUrlEncoded
    void addUser(@Field("email") String email,
                 @Field("firstName") String firstName,
                 @Field("lastName") String lastName,
                 @Field("password") String password,
                 Callback<SignUpResponse> cb);

    @POST("/reset")
    @FormUrlEncoded
    void resetPassword(@Field("email") String email,
                       Callback<ResetPasswordResponse> cb);
}
