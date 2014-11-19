package com.irewind.sdk.api;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.irewind.sdk.api.cache.SharedPreferencesTokenCachingStrategy;
import com.irewind.sdk.api.cache.TokenCachingStrategy;
import com.irewind.sdk.api.event.AdminAccessTokenFailEvent;
import com.irewind.sdk.api.event.AdminAccessTokenSuccessEvent;
import com.irewind.sdk.api.event.RegisterFailEvent;
import com.irewind.sdk.api.event.RegisterSuccessEvent;
import com.irewind.sdk.api.event.ResetPasswordFailEvent;
import com.irewind.sdk.api.event.ResetPasswordSuccesEvent;
import com.irewind.sdk.api.event.SessionClosedEvent;
import com.irewind.sdk.api.event.SessionOpenFailEvent;
import com.irewind.sdk.api.event.SessionOpenedEvent;
import com.irewind.sdk.api.event.SocialLoginFailEvent;
import com.irewind.sdk.iRewindConfig;
import com.irewind.sdk.iRewindException;
import com.irewind.sdk.model.AccessToken;
import com.irewind.sdk.api.response.BaseResponse;
import com.irewind.sdk.api.response.ResetPasswordResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SessionClient implements SessionRefresher {

    /**
     * The logging tag used by SessionClient.
     */
    public static final String TAG = SessionClient.class.getCanonicalName();

    private static final String SESSION_BUNDLE_SAVE_KEY = "com.irewind.sdk.Session.saveSessionKey";

    private Context context;
    private EventBus eventBus;
    private Session activeSession;

    private final Object lock = new Object();

    private iRewindConfig config;

    private TokenCachingStrategy tokenCachingStrategy;

    private SessionService sessionService;

    public SessionClient(Context context, iRewindConfig config) {
        this(context, config, null, null);
    }

    public SessionClient(Context context, iRewindConfig config, TokenCachingStrategy tokenCachingStrategy) {
        this(context, config, tokenCachingStrategy, null);
    }

    public SessionClient(Context context, iRewindConfig config, EventBus eventBus) {
        this(context, config, null, eventBus);
    }

    public SessionClient(Context context, iRewindConfig config, TokenCachingStrategy tokenCachingStrategy, EventBus eventBus) {
        this.context = context;
        this.config = config;
        this.eventBus = eventBus;
        if (eventBus == null) {
            this.eventBus = new EventBus();
        }
        this.tokenCachingStrategy = tokenCachingStrategy;
        if (tokenCachingStrategy == null) {
            this.tokenCachingStrategy = new SharedPreferencesTokenCachingStrategy(context);
        }

        sessionService = ServiceFactory.createSessionService(config.getBaseURL(), config.getClientID(), config.getClientSecret());

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
                session.setSessionRefresher(this);
            }
        }
    }

    private static final String adminUsername = "tremend@mailinator.com";
    private static final String adminSecret = "tremend.admin";
    public void getAdminAccessToken() {
        sessionService.getAccessToken(adminUsername, adminSecret, new Callback<AccessToken>() {
            @Override
            public void success(AccessToken accessToken, Response response) {
                eventBus.post(new AdminAccessTokenSuccessEvent(accessToken));
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new AdminAccessTokenFailEvent(AdminAccessTokenFailEvent.Reason.BadCredentials));
            }
        });
    }

    private void openAdminSession() {
        openSession(adminUsername, adminSecret);
    }

    public void openSession(String username, String password) {
        sessionService.getAccessToken(username, password, new Callback<AccessToken>() {
            @Override
            public void success(AccessToken accessToken, Response response) {
                accessToken.setLastRefreshDate(new Date());
                openActiveSessionWithAccessToken(context, accessToken);
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new SessionOpenFailEvent(SessionOpenFailEvent.Reason.BadCredentials));
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
                eventBus.post(new SessionOpenFailEvent(SessionOpenFailEvent.Reason.BadCredentials));
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

    public void loginFACEBOOK(String email,
                              String socialId,
                              String firstName,
                              String lastName,
                              String pictureURL) {
        sessionService.socialLoginFacebook(email, socialId, firstName, lastName, pictureURL, new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse baseResponse, Response response) {
                openAdminSession();
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new SocialLoginFailEvent(SocialLoginFailEvent.Reason.Unknown));
            }
        });
    }

    public void loginGOOGLE(String email,
                            String socialId,
                            String firstName,
                            String lastName,
                            String pictureURL) {
        sessionService.socialLoginGoogle(email, socialId, firstName, lastName, pictureURL, new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse baseResponse, Response response) {
                openAdminSession();
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new SocialLoginFailEvent(SocialLoginFailEvent.Reason.Unknown));
            }
        });
    }

    public void resetPassword(String email) {
        sessionService.resetPassword(email, new Callback<ResetPasswordResponse>() {
            @Override
            public void success(ResetPasswordResponse resetPasswordResponse, Response response) {
                if (!resetPasswordResponse.isError()) {
                    eventBus.post(new ResetPasswordSuccesEvent());
                }
                else  {
                    eventBus.post(new ResetPasswordFailEvent(ResetPasswordFailEvent.Reason.NoUser));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                eventBus.post(new ResetPasswordFailEvent(ResetPasswordFailEvent.Reason.Unknown));
            }
        });
    }
}
