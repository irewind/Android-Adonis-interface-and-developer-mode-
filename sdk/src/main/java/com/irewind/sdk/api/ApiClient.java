package com.irewind.sdk.api;

import com.irewind.sdk.api.Events.RestErrorEvent;
import com.irewind.sdk.api.Events.UserResponseEvent;
import com.irewind.sdk.model.Session;
import com.irewind.sdk.model.UserResponse;
import com.squareup.okhttp.internal.Base64;

import java.io.UnsupportedEncodingException;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ApiClient {
    private ApiService apiService;
    private EventBus eventBus;

    public ApiClient(final String baseURL) {
        this(baseURL, null);
    }

    public ApiClient(final String baseURL, EventBus eventBus) {
        apiService = ServiceFactory.createApiService(baseURL);
        this.eventBus = eventBus;
        if (eventBus == null) {
            this.eventBus = new EventBus();
        }
    }

    private String authHeader(Session session) {
        try {
            String token = session.getAccessToken();
            byte[] bytes = token.getBytes("ISO-8859-1");
            String encoded = Base64.encode(bytes);
            return "Bearer " + encoded;
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError();
        }
    }

    public void register(Session session,
                         String email,
                         String firstName,
                         String lastName,
                         String password) {
        apiService.addUser(authHeader(session), email, firstName, lastName, password, new Callback<UserResponse>() {
            @Override
            public void success(UserResponse userResponse, Response response) {
                eventBus.post(new UserResponseEvent(userResponse.getEmbedded().getUser()));
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new RestErrorEvent(error));
            }
        });
    }

    public void loginFACEBOOK(Session session,
                              String email,
                              String socialId,
                              String firstName,
                              String lastName,
                              String pictureURL) {
        apiService.socialLoginFacebook(authHeader(session), email, socialId, firstName, lastName, pictureURL, new Callback<UserResponse>() {
            @Override
            public void success(UserResponse userResponse, Response response) {
                eventBus.post(new UserResponseEvent(userResponse.getEmbedded().getUser()));
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new RestErrorEvent(error));
            }
        });
    }

    public void loginGOOGLE(Session session,
                            String email,
                            String socialId,
                            String socialIdProvider,
                            String firstName,
                            String lastName,
                            String pictureURL) {
        apiService.socialLoginGoogle(authHeader(session), email, socialId, firstName, lastName, pictureURL, new Callback<UserResponse>() {
            @Override
            public void success(UserResponse userResponse, Response response) {
                eventBus.post(new UserResponseEvent(userResponse.getEmbedded().getUser()));
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new RestErrorEvent(error));
            }
        });
    }

    public void getUserByEmail(Session session,
                               String email) {
        apiService.userByEmail(authHeader(session), email, new Callback<UserResponse>() {
            @Override
            public void success(UserResponse userResponse, Response response) {
                eventBus.post(new UserResponseEvent(userResponse.getEmbedded().getUser()));
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new RestErrorEvent(error));
            }
        });
    }

    public void getUsers(Session session, Integer page) {
        apiService.users(authHeader(session), page, 20, new Callback<UserResponse>() {
            @Override
            public void success(UserResponse userResponse, Response response) {
                eventBus.post(new UserResponseEvent(userResponse.getEmbedded().getUser()));
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new RestErrorEvent(error));
            }
        });
    }
}
