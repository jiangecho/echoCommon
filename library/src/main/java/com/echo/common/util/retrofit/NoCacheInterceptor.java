package com.echo.common.util.retrofit;

import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by jiangecho on 16/3/26.
 */
public class NoCacheInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();
        Response originalResponse = chain.proceed(request);
        return originalResponse.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "max-age=" + Integer.MAX_VALUE)
                .build();
    }
}
