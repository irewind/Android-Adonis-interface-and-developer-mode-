package com.irewind.sdk.api;

import android.content.Context;
import android.os.Bundle;

import com.irewind.sdk.api.cache.SharedPreferencesUserCachingStrategy;
import com.irewind.sdk.api.cache.UserCachingStrategy;
import com.irewind.sdk.api.event.NoActiveUserEvent;
import com.irewind.sdk.api.event.PasswordChangeFailEvent;
import com.irewind.sdk.api.event.PasswordChangeSuccessEvent;
import com.irewind.sdk.api.event.RestErrorEvent;
import com.irewind.sdk.api.event.UserDeleteSuccessEvent;
import com.irewind.sdk.api.event.UserInfoLoadedEvent;
import com.irewind.sdk.api.event.UserInfoUpdateFailEvent;
import com.irewind.sdk.api.event.UserInfoUpdateSuccessEvent;
import com.irewind.sdk.api.event.UserListEvent;
import com.irewind.sdk.api.event.UserNotificationSettingsLoadedEvent;
import com.irewind.sdk.api.event.UserNotificationSettingsUpdateFailEvent;
import com.irewind.sdk.api.event.UserNotificationSettingsUpdateSuccessEvent;
import com.irewind.sdk.api.event.UserResponseEvent;
import com.irewind.sdk.model.AccessToken;
import com.irewind.sdk.model.NotificationSettings;
import com.irewind.sdk.model.NotificationSettingsResponse;
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

                eventBus.post(new UserInfoLoadedEvent(user));
            }
        }
    }

    private String authHeader(Session session) {
        return authHeader(session.getTokenInfo());
    }

    private String authHeader(AccessToken accessToken) {
//        try {
//            String token = session.getAccessToken();
//            byte[] bytes = token.getBytes("ISO-8859-1");
//            String encoded = ByteString.of(bytes).base64();
//            return "Basic " + encoded;
//        } catch (UnsupportedEncodingException e) {
//            throw new AssertionError();
//        }
        return "Bearer " + accessToken.getCurrentToken();
    }

    public void loadActiveUserInfo() {
        Bundle userBundle = userCachingStrategy.load();
        if (userBundle != null) {
            User user = UserCachingStrategy.createFromBundle(userBundle);
            setActiveUser(user);
        } else {
            eventBus.post(new NoActiveUserEvent());
        }
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

    public void updateUser(final Session session, final User user, String firstname, String lastname) {
        apiService.updateUser(authHeader(session), user.getId(), firstname, lastname, new Callback<Boolean>() {
            @Override
            public void success(Boolean success, Response response) {
                if (success) {
                    eventBus.post(new UserInfoUpdateSuccessEvent());

                    getActiveUserByEmail(session, user.getEmail());
                } else {
                    eventBus.post(new UserInfoUpdateFailEvent(UserInfoUpdateFailEvent.Reason.Unknown));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new UserInfoUpdateFailEvent(UserInfoUpdateFailEvent.Reason.Unknown));
            }
        });
    }

    public void changeUserPassword(final Session session, final User user, String currentPassword, String newPassword) {
        apiService.changePassword(authHeader(session), user.getId(), currentPassword, newPassword, newPassword, new Callback<Boolean>() {
            @Override
            public void success(Boolean success, Response response) {
                if (success) {
                    eventBus.post(new PasswordChangeSuccessEvent());
                } else {
                    eventBus.post(new PasswordChangeFailEvent(PasswordChangeFailEvent.Reason.WrongPassword));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new PasswordChangeFailEvent(PasswordChangeFailEvent.Reason.Unknown));
            }
        });
    }

    public void deleteUser(final Session session, final User user) {
        apiService.deleteAccount(authHeader(session), user.getId(), new Callback<Boolean>() {
            @Override
            public void success(Boolean success, Response response) {
                eventBus.post(new UserDeleteSuccessEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new RestErrorEvent(error));
            }
        });
    }

    // --- Notification Settings --- //

    public void getUserNotificationSettings(final Session session, final User user) {
        apiService.userNotificationSettings(authHeader(session), user.getId(), new Callback<NotificationSettingsResponse>() {
            @Override
            public void success(NotificationSettingsResponse notificationSettingsResponse, Response response) {
                List<NotificationSettings> results = notificationSettingsResponse.getContent();
                if (results != null && results.size() > 0) {
                    eventBus.post(new UserNotificationSettingsLoadedEvent(results.get(0)));
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void toggleCommentNotifications(final Session session, boolean status) {
        apiService.toggleCommentNotifications(authHeader(session), status, new Callback<Boolean>() {
            @Override
            public void success(Boolean success, Response response) {
                if (success) {
                    eventBus.post(new UserNotificationSettingsUpdateSuccessEvent());
                }
                else {
                    eventBus.post(new UserNotificationSettingsUpdateFailEvent());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new UserNotificationSettingsUpdateFailEvent());
            }
        });
    }

    public void toggleShareNotifications(final Session session, boolean status) {
        apiService.toggleShareNotifications(authHeader(session), status, new Callback<Boolean>() {
            @Override
            public void success(Boolean success, Response response) {
                if (success) {
                    eventBus.post(new UserNotificationSettingsUpdateSuccessEvent());
                }
                else {
                    eventBus.post(new UserNotificationSettingsUpdateFailEvent());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new UserNotificationSettingsUpdateFailEvent());
            }
        });
    }

    public void toggleLikeNotifications(final Session session, boolean status) {
        apiService.toggleLikeNotifications(authHeader(session), status, new Callback<Boolean>() {
            @Override
            public void success(Boolean success, Response response) {
                if (success) {
                    eventBus.post(new UserNotificationSettingsUpdateSuccessEvent());
                }
                else {
                    eventBus.post(new UserNotificationSettingsUpdateFailEvent());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new UserNotificationSettingsUpdateFailEvent());
            }
        });
    }

    public void toggleMessageNotifications(final Session session, boolean status) {
        apiService.toggleMessageNotifications(authHeader(session), status, new Callback<Boolean>() {
            @Override
            public void success(Boolean success, Response response) {
                if (success) {
                    eventBus.post(new UserNotificationSettingsUpdateSuccessEvent());
                }
                else {
                    eventBus.post(new UserNotificationSettingsUpdateFailEvent());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new UserNotificationSettingsUpdateFailEvent());
            }
        });
    }
}
