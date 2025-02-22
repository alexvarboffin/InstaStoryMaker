package com.maxvidzgallery.storymaker.mediapicker.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

public class ScreenUtil {
    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (activity != null) {
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics;
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (activity != null) {
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (activity != null) {
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics.heightPixels;
    }

    public static int getStatusBarHeight(Activity activity) {
        int dp2px = DensityUtil.dp2px(activity, 25.0f);
        int identifier = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return identifier > 0 ? activity.getResources().getDimensionPixelSize(identifier) : dp2px;
    }
}
