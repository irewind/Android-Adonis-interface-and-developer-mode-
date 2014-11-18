package com.irewind.sdk.api;

import android.content.Context;
import android.os.Bundle;

import com.irewind.sdk.api.cache.SharedPreferencesUserCachingStrategy;
import com.irewind.sdk.api.cache.UserCachingStrategy;
import com.irewind.sdk.api.event.NoActiveUserEvent;
import com.irewind.sdk.api.event.RestErrorEvent;
import com.irewind.sdk.api.event.UserInfoLoadedEvent;
import com.irewind.sdk.api.event.UserListEvent;
import com.irewind.sdk.api.event.UserResponseEvent;
import com.irewind.sdk.model.Session;
import com.irewind.sdk.model.User;
import com.irewind.sdk.model.UserResponse;

import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ApiClient {
    /**
     * The logging tag used by ApiClient.
     */
    public static final String TAG = ApiClient.class.getCanonicalName();

    private static final String USER_BUNDLE_SAVE_KEY = "com.irewind.sdk.User.saveUserKey";

    private Context context;
    private ApiService apiService;
    private EventBus eventBus;

    private User activeUser;
    private UserCachingStrategy userCachingStrategy;
    private final Object lock = new Object();

    public ApiClient(Context context, final String baseURL) {
        this(context, baseURL, null, null);
    }

    public ApiClient(Context context, final String baseURL, UserCachingStrategy userCachingStrategy) {
        this(context, baseURL, userCachingStrategy, null);
    }

    public ApiClient(Context context, final String baseURL, EventBus eventBus) {
        this(context, baseURL, null, eventBus);
    }

    public ApiClient(Context context, final String baseURL, UserCachingStrategy userCachingStrategy, EventBus eventBus) {
        this.context = context;

        this.userCachingStrategy = userCachingStrategy;
        if (userCachingStrategy == null) {
            this.userCachingStrategy = new SharedPreferencesUserCachingStrategy(context);
        }

        this.eventBus = eventBus;
        if (eventBus == null) {
            this.eventBus = new EventBus();
        }

        apiService = ServiceFactory.createApiService(baseURL);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public Context getContext() {
        return context;
    }

    public User getActiveUser() {
        synchronized (this.lock) {
            return activeUser;
        }
    }

    public final void setActiveUser(User user) {
        synchronized (this.lock) {
            if (user != this.activeUser) {
                User oldUser = this.activeUser;

//                if (oldUser != null) {
//                    oldUser.close();
//                }

                this.activeUser = user;

                userCachingStrategy.save(UserCachingStrategy.userToBundle(this.activeUser));
            }
        }
    }

    private String authHeader(Session session) {
//        try {
//            String token = session.getAccessToken();
//            byte[] bytes = token.getBytes("ISO-8859-1");
//            String encoded = ByteString.of(bytes).base64();
//            return "Basic " + encoded;
//        } catch (UnsupportedEncodingException e) {
//            throw new AssertionError();
//        }
        return "Bearer " + session.getAccessToken();
    }

    public void loadActiveUserInfo() {
        Bundle userBundle = userCachingStrategy.load();
        if (userBundle != null) {
            User user = UserCachingStrategy.createFromBundle(userBundle);
            setActiveUser(user);
            eventBus.post(new UserInfoLoadedEvent(user));
        } else {
            eventBus.post(new NoActiveUserEvent());
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
                List<User> users = userResponse.getEmbedded().getUsers();
                if (users != null && users.size() > 0) {
                    eventBus.post(new UserResponseEvent(users.get(0)));
                }
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
                List<User> users = userResponse.getEmbedded().getUsers();
                if (users != null && users.size() > 0) {
                    eventBus.post(new UserResponseEvent(users.get(0)));
                }
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
                List<User> users = userResponse.getEmbedded().getUsers();
                if (users != null && users.size() > 0) {
                    eventBus.post(new UserResponseEvent(users.get(0)));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new RestErrorEvent(error));
            }
        });
    }

    public void getActiveUserByEmail(Session session,
                                     String email) {
        apiService.userByEmail(authHeader(session), email, new Callback<UserResponse>() {
            @Override
            public void success(UserResponse userResponse, Response response) {
                List<User> users = userResponse.getEmbedded().getUsers();
                if (users != null && users.size() > 0) {
                    User user = users.get(0);
                    setActiveUser(user);
                }
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
                List<User> users = userResponse.getEmbedded().getUsers();
                if (users != null && users.size() > 0) {
                    eventBus.post(new UserResponseEvent(users.get(0)));
                }
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
                List<User> users = userResponse.getEmbedded().getUsers();
                if (users != null && users.size() > 0) {
                    eventBus.post(new UserListEvent(users));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new RestErrorEvent(error));
            }
        });
    }
}
