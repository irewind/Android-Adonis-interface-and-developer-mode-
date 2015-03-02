package com.irewind.sdk.api;

import retrofit.RetrofitError;

public class ErrorUtils {
    public static boolean isUnauthorized(RetrofitError error) {
        if (error.getResponse() == null) {
            return false;
        }
        return error.getResponse().getStatus() == 401;
    }
}
