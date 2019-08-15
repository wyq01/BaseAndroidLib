package com.wyq.base.util;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wyq.base.R;

/**
 * Created by wyq
 * Date: 2018/9/19
 */
public class ToastUtil {
    private static Toast mToast;

    public static void shortToast(Context context, @StringRes int msgId) {
        String msg = context.getResources().getString(msgId);
        shortToast(context, msg);
    }

    public static void shortToast(Context context, @NonNull String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (mToast != null)
            mToast.cancel();
        mToast = new Toast(context);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        View view = LayoutInflater.from(context).inflate(R.layout.base_layout_toast, null);
        TextView msgTv = (TextView) view.findViewById(R.id.msgTv);
        msgTv.setText(msg);
        mToast.setView(view);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static void shortToastWithIcon(Context context, @StringRes int msgId) {
        String msg = context.getResources().getString(msgId);
        shortToastWithIcon(context, msg, 0);
    }

    public static void shortToastWithIcon(Context context, @NonNull String msg) {
        shortToastWithIcon(context, msg, 0);
    }

    public static void shortToastWithIcon(Context context, @StringRes int msgId, @DrawableRes int imgId) {
        String msg = context.getResources().getString(msgId);
        shortToastWithIcon(context, msg, imgId);
    }

    public static void shortToastWithIcon(Context context, @NonNull String msg, @DrawableRes int imgId) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (mToast != null)
            mToast.cancel();
        mToast = new Toast(context);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        View view = LayoutInflater.from(context).inflate(R.layout.base_layout_toast_with_icon, null);
        TextView msgTv = (TextView) view.findViewById(R.id.msgTv);
        msgTv.setText(msg);
        if (imgId != 0) {
            ImageView iv = (ImageView) view.findViewById(R.id.iv);
            iv.setImageResource(imgId);
        }
        mToast.setView(view);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

}