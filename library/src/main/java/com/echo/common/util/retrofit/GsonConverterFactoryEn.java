package com.echo.common.util.retrofit;


/**
 * Created by jiangecho on 16/1/5.
 */
/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * attention: because the response json maybe dynamic, so create it.
 * support return the json format string directly
 * ex:
 * <pre>
 * {@code
 *      // fuck the response is dynamic
 *      @literal @'GET("workout/plan/indexv1")
 *      Observable<String> getPlanStatusInfo();
 * }
 * </pre>
 */
public final class GsonConverterFactoryEn extends Converter.Factory {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static GsonConverterFactoryEn create() {
        return create(new Gson());
    }

    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static GsonConverterFactoryEn create(Gson gson) {
        return new GsonConverterFactoryEn(gson);
    }

    private final Gson gson;

    private GsonConverterFactoryEn(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new GsonResponseBodyConverter<>(gson, type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new GsonRequestBodyConverter<>(gson, type);
    }


    final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson gson;
        private final Type type;

        GsonResponseBodyConverter(Gson gson, Type type) {
            this.gson = gson;
            this.type = type;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            Reader reader = value.charStream();

            if (String.class.equals(type)) {
                return (T) fromStream(reader);
            }

            try {
                return gson.fromJson(reader, type);
            } finally {
                closeQuietly(reader);
            }
        }

        private String fromStream(Reader reader) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                out.append(line);
                out.append("\r\n");
            }
            return out.toString();
        }
    }

    final class GsonRequestBodyConverter<T> implements Converter<T, RequestBody> {

        private final Gson gson;
        private final Type type;

        GsonRequestBodyConverter(Gson gson, Type type) {
            this.gson = gson;
            this.type = type;
        }

        @Override
        public RequestBody convert(T value) throws IOException {
            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
            try {
                gson.toJson(value, type, writer);
                writer.flush();
            } catch (IOException e) {
                throw new AssertionError(e); // Writing to Buffer does no I/O.
            }
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        }
    }

    static void closeQuietly(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }
}
