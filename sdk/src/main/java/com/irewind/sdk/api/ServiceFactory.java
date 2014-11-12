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

    public static AuthenticationService createAuthenticationService(final String baseURL, final String clientId, final String clientSecret) {
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
                .setEndpoint(baseURL)
                .setClient(new OkClient(okHttpClient))
                .build();

        return adapter.create(AuthenticationService.class);
    }

    public static ApiService createApiService(String baseURL) {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(baseURL)
                .setClient(new OkClient(new OkHttpClient()));

        RestAdapter adapter = builder.build();

        return adapter.create(ApiService.class);
    }
}
