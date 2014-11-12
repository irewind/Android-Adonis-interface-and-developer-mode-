package com.irewind.sdk.api;

public class ApiClient {
    private AuthenticationService authenticationService;
    private ApiService apiService;

    public ApiClient(final String baseURL, final String clientId, final String clientSecret) {
        authenticationService = ServiceFactory.createAuthenticationService(baseURL, clientId, clientSecret);
        apiService = ServiceFactory.createApiService(baseURL);
    }

    public boolean isAuthenticated() {
        return false;
    }

    public void login(String username, String password) {

    }

    public void register(String email,
                         String firstName,
                         String lastName,
                         String password) {
    }

    public void loginFACEBOOK(String email,
                        String socialId,
                        String socialIdProvider,
                        String firstName,
                        String lastName,
                        String pictureURL) {


    }

    public void loginGOOGLE(String email,
                        String socialId,
                        String socialIdProvider,
                        String firstName,
                        String lastName,
                        String pictureURL) {


    }
}
