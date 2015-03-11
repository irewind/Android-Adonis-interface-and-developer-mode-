package com.irewind.sdk.util;

import android.util.Log;

/**
 * Created by mpopa on 12/12/13.
 */
public class MainLog {

    private static boolean isDebugging = false;

    public static void logMsg(String tag, String message) {
        if (isDebugging) {
            Log.d(tag, "" + message);

        }
    }

    public static void logMsg(String tag, int message) {
        if (isDebugging) {
            Log.d(tag, "" + message);
        }
    }

    public static void logMsg(String tag, boolean message) {
        if (isDebugging) {
            Log.d(tag, "" + message);
        }
    }

    public static void logMsg(String tag, char message) {
        if (isDebugging) {
            Log.d(tag, "" + message);
        }
    }

    public static void logMsg(String tag, long message) {
        if (isDebugging) {
            Log.d(tag, "" + message);
        }
    }
}
