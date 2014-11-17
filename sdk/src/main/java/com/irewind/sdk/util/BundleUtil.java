package com.irewind.sdk.util;

import android.os.Bundle;

import java.util.Date;

public class BundleUtil {
    private static final long INVALID_BUNDLE_MILLISECONDS = Long.MIN_VALUE;

    public static Date getDate(Bundle bundle, String key) {
        if (bundle == null) {
            return null;
        }

        long n = bundle.getLong(key, INVALID_BUNDLE_MILLISECONDS);
        if (n == INVALID_BUNDLE_MILLISECONDS) {
            return null;
        }

        return new Date(n);
    }

    public static void putDate(Bundle bundle, String key, Date date) {
        bundle.putLong(key, date.getTime());
    }
}
