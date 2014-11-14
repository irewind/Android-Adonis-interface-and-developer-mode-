package com.irewind.sdk.api;

import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class ServiceFactory {

    private ServiceFactory() {
    }

    public static SessionService createSessionService(final String baseURL, final String clientId, final String clientSecret) {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(baseURL)
                .setClient(new OkClient(new OkHttpClient()))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        final String authorizationValue = Credentials.basic(clientId, clientSecret);
                        request.addHeader("Authorization", authorizationValue);
                    }
                })
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
