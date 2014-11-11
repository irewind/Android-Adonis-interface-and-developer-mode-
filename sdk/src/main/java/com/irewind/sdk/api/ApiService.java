package com.irewind.sdk.api;

import com.irewind.sdk.model.UserResponse;

import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.POST;

public interface ApiService {
    @POST("/registeriOS")
    UserResponse addUser(@Field("email")      String email,
                         @Field("firstName")  String firstName,
                         @Field("lastName")   String lastName,
                         @Field("password")   String password);

    @POST("/rest/v2/socialMobileLogin")
    UserResponse socialLogin(@Field("email")      String email,
                             @Field("socialId")   String socialId,
                             @Field("provider")   String socialIdProvider,
                             @Field("firstName")  String firstName,
                             @Field("lastName")   String lastName,
                             @Field("pictureUrl") String pictureURL);

    @POST("/rest/v2/socialMobileLogin?provider=FACEBOOK")
    UserResponse socialLoginFacebook(@Field("email")      String email,
                                     @Field("socialId")   String socialId,
                                     @Field("firstName")  String firstName,
                                     @Field("lastName")   String lastName,
                                     @Field("pictureUrl") String pictureURL);

    @POST("/rest/v2/socialMobileLogin?provider=GOOGLE")
    UserResponse socialLoginGoogle(@Field("email")      String email,
                                   @Field("socialId")   String socialId,
                                   @Field("firstName")  String firstName,
                                   @Field("lastName")   String lastName,
                                   @Field("pictureUrl") String pictureURL);

    @GET("/rest/user/search/findByEmail")
    UserResponse userByEmail(@Field("email") String email);
}
