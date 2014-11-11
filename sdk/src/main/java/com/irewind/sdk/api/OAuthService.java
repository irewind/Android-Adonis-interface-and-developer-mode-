package com.irewind.sdk.api;

import com.irewind.sdk.model.AccessToken;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface OAuthService {

    @POST("/oauth/token?grant_type=password")
    @FormUrlEncoded
    AccessToken getAccessToken(@Field("username")        String username,
                               @Field("password")        String password);

    @POST("/oauth/token?grant_type=refresh_token")
    @FormUrlEncoded
    AccessToken refreshAccessToken(@Field("refresh_token")  String refreshToken);


    // why is this used? not unsafe at all /s
    static final String DEFAULT_USERNAME = "tremend@mailinator.com";
    static final String DEFAUL_PASSWORD = "tremend.admin";
    @POST("/oauth/token?grant_type=password&username="+DEFAULT_USERNAME+"&password="+DEFAUL_PASSWORD)
    @FormUrlEncoded
    AccessToken getAccessToken();
}
