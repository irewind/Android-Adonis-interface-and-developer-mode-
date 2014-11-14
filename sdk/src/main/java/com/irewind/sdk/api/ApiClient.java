package com.irewind.sdk.api;

import com.irewind.sdk.model.Session;

public class ApiClient {
    private ApiService apiService;

    public ApiClient(final String baseURL) {
        apiService = ServiceFactory.createApiService(baseURL);
    }

    public boolean isAuthenticated() {
        return false;
    }

    public void register(Session session,
                         String email,
                         String firstName,
                         String lastName,
                         String password) {
    }

    public void loginFACEBOOK(Session session,
                              String email,
                              String socialId,
                              String socialIdProvider,
                              String firstName,
                              String lastName,
                              String pictureURL) {


    }

    public void loginGOOGLE(Session session,
                            String email,
                            String socialId,
                            String socialIdProvider,
                            String firstName,
                            String lastName,
                            String pictureURL) {


    }
}
