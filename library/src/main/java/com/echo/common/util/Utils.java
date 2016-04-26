package com.echo.common.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

/**
 * Created by jiangecho on 16/4/26.
 */
public class Utils {

    public static int calculateMemoryCacheSize(Context context) {
        ActivityManager am = getService(context, Application.ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0;
        int memoryClass = am.getMemoryClass();
        if (largeHeap && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            memoryClass = ActivityManagerHoneycomb.getLargeMemoryClass(am);
        }
        // Target ~10% of the available heap.
        return 1024 * 1024 * memoryClass / 9;
    }

    static <T> T getService(Context context, String service) {
        return (T) context.getSystemService(service);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static class ActivityManagerHoneycomb {
        static int getLargeMemoryClass(ActivityManager activityManager) {
            return activityManager.getLargeMemoryClass();
        }
    }
}
