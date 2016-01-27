package com.echo.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by jiangecho on 16/1/27.
 */
public class ScreenUtil {

    public static boolean isScreenPortrait(Activity activity) {
        final int rotation = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            return true;
        } else {
            return false;
        }
    }

    public void disableScreenRotation(Activity activity) {
        if (isScreenPortrait(activity)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public static void enableScreenRotation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
