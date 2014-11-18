package com.irewind.sdk.api.cache;

import android.content.Context;
import android.os.Bundle;

public class SharedPreferencesUserCachingStrategy extends UserCachingStrategy {
    private static final String DEFAULT_USER_CACHE_KEY = "com.irewind.SharedPreferencesUserCachingStrategy.DefaultUserCache";

    private SharedPreferencesCache cache;

    public SharedPreferencesUserCachingStrategy(Context context) {
        this(context, null);
    }

    public SharedPreferencesUserCachingStrategy(Context context, String cacheKey) {
        if (context == null) {
            return;
        }
        cacheKey = (cacheKey != null && cacheKey.length() > 0) ? DEFAULT_USER_CACHE_KEY : cacheKey;
        this.cache = new SharedPreferencesCache(context, cacheKey);
    }

    public Bundle load() {
        return cache.load();
    }

    public void save(Bundle bundle) {
        cache.save(bundle);
    }

    public void clear() {
        cache.clear();
    }
}
