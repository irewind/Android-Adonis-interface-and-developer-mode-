package com.irewind.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tzmst on 11/12/2014.
 */
public class CheckUtil {
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    public static boolean isPasswordValid(String password, String confirm) {
        return password.equals(confirm);
    }
}
