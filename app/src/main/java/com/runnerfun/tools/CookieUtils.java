package com.runnerfun.tools;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.runnerfun.RunApplication;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import rx.Observable;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * CookieUtils
 * Created by lixiaoyang on 18/10/2016.
 */

public class CookieUtils {

    public static void synWebViewCookies(Context context, final String url, final List<String> cookies) {
        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.acceptCookie();
        Observable.from(cookies).subscribe(new Action1<String>() {
            @Override
            public void call(String string) {
                String cookie = string + "Domain=www.paobuzhijia.com;Path=/";
                cookieManager.setCookie(url, cookie);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Timber.e(throwable, "synCookies error");
            }
        });
        CookieSyncManager.getInstance().sync();
    }

    public static List<String> getLocalCookies() {
        final List<String> cookies = new ArrayList<>();
        SharedPrefsCookiePersistor cookiePersistor = new SharedPrefsCookiePersistor(RunApplication.getAppContex());
        Observable.from(cookiePersistor.loadAll()).subscribe(new Action1<Cookie>() {
            @Override
            public void call(Cookie cookie) {
                cookies.add(cookie.toString());
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Timber.e(throwable, "getLocalCookies error");
            }
        });
        return cookies;
    }

    public static void removeAllCookies(Context context) {
        SharedPrefsCookiePersistor cookiePersistor = new SharedPrefsCookiePersistor(RunApplication.getAppContex());
        cookiePersistor.clear();

        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }

}
