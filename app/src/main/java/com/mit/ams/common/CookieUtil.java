package com.mit.ams.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.tamic.novate.cookie.SerializableCookie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;

public class CookieUtil {
    private static final String COOKIE_PREFS = "Novate_Cookies_Prefs";

    /**
     * 加载cookie
     * @param context
     * @return
     */
    public static List<Cookie> loadAll(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE);
        List<Cookie> cookies = new ArrayList<>(sharedPreferences.getAll().size());

        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            String serializedCookie = (String) entry.getValue();
            Cookie cookie = new SerializableCookie().decode(serializedCookie);
            cookies.add(cookie);
        }
        return cookies;
    }

    /**
     * 将cookie转化为webview使用的cookie形式
     * @param context
     * @return
     */
    public static String getCookie(Context context) {
        List<Cookie> allCookie = loadAll(context);
        for (int i = 0; i < allCookie.size(); i++) {
            return allCookie.get(i).toString();
        }
        return null;

    }
}
