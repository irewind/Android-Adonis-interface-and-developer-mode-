package com.irewind.sdk.api;

import com.squareup.okhttp.OkAuthenticator;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class ServiceFactory {

    private static final String CLIENT_ID = "web-client";
    private static final String CLIENT_SECRET = "web-client-secret";

    private ServiceFactory() {}

    public static OAuthService createOAuthService(final String baseUrl, final String clientId, final String clientSecret) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setAuthenticator(new OkAuthenticator() {
            @Override
            public Credential authenticate(Proxy proxy, URL url, List<Challenge> challenges) throws IOException {
                return Credential.basic(clientId, clientSecret);
            }

            @Override
            public Credential authenticateProxy(Proxy proxy, URL url, List<Challenge> challenges) throws IOException {
                return null;
            }
        });

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(baseUrl)
                .setClient(new OkClient(okHttpClient))
                .build();

        return adapter.create(OAuthService.class);
    }

    public static ApiService createApiService(String baseUrl, final String accessToken) {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(baseUrl)
                .setClient(new OkClient(new OkHttpClient()));

        if (accessToken != null) {
            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Accept", "application/json");
                    request.addHeader("Authorization", "Bearer" +
                            " " + accessToken);
                }
            });
        }

        RestAdapter adapter = builder.build();

        return adapter.create(ApiService.class);
    }
}
