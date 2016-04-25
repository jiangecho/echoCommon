package com.echo.common.util.retrofit;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jiangecho on 16/3/26.
 */
public class CacheInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                //.header("Cache-Control", "public, only-if-cached, max-stale=" + Integer.MAX_VALUE)
                .cacheControl(CacheControl.FORCE_CACHE)
                .build();
        return chain.proceed(request);
//        Response originalResponse = chain.proceed(request);
//        return originalResponse.newBuilder()
//                .removeHeader("Pragma")
//                .header("Cache-Control", "max-age=" + Integer.MAX_VALUE)
//                .build();
    }
}
