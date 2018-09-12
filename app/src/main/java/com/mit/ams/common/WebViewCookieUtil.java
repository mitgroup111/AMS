package com.mit.ams.common;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class WebViewCookieUtil {

    /**
     * 把cookie交给webview
     *
     * @param context
     * @param url
     * @param cookies
     */
    public static void synchronousWebCookies(Context context, String url, String cookies) {
        if (!TextUtils.isEmpty(url))
            if (!TextUtils.isEmpty(cookies)) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    CookieSyncManager.createInstance(context);
                }
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setAcceptCookie(true);
                cookieManager.removeSessionCookie();// 移除
                cookieManager.removeAllCookie();
                StringBuilder sbCookie = new StringBuilder();
                sbCookie.append(cookies);
                String cookieValue = sbCookie.toString();
                Log.e("synchronousWebCookies: ", cookieValue);
                cookieManager.setCookie(url, cookieValue);//为url设置cookie

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    CookieSyncManager.getInstance().sync();
                } else {
                    cookieManager.flush();
                }


            }
    }

}
