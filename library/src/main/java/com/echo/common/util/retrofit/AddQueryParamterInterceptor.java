package com.echo.common.util.retrofit;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jiangecho on 15/12/24.
 */
public class AddQueryParamterInterceptor implements Interceptor {

    private String key, value;

    public AddQueryParamterInterceptor(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpUrl url = chain.request().url()
                .newBuilder()
                .addQueryParameter(key, value)
                .build();
        Request request = chain.request().newBuilder().url(url).build();
        return chain.proceed(request);
    }
}
