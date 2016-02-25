package com.echo.common.util;

import android.os.Environment;

/**
 * Created by jiangecho on 16/2/23.
 */
public class FileUtil {
    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static String getSdCardPath() {
        boolean exist = isSdCardExist();
        String sdPath;
        if (exist) {
            sdPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {
            sdPath = null;
        }

        return sdPath;

    }
}
