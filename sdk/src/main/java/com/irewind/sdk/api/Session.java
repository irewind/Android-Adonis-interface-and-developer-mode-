package com.irewind.sdk.api;

import android.content.Context;
import android.os.Bundle;

import com.irewind.sdk.api.cache.SharedPreferencesTokenCachingStrategy;
import com.irewind.sdk.api.cache.TokenCachingStrategy;
import com.irewind.sdk.model.AccessToken;
import com.irewind.sdk.util.BundleUtil;

import java.io.Serializable;
import java.util.Date;

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

    // Token extension constants
    private static final int TOKEN_EXTEND_THRESHOLD_SECONDS = 24 * 60 * 60; // 1
    // day
    private static final int TOKEN_EXTEND_RETRY_SECONDS = 60 * 60; // 1 hour

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

    public Session(Context context, TokenCachingStrategy tokenCachingStrategy) {
        this(context, tokenCachingStrategy, true);
    }

    public Session(Context context, TokenCachingStrategy tokenCachingStrategy,
                   boolean loadTokenFromCache) {

        if (tokenCachingStrategy == null) {
            tokenCachingStrategy = new SharedPreferencesTokenCachingStrategy(context);
        }

        this.tokenCachingStrategy = tokenCachingStrategy;
        this.state = SessionState.CREATED;

        Bundle tokenState = loadTokenFromCache ? tokenCachingStrategy.load() : null;
        if (TokenCachingStrategy.hasTokenInformation(tokenState)) {
            long expirationTime = tokenState.getLong(TokenCachingStrategy.EXPIRES_IN_KEY);
            Date lastRefreshDate = BundleUtil.getDate(tokenState, TokenCachingStrategy.LAST_REFRESH_DATE_KEY);
            Date now = new Date();

            this.tokenInfo = TokenCachingStrategy.createFromBundle(tokenState);

            if (state.isOpened()
                    && (expirationTime == 0) || now.getTime() - lastRefreshDate.getTime() + expirationTime > TOKEN_EXTEND_RETRY_SECONDS * 1000) {
                // If expired, mark it
                this.state = SessionState.CREATED_TOKEN_LOADED;
            } else {
                // Otherwise we have a valid token, so use it.
                this.state = SessionState.OPENED_TOKEN_EXPIRED;
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

    public AccessToken getTokenInfo() {
        synchronized (this.lock) {
            return tokenInfo;
        }
    }

    public void setTokenInfo(AccessToken tokenInfo) {
        synchronized (this.lock) {
            this.tokenInfo = tokenInfo;
        }
    }

    public Date getLastAttemptedTokenExtendDate() {
        synchronized (this.lock) {
            return lastAttemptedTokenExtendDate;
        }
    }

    public void setLastAttemptedTokenExtendDate(Date lastAttemptedTokenExtendDate) {
        synchronized (this.lock) {
            this.lastAttemptedTokenExtendDate = lastAttemptedTokenExtendDate;
        }
    }

    public void setTokenCachingStrategy(TokenCachingStrategy tokenCachingStrategy) {
        synchronized (this.lock) {
            this.tokenCachingStrategy = tokenCachingStrategy;
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
                this.tokenCachingStrategy.save(TokenCachingStrategy.accessTokenToBundle(accessToken));
            }

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

    public void saveTokenToCache(AccessToken newToken) {
        if (newToken != null && tokenCachingStrategy != null) {
            tokenCachingStrategy.save(TokenCachingStrategy.accessTokenToBundle(newToken));
        }
    }

    public boolean shouldExtendAccessToken() {
        boolean result = false;

        Date now = new Date();

        if (state.isOpened()
                && now.getTime() - lastAttemptedTokenExtendDate.getTime() + getExpirationTime() > TOKEN_EXTEND_RETRY_SECONDS * 1000
                && now.getTime() - tokenInfo.getLastRefreshDate().getTime() > TOKEN_EXTEND_THRESHOLD_SECONDS * 1000) {
            result = true;
        }

        return result;
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

        return areEqual(other.state, state) &&
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
