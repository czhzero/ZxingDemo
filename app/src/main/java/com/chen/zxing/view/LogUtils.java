package com.chen.zxing.view;

import android.text.TextUtils;

/**
 * Created by chenzhaohua on 17/1/11.
 */
public class LogUtils {

    private final static String TAG = "czh";
    private final static boolean DEBUG = false;

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (DEBUG) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void e(String msg) {
        e(TAG, msg);
    }


    public static void e(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (DEBUG) {
            android.util.Log.e(tag, msg);
        }
    }


}
