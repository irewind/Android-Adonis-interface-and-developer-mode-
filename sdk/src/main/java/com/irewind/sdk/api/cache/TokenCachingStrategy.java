package com.irewind.sdk.api.cache;

import android.os.Bundle;

import com.irewind.sdk.model.AccessToken;
import com.irewind.sdk.util.BundleUtil;

import java.util.Date;

/**
 * <p>
 * A base class for implementations of a {@link com.irewind.sdk.api.Session Session} token cache.
 * </p>
 * <p>
 * The Session constructor optionally takes a TokenCachingStrategy, from which it will
 * attempt to load a cached token during construction. Also, whenever the
 * Session updates its token, it will also save the token and associated state
 * to the TokenCachingStrategy.
 * </p>
 * <p>
 * This is the only mechanism supported for an Android service to use Session.
 * The service can create a custom TokenCachingStrategy that returns the Session provided
 * by an Activity through which the user logged in to iRewind.
 * </p>
 */
public abstract class TokenCachingStrategy {
    /**
     * The key used by Session to store the token value in the Bundle during
     * load and save.
     */
    public static final String CURRENT_TOKEN_KEY = "com.irewind.TokenCachingStrategy.CurrentToken";

    /**
     * The key used by Session to store the refresh token value in the Bundle during
     * load and save.
     */
    public static final String REFRESH_TOKEN_KEY = "com.irewind.TokenCachingStrategy.RefreshToken";

    /**
     * The key used by Session to store the expiration time value in the Bundle
     * during load and save.
     */
    public static final String EXPIRES_IN_KEY = "com.irewind.TokenCachingStrategy.ExpiresIn";

    /**
     * The key used by Session to store the type value in the Bundle
     * during load and save.
     */
    public static final String TYPE_KEY = "com.irewind.TokenCachingStrategy.Type";

    /**
     * The key used by Session to store the scope value in the Bundle
     * during load and save.
     */
    public static final String SCOPE_KEY = "com.irewind.TokenCachingStrategy.Scope";

    /*
    * The key used by Session to store the last refresh date value in the
    * Bundle during load and save.
    */
    public static final String LAST_REFRESH_DATE_KEY = "com.irewind.TokenCachingStrategy.LastRefreshDate";


    /**
     * Called during Session construction to get the token state. Typically this
     * is loaded from a persistent store that was previously initialized via
     * save.  The caller may choose to keep a reference to the returned Bundle
     * indefinitely.  Therefore the TokenCachingStrategy should not store the returned Bundle
     * and should return a new Bundle on every call to this method.
     *
     * @return A Bundle that represents the token state that was loaded.
     */
    public abstract Bundle load();

    /**
     * Called when a Session updates its token. This is passed a Bundle of
     * values that should be stored durably for the purpose of being returned
     * from a later call to load.  Some implementations may choose to store
     * bundle beyond the scope of this call, so the caller should keep no
     * references to the bundle to ensure that it is not modified later.
     *
     * @param bundle A Bundle that represents the token state to be saved.
     */
    public abstract void save(Bundle bundle);

    /**
     * Called when a Session learns its token is no longer valid or during a
     * call to {@link com.irewind.sdk.api.Session#closeAndClearTokenInformation
     * closeAndClearTokenInformation} to clear the durable state associated with
     * the token.
     */
    public abstract void clear();

    /**
     * Returns a boolean indicating whether a Bundle contains properties that
     * could be a valid saved token.
     *
     * @param bundle A Bundle to check for token information.
     * @return a boolean indicating whether a Bundle contains properties that
     * could be a valid saved token.
     */
    public static boolean hasTokenInformation(Bundle bundle) {
        if (bundle == null) {
            return false;
        }

        String token = bundle.getString(CURRENT_TOKEN_KEY);
        if ((token == null) || (token.length() == 0)) {
            return false;
        }

        String refreshToken = bundle.getString(REFRESH_TOKEN_KEY);
        if ((refreshToken == null) || (refreshToken.length() == 0)) {
            return false;
        }

        long expiresMilliseconds = bundle.getLong(EXPIRES_IN_KEY, 0L);
        if (expiresMilliseconds == 0L) {
            return false;
        }

        return true;
    }

    /**
     * Gets the cached token value from a Bundle.
     *
     * @param bundle A Bundle in which the token value was stored.
     * @return the cached token value, or null.
     * @throws NullPointerException if the passed in Bundle is null
     */
    public static String getCurrentToken(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        return bundle.getString(CURRENT_TOKEN_KEY);
    }

