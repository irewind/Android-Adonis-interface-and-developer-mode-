package com.irewind.sdk.model;

import android.content.*;
import android.os.*;
import android.util.Log;

import com.irewind.sdk.SharedPreferencesTokenCachingStrategy;
import com.irewind.sdk.TokenCachingStrategy;
import com.irewind.sdk.iRewindException;

import java.io.*;
import java.util.*;

/**
 * <p>
 * Session is used to authenticate a user and manage the user's session with
 * iRewind.
 * </p>
 * <p>
 * Sessions must be opened before they can be used to make a Request. When a
 * Session is created, it attempts to initialize itself from a TokenCachingStrategy.
 * Closing the session can optionally clear this cache.  The Session lifecycle
 * uses {@link SessionState SessionState} to indicate its state. Once a Session has
 * been closed, it can't be re-opened; a new Session must be created.
 * </p>
 */
public class Session implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The logging tag used by Session.
     */
    public static final String TAG = Session.class.getCanonicalName();

    /**
     * The action used to indicate that the active session has been set.
     */
    public static final String ACTION_ACTIVE_SESSION_SET = "com.irewind.sdk.ACTIVE_SESSION_SET";

    /**
     * The action used to indicate that the active session has been set to null.
     */
    public static final String ACTION_ACTIVE_SESSION_UNSET = "com.irewind.sdk.ACTIVE_SESSION_UNSET";

    /**
     * The action used to indicate that the active session has been opened.
     */
    public static final String ACTION_ACTIVE_SESSION_OPENED = "com.irewind.sdk.ACTIVE_SESSION_OPENED";

    /**
     * The action used to indicate that the active session has been closed.
     */
    public static final String ACTION_ACTIVE_SESSION_CLOSED = "com.irewind.sdk.ACTIVE_SESSION_CLOSED";

    private static final Object STATIC_LOCK = new Object();
    private static Session activeSession;
    private static volatile Context staticContext;

    // Token extension constants
    private static final int TOKEN_EXTEND_THRESHOLD_SECONDS = 24 * 60 * 60; // 1
    // day
    private static final int TOKEN_EXTEND_RETRY_SECONDS = 60 * 60; // 1 hour

    private static final String SESSION_BUNDLE_SAVE_KEY = "com.irewind.sdk.Session.saveSessionKey";

    private SessionState state;
    private AccessToken tokenInfo;
    private Date lastAttemptedTokenExtendDate = new Date(0);

    // This is the object that synchronizes access to state and tokenInfo
    private final Object lock = new Object();
    private TokenCachingStrategy tokenCachingStrategy;

    private Session(SessionState state,
                    AccessToken tokenInfo, Date lastAttemptedTokenExtendDate) {
        this.state = state;
        this.tokenInfo = tokenInfo;
        this.lastAttemptedTokenExtendDate = lastAttemptedTokenExtendDate;
        tokenCachingStrategy = null;
    }

    /**
     * Initializes a new Session with the specified context.
     *
     * @param currentContext The Activity or Service creating this Session.
     */
    public Session(Context currentContext) {
        this(currentContext, null, true);
    }

    Session(Context context,TokenCachingStrategy tokenCachingStrategy) {
        this(context, tokenCachingStrategy, true);
    }

    Session(Context context, TokenCachingStrategy tokenCachingStrategy,
            boolean loadTokenFromCache) {

        initializeStaticContext(context);

        if (tokenCachingStrategy == null) {
            tokenCachingStrategy = new SharedPreferencesTokenCachingStrategy(staticContext);
        }

        this.tokenCachingStrategy = tokenCachingStrategy;
        this.state = SessionState.CREATED;

        Bundle tokenState = loadTokenFromCache ? tokenCachingStrategy.load() : null;
        if (TokenCachingStrategy.hasTokenInformation(tokenState)) {
            long expirationTime = tokenState.getLong(TokenCachingStrategy.EXPIRES_IN_KEY);
            Date lastRefreshDate = TokenCachingStrategy.getDate(tokenState, TokenCachingStrategy.LAST_REFRESH_DATE_KEY);
            Date now = new Date();

            if (state.isOpened()
                && (expirationTime == 0) || now.getTime() - lastRefreshDate.getTime() + expirationTime > TOKEN_EXTEND_RETRY_SECONDS * 1000) {
                // If expired clear out the
                // current token cache.
                tokenCachingStrategy.clear();
                this.tokenInfo = AccessToken.createEmptyToken();
            } else {
                // Otherwise we have a valid token, so use it.
                this.tokenInfo = AccessToken.createFromBundle(tokenState);
                this.state = SessionState.CREATED_TOKEN_LOADED;
            }
        } else {
            this.tokenInfo = AccessToken.createEmptyToken();
        }
    }

    /**
     * Returns a boolean indicating whether the session is opened.
     *
     * @return a boolean indicating whether the session is opened.
     */
    public final boolean isOpened() {
        synchronized (this.lock) {
            return this.state.isOpened();
        }
    }

    public final boolean isClosed() {
        synchronized (this.lock) {
            return this.state.isClosed();
        }
    }

    /**
     * Returns the current state of the Session.
     * See {@link SessionState} for details.
     *
     * @return the current state of the Session.
     */
    public final SessionState getState() {
        synchronized (this.lock) {
            return this.state;
        }
    }

    /**
     * Returns the access token String.
     *
     * @return the access token String, or null if there is no access token
     */
    public final String getAccessToken() {
        synchronized (this.lock) {
            return (this.tokenInfo == null) ? null : this.tokenInfo.getCurrentToken();
        }
    }

    /**
     * <p>
     * Returns the time at which the current token will expire.
     * </p>
     * <p>
     * Note that Session automatically attempts to extend the lifetime of Tokens
     * as needed when iRewind requests are made.
     * </p>
     *
     * @return the time at which the current token will expire, or 0 if there is no access token
     */
    public final long getExpirationTime() {
        synchronized (this.lock) {
            return (this.tokenInfo == null) ? 0 : this.tokenInfo.getExpiresIn();
        }
    }

    /**
     * Opens a session based on an existing iRewind access token. This method should be used
     * only in instances where an application has previously obtained an access token and wishes
     * to import it into the Session/TokenCachingStrategy-based session-management system. An
     * example would be an application which previously did not use the iRewind SDK for Android
     * and implemented its own session-management scheme, but wishes to implement an upgrade path
     * for existing users so they do not need to log in again when upgrading to a version of
     * the app that uses the SDK.
     * <p/>
     * No validation is done that the token, token source, or permissions are actually valid.
     * It is the caller's responsibility to ensure that these accurately reflect the state of
     * the token that has been passed in, or calls to the iRewind API may fail.
     *
     * @param accessToken the access token obtained from iRewind
     */
    public final void open(AccessToken accessToken) {
        synchronized (this.lock) {
            if (state.isClosed()) {
                throw new UnsupportedOperationException(
                        "Session: an attempt was made to open a previously-closed session.");
            } else if (state != SessionState.CREATED && state != SessionState.CREATED_TOKEN_LOADED) {
                throw new UnsupportedOperationException(
                        "Session: an attempt was made to open an already opened session.");
            }

            this.tokenInfo = accessToken;

            if (this.tokenCachingStrategy != null) {
                this.tokenCachingStrategy.save(accessToken.toBundle());
            }

            final SessionState oldState = state;
            state = SessionState.OPENED;
        }
    }

    /**
     * Closes the local in-memory Session object, but does not clear the
     * persisted token cache.
     */
    public final void close() {
        synchronized (this.lock) {
            switch (this.state) {
                case CREATED:
                case OPENING:
                    this.state = SessionState.CLOSED_LOGIN_FAILED;
                    break;

                case CREATED_TOKEN_LOADED:
                case OPENED:
                case OPENED_TOKEN_UPDATED:
                    this.state = SessionState.CLOSED;
                    break;

                case CLOSED:
                case CLOSED_LOGIN_FAILED:
                    break;
            }
        }
    }

    /**
     * Closes the local in-memory Session object and clears any persisted token
     * cache related to the Session.
     */
    public final void closeAndClearTokenInformation() {
        if (this.tokenCachingStrategy != null) {
            this.tokenCachingStrategy.clear();
        }
        close();
    }

    void extendTokenCompleted(Bundle bundle) {
        synchronized (this.lock) {
            final SessionState oldState = this.state;

            switch (this.state) {
                case OPENED:
                    this.state = SessionState.OPENED_TOKEN_UPDATED;
                    break;
                case OPENED_TOKEN_UPDATED:
                    break;
                default:
                    // Silently ignore attempts to refresh token if we are not open
                    Log.d(TAG, "refreshToken ignored in state " + this.state);
                    return;
            }
            this.tokenInfo = AccessToken.createFromBundle(bundle);
            if (this.tokenCachingStrategy != null) {
                this.tokenCachingStrategy.save(this.tokenInfo.toBundle());
            }
        }
    }

    /**
     * Save the Session object into the supplied Bundle. This method is intended to be called from an
     * Activity or Fragment's onSaveInstanceState method in order to preserve Sessions across Activity lifecycle events.
     *
     * @param session the Session to save
     * @param bundle  the Bundle to save the Session to
     */
    public static final void saveSession(Session session, Bundle bundle) {
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
     * @param context         the Activity or Service creating the Session, must not be null
     * @param cachingStrategy the TokenCachingStrategy to use to load and store the token. If this is
     *                        null, a default token cachingStrategy that stores data in
     *                        SharedPreferences will be used
     * @param bundle          the bundle to restore the Session from
     * @return the restored Session, or null
     */
    public static final Session restoreSession(
            Context context, TokenCachingStrategy cachingStrategy, Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        byte[] data = bundle.getByteArray(SESSION_BUNDLE_SAVE_KEY);
        if (data != null) {
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            try {
                Session session = (Session) (new ObjectInputStream(is)).readObject();
                initializeStaticContext(context);
                if (cachingStrategy != null) {
                    session.tokenCachingStrategy = cachingStrategy;
                } else {
                    session.tokenCachingStrategy = new SharedPreferencesTokenCachingStrategy(context);
                }
                return session;
            } catch (ClassNotFoundException e) {
                Log.w(TAG, "Unable to restore session", e);
            } catch (IOException e) {
                Log.w(TAG, "Unable to restore session.", e);
            }
        }
        return null;
    }


    /**
     * Returns the current active Session, or null if there is none.
     *
     * @return the current active Session, or null if there is none.
     */
    public static final Session getActiveSession() {
        synchronized (Session.STATIC_LOCK) {
            return Session.activeSession;
        }
    }

    /**
     * <p>
     * Sets the current active Session.
     * </p>
     * <p>
     * The active Session is used implicitly by predefined Request factory
     * methods as well as optionally by UI controls in the sdk.
     * </p>
     * <p>
     * It is legal to set this to null, or to a Session that is not yet open.
     * </p>
     *
     * @param session A Session to use as the active Session, or null to indicate
     *                that there is no active Session.
     */
    public static final void setActiveSession(Session session) {
        synchronized (Session.STATIC_LOCK) {
            if (session != Session.activeSession) {
                Session oldSession = Session.activeSession;

                if (oldSession != null) {
                    oldSession.close();
                }

                Session.activeSession = session;
            }
        }
    }

    /**
     * If a cached token is available, creates and opens the session and makes it active without any user interaction,
     * otherwise this does nothing.
     *
     * @param context The Context creating this session
     * @return The new session or null if one could not be created
     */
    public static Session openActiveSessionFromCache(Context context) {
        return openActiveSession(context);
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
     * @param context     the Context to use for creation the session
     * @param accessToken the access token obtained from iRewind
     * @return The new Session or null if one could not be created
     */
    public static Session openActiveSessionWithAccessToken(Context context, AccessToken accessToken) {
        Session session = new Session(context, null, false);

        setActiveSession(session);

        return session;
    }

    private static Session openActiveSession(Context context) {
        Session session = new Builder(context).build();
        if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState())) {
            setActiveSession(session);
            return session;
        }
        return null;
    }

    static Context getStaticContext() {
        return staticContext;
    }

    static void initializeStaticContext(Context currentContext) {
        if ((currentContext != null) && (staticContext == null)) {
            Context applicationContext = currentContext.getApplicationContext();
            staticContext = (applicationContext != null) ? applicationContext : currentContext;
        }
    }

    private void saveTokenToCache(AccessToken newToken) {
        if (newToken != null && tokenCachingStrategy != null) {
            tokenCachingStrategy.save(newToken.toBundle());
        }
    }

    boolean shouldExtendAccessToken() {
        boolean result = false;

        Date now = new Date();

        if (state.isOpened()
                && now.getTime() - lastAttemptedTokenExtendDate.getTime() + getExpirationTime() > TOKEN_EXTEND_RETRY_SECONDS * 1000
                && now.getTime() - tokenInfo.getLastRefreshDate().getTime() > TOKEN_EXTEND_THRESHOLD_SECONDS * 1000) {
            result = true;
        }

        return result;
    }

    AccessToken getTokenInfo() {
        return tokenInfo;
    }

    void setTokenInfo(AccessToken tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    Date getLastAttemptedTokenExtendDate() {
        return lastAttemptedTokenExtendDate;
    }

    void setLastAttemptedTokenExtendDate(Date lastAttemptedTokenExtendDate) {
        this.lastAttemptedTokenExtendDate = lastAttemptedTokenExtendDate;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof Session)) {
            return false;
        }
        Session other = (Session) otherObj;

        return  areEqual(other.state, state) &&
                areEqual(other.getExpirationTime(), getExpirationTime());
    }

    private static boolean areEqual(Object a, Object b) {
        if (a == null) {
            return b == null;
        } else {
            return a.equals(b);
        }
    }

    /**
     * Builder class used to create a Session.
     */
    public static final class Builder {
        private final Context context;
        private TokenCachingStrategy tokenCachingStrategy;

        /**
         * Constructs a new Builder associated with the context.
         *
         * @param context the Activity or Service starting the Session
         */
        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Sets the TokenCachingStrategy for the Session.
         *
         * @param tokenCachingStrategy the token cache to use
         * @return the Builder instance
         */
        public Builder setTokenCachingStrategy(final TokenCachingStrategy tokenCachingStrategy) {
            this.tokenCachingStrategy = tokenCachingStrategy;
            return this;
        }

        /**
         * Build the Session.
         *
         * @return a new Session
         */
        public Session build() {
            return new Session(context, tokenCachingStrategy);
        }
    }
}
