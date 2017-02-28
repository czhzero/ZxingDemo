package com.chen.zxing.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by chenzhaohua on 16/4/26.
 */
public class ToastUtils {

    private static Toast toast;

    /**
     * 显示Toast
     *
     * @param context
     * @param message
     */
    public static void showToast(Context context, String message) {
        showToast(context, message, Gravity.BOTTOM);
    }

    /**
     * 显示Toast
     *
     * @param context
     * @param message
     * @param gravity
     */
    public static void showToast(Context context, String message, int gravity) {
        if (context == null || TextUtils.isEmpty(message)) {
            return;
        }

        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }

        toast.setGravity(gravity, 0, 0);
        toast.show();
    }

}
