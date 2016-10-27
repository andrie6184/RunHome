package com.runnerfun;

import android.app.Application;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * RunApplication
 * Created by lixiaoyang on 18/10/2016.
 */

public class RunApplication extends Application {

    private static RunApplication _instance;

    public static RunApplication getAppContex() {
        return _instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        _instance = this;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this);
            // unrelated, just make sure cookies are generally allowed
            android.webkit.CookieManager.getInstance().setAcceptCookie(true);
            CookieSyncManager.getInstance().sync();
        }
        CookieManager.getInstance().setAcceptCookie(true);
        // magic starts here
    }

}
