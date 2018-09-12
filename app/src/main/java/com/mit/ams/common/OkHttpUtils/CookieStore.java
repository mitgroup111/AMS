package com.mit.ams.common.OkHttpUtils;

import android.content.Context;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieStore implements CookieJar {

    private static CookieStore instance = null;
    private static Context context;
    private final PersistentCookieStore cookieStore = new
            PersistentCookieStore(context);

    public static CookieStore getInstance(Context context1) {
        context=context1;
        if (null == instance) {
            instance = new CookieStore();
        }
        return instance;
    }
    private CookieStore() {
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(url, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url);
        return cookies;
    }
}
