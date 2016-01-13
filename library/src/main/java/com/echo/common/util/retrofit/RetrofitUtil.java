package com.echo.common.util.retrofit;

import android.content.Context;

import com.google.gson.Gson;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiangecho on 16/1/5.
 */
public class RetrofitUtil {
    public static void deleteAllCookies(Context context) {
        PersistentCookieStore.getInstance(context).removeAll();
    }

    public static String getCookies(Context context, String domain) {
        PersistentCookieStore myCookieStore = PersistentCookieStore.getInstance(context);
        List<HttpCookie> cookies = myCookieStore.getCookies();
        Map<String, String> keyValues = new HashMap<>();
        if (cookies == null || domain == null) {
            return null;
        }

        for (HttpCookie cookie : cookies) {
            if (domain.equals(cookie.getDomain())) {
                keyValues.put(cookie.getName(), cookie.getValue());
            }
        }
        return new Gson().toJson(keyValues);
    }

    public static String getCookie(Context context, String domain, String key) {
        PersistentCookieStore cookieStore = PersistentCookieStore.getInstance(context);
        List<HttpCookie> cookies = cookieStore.getCookies();
        if (cookies == null || domain == null || key == null) {
            return null;
        }

        for (HttpCookie cookie : cookies) {
            if (cookie.getDomain().equals(domain) && cookie.getName().equals(key)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
