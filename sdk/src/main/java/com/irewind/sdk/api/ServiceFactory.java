package com.irewind.sdk.api;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.Proxy;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class ServiceFactory {

    private ServiceFactory() {}

    public static SessionService createSessionService(final String baseURL, final String clientId, final String clientSecret) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setAuthenticator(new Authenticator() {
            @Override
            public Request authenticate(Proxy proxy, Response response) throws IOException {
                String credential = Credentials.basic(clientId, clientSecret);
                return response.request().newBuilder()
                        .header("Authorization", credential)
                        .build();
            }

            @Override
            public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
                return null;
            }
        });

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(baseURL)
                .setClient(new OkClient(okHttpClient))
                .build();

        return adapter.create(SessionService.class);
    }

    public static ApiService createApiService(String baseURL) {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(baseURL)
                .setClient(new OkClient(new OkHttpClient()));

        RestAdapter adapter = builder.build();

        return adapter.create(ApiService.class);
    }
}
