package com.echo.common.util.retrofit;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by jiangecho on 15/12/24.
 */
public class AddQueryParamterInterceptor implements Interceptor {

    private String key, value;

    public AddQueryParamterInterceptor(String key, String value){
        this.key = key;
        this.value = value;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpUrl url = chain.request().httpUrl()
                .newBuilder()
                .addQueryParameter(key, value)
                .build();
        Request request = chain.request().newBuilder().url(url).build();
        return chain.proceed(request);
    }
}
