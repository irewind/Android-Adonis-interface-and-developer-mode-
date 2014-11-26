package com.irewind.sdk.api;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.irewind.sdk.api.cache.SharedPreferencesTokenCachingStrategy;
import com.irewind.sdk.api.cache.SharedPreferencesUserCachingStrategy;
import com.irewind.sdk.api.cache.TokenCachingStrategy;
import com.irewind.sdk.api.cache.UserCachingStrategy;
import com.irewind.sdk.api.event.CommentAddEvent;
import com.irewind.sdk.api.event.CommentAddFailEvent;
import com.irewind.sdk.api.event.CommentListEvent;
import com.irewind.sdk.api.event.CommentListFailEvent;
import com.irewind.sdk.api.event.NoActiveUserEvent;
import com.irewind.sdk.api.event.NotificationSettingsListFailedEvent;
import com.irewind.sdk.api.event.NotificationSettingsListSuccessEvent;
import com.irewind.sdk.api.event.NotificationSettingsUpdateFailEvent;
import com.irewind.sdk.api.event.NotificationSettingsUpdateSuccessEvent;
import com.irewind.sdk.api.event.PasswordChangeFailEvent;
import com.irewind.sdk.api.event.PasswordChangeSuccessEvent;
import com.irewind.sdk.api.event.RegisterFailEvent;
import com.irewind.sdk.api.event.RegisterSuccessEvent;
import com.irewind.sdk.api.event.ResetPasswordFailEvent;
import com.irewind.sdk.api.event.ResetPasswordSuccesEvent;
import com.irewind.sdk.api.event.RestErrorEvent;
import com.irewind.sdk.api.event.SessionClosedEvent;
import com.irewind.sdk.api.event.SessionOpenFailEvent;
import com.irewind.sdk.api.event.SessionOpenedEvent;
import com.irewind.sdk.api.event.UserDeleteSuccessEvent;
import com.irewind.sdk.api.event.UserInfoLoadedEvent;
import com.irewind.sdk.api.event.UserInfoUpdateFailEvent;
import com.irewind.sdk.api.event.UserInfoUpdateSuccessEvent;
import com.irewind.sdk.api.event.UserListEvent;
import com.irewind.sdk.api.event.UserListFailEvent;
import com.irewind.sdk.api.event.UserResponseEvent;
import com.irewind.sdk.api.event.VideoInfoEvent;
import com.irewind.sdk.api.event.VideoInfoFailEvent;
import com.irewind.sdk.api.event.VideoListEvent;
import com.irewind.sdk.api.event.VideoListFailEvent;
import com.irewind.sdk.api.request.CreateCommentRequest;
import com.irewind.sdk.api.request.ReplyCommentRequest;
import com.irewind.sdk.api.response.BaseResponse;
import com.irewind.sdk.api.response.CommentListResponse;
import com.irewind.sdk.api.response.NotificationSettingsResponse;
import com.irewind.sdk.api.response.ResetPasswordResponse;
import com.irewind.sdk.api.response.TagListResponse;
import com.irewind.sdk.api.response.UserListResponse;
import com.irewind.sdk.api.response.UserResponse;
import com.irewind.sdk.api.response.VideoListResponse;
import com.irewind.sdk.api.response.VideoListResponse2;
import com.irewind.sdk.api.response.VideoResponse;
import com.irewind.sdk.iRewindConfig;
import com.irewind.sdk.iRewindException;
import com.irewind.sdk.model.AccessToken;
import com.irewind.sdk.model.NotificationSettings;
import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.User;
import com.irewind.sdk.model.Video;
import com.irewind.sdk.util.SafeAsyncTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ApiClient implements SessionRefresher {
    /**
     * The logging tag used by ApiClient.
     */
    public static final String TAG = ApiClient.class.getCanonicalName();

    private static final String SESSION_BUNDLE_SAVE_KEY = "com.irewind.sdk.Session.saveSessionKey";
    private static final String USER_BUNDLE_SAVE_KEY = "com.irewind.sdk.User.saveUserKey";

    private final Object lock = new Object();

    private Context context;
    private EventBus eventBus;

    private iRewindConfig config;

    private SessionService sessionService;
    private ApiService apiService;

    private Session activeSession;
    private TokenCachingStrategy tokenCachingStrategy;

    private User activeUser;
    private UserCachingStrategy userCachingStrategy;

    public ApiClient(Context context, iRewindConfig config) {
        this(context, config, null, null, null);
    }

    public ApiClient(Context context, iRewindConfig config, EventBus eventBus) {
        this(context, config, null, null, eventBus);
    }

    public ApiClient(Context context, iRewindConfig config, TokenCachingStrategy tokenCachingStrategy, UserCachingStrategy userCachingStrategy, EventBus eventBus) {
        this.context = context;
        this.config = config;

        this.tokenCachingStrategy = tokenCachingStrategy;
        if (tokenCachingStrategy == null) {
            this.tokenCachingStrategy = new SharedPreferencesTokenCachingStrategy(context);
        }

        sessionService = ServiceFactory.createSessionService(config.getBaseURL(), config.getClientID(), config.getClientSecret());

        this.userCachingStrategy = userCachingStrategy;
        if (userCachingStrategy == null) {
            this.userCachingStrategy = new SharedPreferencesUserCachingStrategy(context);
        }

        this.eventBus = eventBus;
        if (eventBus == null) {
            this.eventBus = new EventBus();
        }

        apiService = ServiceFactory.createApiService(config.getBaseURL());

        activeSession = openActiveSession(context);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public Context getContext() {
        return context;
    }

    public Session getActiveSession() {
        synchronized (this.lock) {
            return activeSession;
        }
    }

    /**
     * <p>
     * Sets the current active Session.
     * </p>
     * <p>
     * It is legal to set this to null, or to a Session that is not yet open.
     * </p>
     *
     * @param session A Session to use as the active Session, or null to indicate
     *                that there is no active Session.
     */
    public final void setActiveSession(Session session) {
        synchronized (this.lock) {
            if (session != this.activeSession) {
                Session oldSession = this.activeSession;

                if (oldSession != null) {
                    oldSession.close();
                }

                this.activeSession = session;
            }
        }
    }

    private static final String adminUsername = "tremend@mailinator.com";
    private static final String adminSecret = "tremend.admin";

    private void getAccessToken(String username, String password, final Callback<AccessToken> cb) {
        sessionService.getAccessToken(username, password, new Callback<AccessToken>() {
            @Override
            public void success(AccessToken accessToken, Response response) {
                if (cb != null) {
                    cb.success(accessToken, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (cb != null) {
                    cb.failure(error);
                }
            }
        });
    }

    public void openSession(String username, String password) {
        getAccessToken(username, password, new Callback<AccessToken>() {
            @Override
            public void success(AccessToken accessToken, Response response) {
                accessToken.setLastRefreshDate(new Date());
                openActiveSessionWithAccessToken(context, accessToken);
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new SessionOpenFailEvent(SessionOpenFailEvent.Reason.BadCredentials, null));
            }
        });
    }

    public Session openSession(Session session) {
        if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState())) {
            session.open(session.getTokenInfo());
            setActiveSession(session);

            eventBus.post(new SessionOpenedEvent());
            return session;
        }
        return null;
    }

    private Session openActiveSession(Context context) {
        Session session = new Session.Builder(context).build();
        if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState())) {
            session.open(session.getTokenInfo());
            setActiveSession(session);

            eventBus.post(new SessionOpenedEvent());
            return session;
        }
        else if(SessionState.OPENED_TOKEN_EXPIRED.equals(session.getState())) {
            refreshSession(session);
        }
        return session;
    }

    /**
     * If a cached token is available, creates and opens the session and makes it active without any user interaction,
     * otherwise this does nothing.
     *
     * @return The new session or null if one could not be created
     */
    private Session openActiveSessionFromCache(Context context) {
        Session session = new Session(context, tokenCachingStrategy, true);
        if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState())) {
            session.open(session.getTokenInfo());
            setActiveSession(session);

            eventBus.post(new SessionOpenedEvent());
        }
        return session;
    }

    /**
     * Opens a session based on an existing iRewind access token, and also makes this session
     * the currently active session. This method should be used
     * only in instances where an application has previously obtained an access token and wishes
     * to import it into the Session/TokenCachingStrategy-based session-management system. A primary
     * example would be an application which previously did not use the iRewind SDK for Android
     * and implemented its own session-management scheme, but wishes to implement an upgrade path
     * for existing users so they do not need to log in again when upgrading to a version of
     * the app that uses the SDK. In general, this method will be called only once, when the app
     * detects that it has been upgraded -- after that, the usual Session lifecycle methods
     * should be used to manage the session and its associated token.
     * <p/>
     * No validation is done that the token, token source, or permissions are actually valid.
     * It is the caller's responsibility to ensure that these accurately reflect the state of
     * the token that has been passed in, or calls to the iRewind API may fail.
     *
     * @param accessToken the access token obtained from iRewind
     * @return The new Session or null if one could not be created
     */
    private Session openActiveSessionWithAccessToken(Context context, AccessToken accessToken) {
        Session session = new Session(context, tokenCachingStrategy, false);
        session.open(accessToken);
        setActiveSession(session);

        eventBus.post(new SessionOpenedEvent());
        return session;
    }

    public void refreshSession(Session session) {
        AccessToken accessToken = session.getTokenInfo();
        sessionService.refreshAccessToken(accessToken.getRefreshToken(), new Callback<AccessToken>() {
            @Override
            public void success(AccessToken accessToken, Response response) {
                accessToken.setLastRefreshDate(new Date());
                openActiveSessionWithAccessToken(context, accessToken);
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new SessionOpenFailEvent(SessionOpenFailEvent.Reason.BadCredentials, null));
            }
        });
    }

    public final void closeSession() {
        getActiveSession().close();
        eventBus.post(new SessionClosedEvent());
    }

    public final void closeSessionAndClearTokenInformation() {
        getActiveSession().closeAndClearTokenInformation();
        eventBus.post(new SessionClosedEvent());
    }

    /**
     * Save the Session object into the supplied Bundle. This method is intended to be called from an
     * Activity or Fragment's onSaveInstanceState method in order to preserve Sessions across Activity lifecycle events.
     *
     * @param session the Session to save
     * @param bundle  the Bundle to save the Session to
     */
    public final void saveSession(Session session, Bundle bundle) {
        if (bundle != null && session != null && !bundle.containsKey(SESSION_BUNDLE_SAVE_KEY)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                new ObjectOutputStream(outputStream).writeObject(session);
            } catch (IOException e) {
                throw new iRewindException("Unable to save session.", e);
            }
            bundle.putByteArray(SESSION_BUNDLE_SAVE_KEY, outputStream.toByteArray());
        }
    }

    /**
     * Restores the saved session from a Bundle, if any. Returns the restored Session or
     * null if it could not be restored. This method is intended to be called from an Activity or Fragment's
     * onCreate method when a Session has previously been saved into a Bundle via saveState to preserve a Session
     * across Activity lifecycle events.
     *
     * @param context the Activity or Service creating the Session, must not be null
     * @param bundle  the bundle to restore the Session from
     * @return the restored Session, or null
     */
    public final Session restoreSession(
            Context context, Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        byte[] data = bundle.getByteArray(SESSION_BUNDLE_SAVE_KEY);
        if (data != null) {
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            try {
                Session session = (Session) (new ObjectInputStream(is)).readObject();
                session.setTokenCachingStrategy(this.tokenCachingStrategy);
                return openSession(session);
            } catch (ClassNotFoundException e) {
                Log.w(TAG, "Unable to restore session", e);
            } catch (IOException e) {
                Log.w(TAG, "Unable to restore session.", e);
            }
        }
        return null;
    }


    public void register(final String email,
                         String firstName,
                         String lastName,
                         final String password) {
        sessionService.addUser(email, firstName, lastName, password, new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse baseResponse, Response response) {
                eventBus.post(new RegisterSuccessEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new RegisterFailEvent(RegisterFailEvent.Reason.UserExists));
            }
        });
    }

    public void loginFACEBOOK(final String email,
                              final String socialId,
                              final String firstName,
                              final String lastName,
                              final String pictureURL) {
        getAccessToken(adminUsername, adminSecret, new Callback<AccessToken>() {
            @Override
            public void success(AccessToken accessToken, Response response) {
                apiService.socialLoginFacebook(authHeader(accessToken), email, socialId, firstName, lastName, pictureURL, new Callback<AccessToken>() {
                    @Override
                    public void success(AccessToken userAccessToken, Response response) {
                        if (userAccessToken.getError() != null && userAccessToken.getError().length() > 0) {
                            eventBus.post(new SessionOpenFailEvent(SessionOpenFailEvent.Reason.Unknown, userAccessToken.getError()));
                        } else {
                            userAccessToken.setLastRefreshDate(new Date());
                            openActiveSessionWithAccessToken(context, userAccessToken);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        eventBus.post(new SessionOpenFailEvent(SessionOpenFailEvent.Reason.Unknown, null));
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new SessionOpenFailEvent(SessionOpenFailEvent.Reason.Unknown, null));
            }
        });
    }

    public void loginGOOGLE(final String email,
                            final String socialId,
                            final String firstName,
                            final String lastName,
                            final String pictureURL) {
        getAccessToken(adminUsername, adminSecret, new Callback<AccessToken>() {
            @Override
            public void success(AccessToken accessToken, Response response) {
                apiService.socialLoginGoogle(authHeader(accessToken), email, socialId, firstName, lastName, pictureURL, new Callback<AccessToken>() {
                    @Override
                    public void success(AccessToken userAccessToken, Response response) {
                        if (userAccessToken.getError() != null && userAccessToken.getError().length() > 0) {
                            eventBus.post(new SessionOpenFailEvent(SessionOpenFailEvent.Reason.Unknown, userAccessToken.getError()));
                        } else {
                            userAccessToken.setLastRefreshDate(new Date());
                            openActiveSessionWithAccessToken(context, userAccessToken);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        eventBus.post(new SessionOpenFailEvent(SessionOpenFailEvent.Reason.Unknown, null));
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new SessionOpenFailEvent(SessionOpenFailEvent.Reason.Unknown, null));
            }
        });
    }

    public void resetPassword(String email) {
        sessionService.resetPassword(email, new Callback<ResetPasswordResponse>() {
            @Override
            public void success(ResetPasswordResponse resetPasswordResponse, Response response) {
                if (!resetPasswordResponse.isError()) {
                    eventBus.post(new ResetPasswordSuccesEvent());
                } else {
                    eventBus.post(new ResetPasswordFailEvent(ResetPasswordFailEvent.Reason.NoUser));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new ResetPasswordFailEvent(ResetPasswordFailEvent.Reason.Unknown));
            }
        });
    }

    public User getActiveUser() {
        synchronized (this.lock) {
            return activeUser;
        }
    }

    public final void setActiveUser(User user) {
        synchronized (this.lock) {
            if (user != this.activeUser) {
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

    public void getActiveUserByEmail(String email) {
        Session session = getActiveSession();
        apiService.userByEmail(authHeader(session), email, new Callback<UserResponse>() {
            @Override
            public void success(UserResponse userResponse, Response response) {
                UserResponse.EmbeddedUserResponse embeddedUserResponse = userResponse.getEmbedded();

                if (embeddedUserResponse != null) {
                    List<User> users = userResponse.getEmbedded().getUsers();
                    if (users != null && users.size() > 0) {
                        User user = users.get(0);
                        setActiveUser(user);
                    } else {
                        eventBus.post(new UserInfoLoadedEvent(null));
                    }
                } else {
                    eventBus.post(new UserInfoLoadedEvent(null));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new RestErrorEvent(error));
            }
        });
    }

    public void getUserByEmail(String email) {
        Session session = getActiveSession();
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

    private SafeAsyncTask<UserListResponse> listUsersTask;
    public void listUsers(final int page, final int perPage) {
        cancelListUsersTask();

        final Session session = getActiveSession();
        SafeAsyncTask<UserListResponse> task = new SafeAsyncTask<UserListResponse>() {
            @Override
            public UserListResponse call() throws Exception {
                return apiService.users(authHeader(session), page, perPage);
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                eventBus.post(new UserListFailEvent((RetrofitError) e, page));
            }

            @Override
            public void onSuccess(UserListResponse userListResponse) {
                UserListResponse.EmbeddedResponse embeddedUserResponse = userListResponse.getEmbeddedResponse();
                PageInfo pageInfo = userListResponse.getPageInfo();

                if (embeddedUserResponse != null) {
                    List<User> users = embeddedUserResponse.getUsers();

                    if (pageInfo == null) {
                        pageInfo = new PageInfo();
                        pageInfo.setNumber(0);
                        pageInfo.setSize(users.size());
                        pageInfo.setTotalPages(1);
                    }

                    eventBus.post(new UserListEvent(users, pageInfo));
                } else {
                    if (pageInfo == null) {
                        pageInfo = new PageInfo();
                        pageInfo.setNumber(0);
                        pageInfo.setSize(0);
                        pageInfo.setTotalPages(0);
                    }

                    eventBus.post(new UserListEvent(null, userListResponse.getPageInfo()));
                }
            }
        };

        task.execute();

        listUsersTask = task;
    }

    public void cancelListUsersTask() {
        if (listUsersTask != null) {
            listUsersTask.cancel(true);
        }

        listUsersTask = null;
    }

    public SafeAsyncTask<UserListResponse> searchUsers(final String query, final int page, final int perPage) {
        final Session session = getActiveSession();
        SafeAsyncTask<UserListResponse> task = new SafeAsyncTask<UserListResponse>() {
            @Override
            public UserListResponse call() throws Exception {
                return apiService.searchUsers(authHeader(session), query, page, perPage);
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                eventBus.post(new UserListFailEvent((RetrofitError) e, page));
            }

            @Override
            public void onSuccess(UserListResponse userListResponse) {
                UserListResponse.EmbeddedResponse embeddedUserResponse = userListResponse.getEmbeddedResponse();
                if (embeddedUserResponse != null) {
                    List<User> users = embeddedUserResponse.getUsers();
                    eventBus.post(new UserListEvent(users, userListResponse.getPageInfo()));
                } else {
                    eventBus.post(new UserListEvent(null, userListResponse.getPageInfo()));
                }
            }
        };

        task.execute();

        return task;
    }

    public void updateUser(final User user, String firstname, String lastname) {
        Session session = getActiveSession();
        apiService.updateUser(authHeader(session), user.getId(), firstname, lastname, new Callback<Boolean>() {
            @Override
            public void success(Boolean success, Response response) {
                if (success) {
                    eventBus.post(new UserInfoUpdateSuccessEvent());

                    getActiveUserByEmail(user.getEmail());
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

    public void changeUserPassword(final User user, String currentPassword, String newPassword) {
        Session session = getActiveSession();
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

    public void deleteUser(final User user) {
        Session session = getActiveSession();
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

    public void getUserNotificationSettings(final User user) {
        Session session = getActiveSession();
        apiService.userNotificationSettings(authHeader(session), user.getId(), new Callback<NotificationSettingsResponse>() {
            @Override
            public void success(NotificationSettingsResponse notificationSettingsResponse, Response response) {
                NotificationSettingsResponse.EmbeddedResponse content = notificationSettingsResponse.getContent();
                if (content != null) {
                    List<NotificationSettings> results = notificationSettingsResponse.getContent().getNotificationSettings();
                    eventBus.post(new NotificationSettingsListSuccessEvent(results.get(0)));
                } else {
                    eventBus.post(new NotificationSettingsListSuccessEvent(new NotificationSettings()));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new NotificationSettingsListFailedEvent(error));
            }
        });
    }

    public void toggleCommentNotifications(boolean status) {
        Session session = getActiveSession();
        apiService.toggleCommentNotifications(authHeader(session), status, new Callback<Boolean>() {
            @Override
            public void success(Boolean success, Response response) {
                if (success) {
                    eventBus.post(new NotificationSettingsUpdateSuccessEvent());
                } else {
                    eventBus.post(new NotificationSettingsUpdateFailEvent());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new NotificationSettingsUpdateFailEvent());
            }
        });
    }

    public void toggleShareNotifications(boolean status) {
        Session session = getActiveSession();
        apiService.toggleShareNotifications(authHeader(session), status, new Callback<Boolean>() {
            @Override
            public void success(Boolean success, Response response) {
                if (success) {
                    eventBus.post(new NotificationSettingsUpdateSuccessEvent());
                } else {
                    eventBus.post(new NotificationSettingsUpdateFailEvent());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new NotificationSettingsUpdateFailEvent());
            }
        });
    }

    public void toggleLikeNotifications(boolean status) {
        Session session = getActiveSession();
        apiService.toggleLikeNotifications(authHeader(session), status, new Callback<Boolean>() {
            @Override
            public void success(Boolean success, Response response) {
                if (success) {
                    eventBus.post(new NotificationSettingsUpdateSuccessEvent());
                } else {
                    eventBus.post(new NotificationSettingsUpdateFailEvent());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new NotificationSettingsUpdateFailEvent());
            }
        });
    }

    public void toggleMessageNotifications(boolean status) {
        Session session = getActiveSession();
        apiService.toggleMessageNotifications(authHeader(session), status, new Callback<Boolean>() {
            @Override
            public void success(Boolean success, Response response) {
                if (success) {
                    eventBus.post(new NotificationSettingsUpdateSuccessEvent());
                } else {
                    eventBus.post(new NotificationSettingsUpdateFailEvent());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new NotificationSettingsUpdateFailEvent());
            }
        });
    }

    // --- Videos --- //

    public void getVideoInfo(long videoID) {
        Session session = getActiveSession();
        apiService.videoInfo(authHeader(session), videoID, new Callback<VideoResponse>() {
            @Override
            public void success(VideoResponse videoResponse, Response response) {
                eventBus.post(new VideoInfoEvent(videoResponse));
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new VideoInfoFailEvent(error));
            }
        });
    }

    private SafeAsyncTask<VideoListResponse> listVideosTask;
    public void listVideos(final int page, final int perPage) {
        cancelListVideosTask();
        cancelSearchVideosTask();

        final Session session = getActiveSession();

        SafeAsyncTask<VideoListResponse> task = new SafeAsyncTask<VideoListResponse>() {
            @Override
            public VideoListResponse call() throws Exception {
                return apiService.listVideos(authHeader(session), page, perPage);
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                eventBus.post(new VideoListFailEvent((RetrofitError) e, page));
            }

            @Override
            public void onSuccess(VideoListResponse videoListResponse) {
                VideoListResponse.EmbeddedResponse embeddedResponse = videoListResponse.getEmbeddedResponse();
                if (embeddedResponse != null) {
                    List<Video> videos = embeddedResponse.getVideos();
                    eventBus.post(new VideoListEvent(videos, videoListResponse.getPageInfo()));
                } else {
                    eventBus.post(new VideoListEvent(null, videoListResponse.getPageInfo()));
                }
            }
        };

        task.execute();

        listVideosTask = task;
    }
    public void cancelListVideosTask() {
        if (listVideosTask != null) {
            listVideosTask.cancel(true);
        }
        listVideosTask = null;
    }

    private SafeAsyncTask<VideoListResponse2> searchVideosTask;
    public void searchVideos(final String query, final int page, final int perPage) {
        cancelListVideosTask();
        cancelSearchVideosTask();

        final Session session = getActiveSession();

        SafeAsyncTask<VideoListResponse2> task = new SafeAsyncTask<VideoListResponse2>() {
            @Override
            public VideoListResponse2 call() throws Exception {
                return apiService.searchVideos(authHeader(session), query, page, perPage);
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                eventBus.post(new VideoListFailEvent((RetrofitError) e, page));
            }

            @Override
            public void onSuccess(VideoListResponse2 videoListResponse) {
                PageInfo pageInfo = new PageInfo();
                pageInfo.setNumber(page);
                pageInfo.setSize(videoListResponse.getContent().size());
                pageInfo.setTotalPages(videoListResponse.getTotal());
                eventBus.post(new VideoListEvent(videoListResponse.getContent(), pageInfo));
            }
        };

        task.execute();

        searchVideosTask = task;
    }

    public void cancelSearchVideosTask() {
        if (searchVideosTask != null) {
            searchVideosTask.cancel(true);
        }
        searchVideosTask = null;
    }

    private SafeAsyncTask<VideoListResponse2> listRelatedVideosTask;
    public void listRelatedVideos(final long videoId, final int page, final int perPage) {
        final Session session = getActiveSession();

        SafeAsyncTask<VideoListResponse2> task = new SafeAsyncTask<VideoListResponse2>() {
            @Override
            public VideoListResponse2 call() throws Exception {
                return apiService.relatedVideos(authHeader(session), videoId, page, perPage, "");
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                eventBus.post(new VideoListFailEvent((RetrofitError) e, page));
            }

            @Override
            public void onSuccess(VideoListResponse2 videoListResponse) {
                PageInfo pageInfo = new PageInfo();
                pageInfo.setNumber(page);
                pageInfo.setSize(videoListResponse.getContent().size());
                pageInfo.setTotalPages(videoListResponse.getTotal());
                eventBus.post(new VideoListEvent(videoListResponse.getContent(), pageInfo));
            }
        };

        task.execute();

        listRelatedVideosTask = task;
    }

    public void cancelListRelatedVideosTask() {
        if (listRelatedVideosTask != null) {
            listRelatedVideosTask.cancel(true);
        }
        listRelatedVideosTask = null;
    }

    private SafeAsyncTask<VideoListResponse> listUserVideosTask;
    public void listVideosForUser(final long userId, final int page, final int perPage) {
        cancelListUserVideosTask();

        final Session session = getActiveSession();

        SafeAsyncTask<VideoListResponse> task = new SafeAsyncTask<VideoListResponse>() {
            @Override
            public VideoListResponse call() throws Exception {
                return apiService.videosForUser(authHeader(session), userId, page, perPage);
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                eventBus.post(new VideoListFailEvent((RetrofitError) e, page));
            }

            @Override
            public void onSuccess(VideoListResponse videoListResponse) {
                VideoListResponse.EmbeddedResponse embeddedResponse = videoListResponse.getEmbeddedResponse();
                if (embeddedResponse != null) {
                    List<Video> videos = embeddedResponse.getVideos();
                    eventBus.post(new VideoListEvent(videos, videoListResponse.getPageInfo()));
                } else {
                    eventBus.post(new VideoListEvent(null, videoListResponse.getPageInfo()));
                }
            }
        };

        task.execute();

        listUserVideosTask = task;
    }

    public void cancelListUserVideosTask() {
        if (listUserVideosTask != null) {
            listUserVideosTask.cancel(true);
        }
        listUserVideosTask = null;
    }

    public void listVideoTags(long videoId) {
        Session session = getActiveSession();
        apiService.tagsForVideo(authHeader(session), videoId, 0, 1000, new Callback<TagListResponse>() {
            @Override
            public void success(TagListResponse tagListResponse, Response response) {
                eventBus.post(tagListResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new TagListResponse());
            }
        });
    }

    private SafeAsyncTask<CommentListResponse> listVideoCommentsTask;
    public void listVideoComments(final long videoId, final int page, final int perPage) {
        cancelListVideoCommentsTask();

        final Session session = getActiveSession();

        SafeAsyncTask<CommentListResponse> task = new SafeAsyncTask<CommentListResponse>() {
            @Override
            public CommentListResponse call() throws Exception {
                return apiService.videoComments(authHeader(session), videoId, page, perPage);
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                eventBus.post(new CommentListFailEvent((RetrofitError) e, page));
            }

            @Override
            public void onSuccess(CommentListResponse commentListResponse) {
                PageInfo pageInfo = new PageInfo();
                pageInfo.setNumber(page);
                pageInfo.setSize(commentListResponse.getContent().size());
                pageInfo.setTotalPages(commentListResponse.getTotal());
                eventBus.post(new CommentListEvent(commentListResponse.getContent(), pageInfo));
            }
        };

        task.execute();

        listVideoCommentsTask = task;
    }

    public void cancelListVideoCommentsTask() {
        if (listVideoCommentsTask != null) {
            listVideoCommentsTask.cancel(true);
        }
        listVideoCommentsTask = null;
    }

    public void addComment(final long videoId, final String content) {
        final Session session = getActiveSession();

        CreateCommentRequest commentRequest = new CreateCommentRequest();
        commentRequest.video = "http://web01.dev.irewind.com/api/rest/video/" + videoId;
        commentRequest.content = content;

        apiService.postVideoComment(authHeader(session), commentRequest, new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse baseResponse, Response response) {
                eventBus.post(new CommentAddEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new CommentAddFailEvent());
            }
        });
    }

    public void replyComment(final long videoId, final String content, final long parentCommentId) {
        final Session session = getActiveSession();

        ReplyCommentRequest commentRequest = new ReplyCommentRequest();
        commentRequest.video = "http://web01.dev.irewind.com/api/rest/video/" + videoId;
        commentRequest.content = content;
        commentRequest.parentVideoComment = "http://web01.dev.irewind.com/api/rest/video-comment/" + parentCommentId;

        apiService.replyVideoComment(authHeader(session), commentRequest, new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse baseResponse, Response response) {
                eventBus.post(new CommentAddEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new CommentAddFailEvent());
            }
        });
    }
}
