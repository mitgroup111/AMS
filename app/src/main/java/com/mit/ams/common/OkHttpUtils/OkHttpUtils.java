package com.mit.ams.common.OkHttpUtils;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpUtils {

    private static OkHttpClient okHttpClient;

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public static void initOkHttp(Context context) {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .cookieJar(CookieStore.getInstance(context))
                .build();
    }
}