    /**
     * Puts the token value into a Bundle.
     *
     * @param bundle A Bundle in which the token value should be stored.
     * @param value  The String representing the token value, or null.
     * @throws NullPointerException if the passed in Bundle or token value are null
     */
    public static void putCurrentToken(Bundle bundle, String value) {
        if (bundle == null || value == null) {
            return;
        }
        bundle.putString(CURRENT_TOKEN_KEY, value);
    }

    /**
     * Gets the cached expiration time from a Bundle.
     *
     * @param bundle A Bundle in which the expiration time was stored.
     * @return the cached expiration time, or null.
     */
    public static long getExpiresIn(Bundle bundle) {
        if (bundle == null) {
            return 0;
        }
        return bundle.getLong(EXPIRES_IN_KEY);
    }

    /**
     * Puts the expiration time into a Bundle.
     *
     * @param bundle A Bundle in which the expiration time should be stored.
     * @param value  The expiration time.
     */
    public static void putExpiresIn(Bundle bundle, long value) {
        if (bundle == null) {
            return;
        }
        bundle.putLong(EXPIRES_IN_KEY, value);
    }

    /**
     * Gets the cached last refresh date from a Bundle.
     *
     * @param bundle A Bundle in which the last refresh date was stored.
     * @return the cached last refresh Date, or null.
     * @throws NullPointerException if the passed in Bundle is null
     */
    public static Date getLastRefreshDate(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        return BundleUtil.getDate(bundle, LAST_REFRESH_DATE_KEY);
    }

    /**
     * Puts the last refresh date into a Bundle.
     *
     * @param bundle A Bundle in which the last refresh date should be stored.
     * @param value  The Date representing the last refresh date, or null.
     * @throws NullPointerException if the passed in Bundle or date value are null
     */
    public static void putLastRefreshDate(Bundle bundle, Date value) {
        if (bundle == null || value == null) {
            return;
        }
        BundleUtil.putDate(bundle, LAST_REFRESH_DATE_KEY, value);
    }

    /**
     * Gets the cached last refresh date from a Bundle.
     *
     * @param bundle A Bundle in which the last refresh date was stored.
     * @return the cached last refresh date in milliseconds since the epoch.
     * @throws NullPointerException if the passed in Bundle is null
     */
    public static long getLastRefreshMilliseconds(Bundle bundle) {
        if (bundle == null) {
            return 0;
        }
        return bundle.getLong(LAST_REFRESH_DATE_KEY);
    }

    /**
     * Puts the last refresh date into a Bundle.
     *
     * @param bundle A Bundle in which the last refresh date should be stored.
     * @param value  The long representing the last refresh date in milliseconds
     *               since the epoch.
     * @throws NullPointerException if the passed in Bundle is null
     */
    public static void putLastRefreshMilliseconds(Bundle bundle, long value) {
        if (bundle == null) {
            return;
        }
        bundle.putLong(LAST_REFRESH_DATE_KEY, value);
    }

    public static AccessToken createFromBundle(Bundle bundle) {
        return new AccessToken(
                bundle.getString(TokenCachingStrategy.TYPE_KEY),
                bundle.getString(TokenCachingStrategy.CURRENT_TOKEN_KEY),
                bundle.getString(TokenCachingStrategy.REFRESH_TOKEN_KEY),
                bundle.getString(TokenCachingStrategy.SCOPE_KEY),
                bundle.getLong(TokenCachingStrategy.EXPIRES_IN_KEY),
                BundleUtil.getDate(bundle, TokenCachingStrategy.LAST_REFRESH_DATE_KEY)
        );
    }

    public static Bundle accessTokenToBundle(AccessToken accessToken) {
        Bundle bundle = new Bundle();
        bundle.putString(TokenCachingStrategy.TYPE_KEY, accessToken.getTokenType());
        bundle.putString(TokenCachingStrategy.CURRENT_TOKEN_KEY, accessToken.getCurrentToken());
        bundle.putString(TokenCachingStrategy.REFRESH_TOKEN_KEY, accessToken.getRefreshToken());
        bundle.putString(TokenCachingStrategy.SCOPE_KEY, accessToken.getScope());
        bundle.putLong(TokenCachingStrategy.EXPIRES_IN_KEY, accessToken.getExpiresIn());
        BundleUtil.putDate(bundle, TokenCachingStrategy.LAST_REFRESH_DATE_KEY, accessToken.getLastRefreshDate());
        return bundle;
    }
}
