package com.irewind.sdk.api;

import com.irewind.sdk.model.UserResponse;

import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

public interface ApiService {

    // --- USER --- //

    @POST("/registeriOS")
    UserResponse addUser(@Header("Authorization") String authorization,
                         @Field("email")          String email,
                         @Field("firstName")      String firstName,
                         @Field("lastName")       String lastName,
                         @Field("password")       String password);

    @POST("/rest/v2/socialMobileLogin")
    UserResponse socialLogin(@Header("Authorization") String authorization,
                             @Field("email")          String email,
                             @Field("socialId")       String socialId,
                             @Field("provider")       String socialIdProvider,
                             @Field("firstName")      String firstName,
                             @Field("lastName")       String lastName,
                             @Field("pictureUrl")     String pictureURL);

    @POST("/rest/v2/socialMobileLogin?provider=FACEBOOK")
    UserResponse socialLoginFacebook(@Header("Authorization") String authorization,
                                     @Field("email")          String email,
                                     @Field("socialId")       String socialId,
                                     @Field("firstName")      String firstName,
                                     @Field("lastName")       String lastName,
                                     @Field("pictureUrl")     String pictureURL);

    @POST("/rest/v2/socialMobileLogin?provider=GOOGLE")
    UserResponse socialLoginGoogle(@Header("Authorization") String authorization,
                                   @Field("email")          String email,
                                   @Field("socialId")       String socialId,
                                   @Field("firstName")      String firstName,
                                   @Field("lastName")       String lastName,
                                   @Field("pictureUrl")     String pictureURL);

    @GET("/rest/user/search/findByEmail")
    UserResponse userByEmail(@Header("Authorization") String authorization,
                             @Field("email")          String email);
}
