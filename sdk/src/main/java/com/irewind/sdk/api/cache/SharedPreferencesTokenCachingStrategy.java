package com.irewind.sdk.api.cache;

import android.content.Context;
import android.os.Bundle;

/*
 * <p>
 * An implementation of {@link TokenCachingStrategy TokenCachingStrategy} that uses Android SharedPreferences
 * to persist information.
 * </p>
 * <p>
 * The data to be cached is passed in via a Bundle. Only non-null key-value-pairs where
 * the value is one of the following types (or an array of the same) are persisted:
 * boolean, byte, int, long, float, double, char. In addition, String and List<String>
 * are also supported.
 * </p>
 */
public class SharedPreferencesTokenCachingStrategy extends TokenCachingStrategy {

    private static final String DEFAULT_TOKEN_CACHE_KEY = "com.irewind.SharedPreferencesTokenCachingStrategy.DefaultTokenCache";

    private SharedPreferencesCache cache;

    /**
     * Creates a default {@link SharedPreferencesTokenCachingStrategy SharedPreferencesTokenCachingStrategy}
     * instance that provides access to a single set of token information.
     *
     * @param context The Context object to use to get the SharedPreferences object.
     * @throws NullPointerException if the passed in Context is null
     */
    public SharedPreferencesTokenCachingStrategy(Context context) {
        this(context, null);
    }

    /**
     * Creates a {@link SharedPreferencesTokenCachingStrategy SharedPreferencesTokenCachingStrategy} instance
     * that is distinct for the passed in cacheKey.
     *
     * @param context  The Context object to use to get the SharedPreferences object.
     * @param cacheKey Identifies a distinct set of token information.
     * @throws NullPointerException if the passed in Context is null
     */
    public SharedPreferencesTokenCachingStrategy(Context context, String cacheKey) {
        if (context == null) {
            return;
        }
        cacheKey = (cacheKey != null && cacheKey.length() > 0) ? DEFAULT_TOKEN_CACHE_KEY : cacheKey;
        this.cache = new SharedPreferencesCache(context, cacheKey);
    }

    /**
     * Returns a Bundle that contains the information stored in this cache
     *
     * @return A Bundle with the information contained in this cache
     */
    public Bundle load() {
        return cache.load();
    }

    /**
     * Persists all supported data types present in the passed in Bundle, to the
     * cache
     *
     * @param bundle The Bundle containing information to be cached
     */
    public void save(Bundle bundle) {
        cache.save(bundle);
    }

    /**
     * Clears out all token information stored in this cache.
     */
    public void clear() {
        cache.clear();
    }
}
