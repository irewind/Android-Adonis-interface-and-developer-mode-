package com.irewind.sdk.api;

import android.util.Base64;

import com.irewind.sdk.model.AccessToken;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class ServiceFactory {

    private static final String CLIENT_ID = "web-client";
    private static final String CLIENT_SECRET = "web-client-secret";

    private ServiceFactory() {}

    public static OAuthService createOAuthService(final String baseUrl, final String clientId, final String clientSecret) {
        final String basicAuthCredentials = Base64.encodeToString((clientId + ":" + clientSecret).getBytes(), Base64.NO_WRAP);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(baseUrl)
                .setClient(new OkClient(new OkHttpClient()))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Basic " + basicAuthCredentials);
                    }
                })
                .build();

        return adapter.create(OAuthService.class);
    }

    public static ApiService createApiService(String baseUrl, final AccessToken accessToken, OAuthService authService) {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(baseUrl)
                .setClient(new OAuthClient(new OkHttpClient(), accessToken, authService));

        if (accessToken != null) {
            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Accept", "application/json");
                    request.addHeader("Authorization", "Bearer" +
                            " " + accessToken.getAccessToken());
                }
            });
        }

        RestAdapter adapter = builder.build();

        return adapter.create(ApiService.class);
    }
}
