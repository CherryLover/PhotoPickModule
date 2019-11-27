package me.iwf.photopicker.utils;

import android.util.Log;

import me.iwf.photopicker.BuildConfig;

/**
 * @description
 * @author: Created jiangjiwei in 2019-10-29 16:45
 */
public class LogUtil {
    private static final String tag = "PhotoPicker";
    private static boolean log = BuildConfig.DEBUG;

    public static void e(String tag, String message, Throwable throwable) {
        if (log) {
            Log.e(tag, message, throwable);
        }
    }

    public static void e(String tag, Throwable throwable) {
        if (log) {
            Log.e(tag, "", throwable);
        }
    }

    public static void e(Throwable throwable) {
        if (log) {
            Log.e(tag, "", throwable);
        }
    }


    public static void e(String tag, String message) {
        if (log) {
            Log.e(tag, message);
        }
    }

    public static void e(Throwable throwable, String message) {
        if (log) {
            Log.e(tag, message, throwable);
        }
    }

    public static void e(String message) {
        if (log) {
            Log.e(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (log) {
            Log.w(tag, message);
        }
    }

    public static void w(String message) {
        if (log) {
            Log.w(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (log) {
            Log.d(tag, message);
        }
    }

    public static void d(String message) {
        if (log) {
            Log.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (log) {
            Log.i(tag, message);
        }
    }

    public static void i(String message) {
        if (log) {
            Log.i(tag, message);
        }
    }
}