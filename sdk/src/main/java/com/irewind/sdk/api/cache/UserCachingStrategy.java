package com.irewind.sdk.api.cache;

import android.os.Bundle;

import com.irewind.sdk.model.User;

public abstract class UserCachingStrategy {
    public static final String USER_ID_KEY = "com.irewind.UserCachingStrategy.UserId";
    public static final String USER_VPS_ID_KEY = "com.irewind.UserCachingStrategy.UserVpsId";
    public static final String EMAIL_KEY = "com.irewind.UserCachingStrategy.Email";
    public static final String FIRSTNAME_KEY = "com.irewind.UserCachingStrategy.Firstname";
    public static final String LASTNAME_KEY = "com.irewind.TokenCachingStrategy.Lastname";
    public static final String FULLNAME_KEY = "com.irewind.UserCachingStrategy.Fullname";
    public static final String CREATED_DATE_KEY = "com.irewind.UserCachingStrategy.CreatedDate";
    public static final String AUTHPROVIDER_KEY = "com.irewind.UserCachingStrategy.Status";
    public static final String STATUS_KEY = "com.irewind.UserCachingStrategy.Status";
    public static final String ROLE_KEY = "com.irewind.UserCachingStrategy.Role";
    public static final String PICTURE_KEY = "com.irewind.UserCachingStrategy.Picture";
    public static final String LAST_LOGIN_DATE_KEY = "com.irewind.UserCachingStrategy.LastLoginDate";

    public abstract Bundle load();

    public abstract void save(Bundle bundle);

    public abstract void clear();

    public static User createFromBundle(Bundle bundle) {
        return new User(
                bundle.getLong(UserCachingStrategy.USER_ID_KEY),
                bundle.getLong(UserCachingStrategy.USER_VPS_ID_KEY),
                bundle.getString(UserCachingStrategy.EMAIL_KEY),
                bundle.getString(UserCachingStrategy.FIRSTNAME_KEY),
                bundle.getString(UserCachingStrategy.LASTNAME_KEY),
                bundle.getString(UserCachingStrategy.FULLNAME_KEY),
                //   bundle.getLong(UserCachingStrategy.CREATED_DATE_KEY),
                bundle.getString(UserCachingStrategy.CREATED_DATE_KEY),
                bundle.getString(UserCachingStrategy.STATUS_KEY),
                bundle.getString(UserCachingStrategy.AUTHPROVIDER_KEY),
                bundle.getString(UserCachingStrategy.ROLE_KEY),
                bundle.getString(UserCachingStrategy.PICTURE_KEY),
                bundle.getString(UserCachingStrategy.LAST_LOGIN_DATE_KEY)
        );
    }

    public static Bundle userToBundle(User user) {
        Bundle bundle = new Bundle();

        bundle.putLong(UserCachingStrategy.USER_ID_KEY, user.getId());
        bundle.putLong(UserCachingStrategy.USER_VPS_ID_KEY, user.getVpsId());
        bundle.putString(UserCachingStrategy.EMAIL_KEY, user.getEmail());
        bundle.putString(UserCachingStrategy.FIRSTNAME_KEY, user.getFirstname());
        bundle.putString(UserCachingStrategy.LASTNAME_KEY, user.getLastname());
        bundle.putString(UserCachingStrategy.FULLNAME_KEY, user.getFullname());
        //   bundle.putLong(UserCachingStrategy.CREATED_DATE_KEY, user.getCreatedDate());
        bundle.putString(UserCachingStrategy.CREATED_DATE_KEY, user.getCreatedDate());
        bundle.putString(UserCachingStrategy.STATUS_KEY, user.getStatus());
        bundle.putString(UserCachingStrategy.AUTHPROVIDER_KEY, user.getAuthProvider());
        bundle.putString(UserCachingStrategy.ROLE_KEY, user.getRole());
        bundle.putString(UserCachingStrategy.PICTURE_KEY, user.getPicture());
        bundle.putString(UserCachingStrategy.LAST_LOGIN_DATE_KEY, user.getLastLoginDate());

        return bundle;
    }
}
