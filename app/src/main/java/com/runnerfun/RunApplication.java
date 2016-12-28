package com.runnerfun;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.runnerfun.model.ConfigModel;
import com.runnerfun.model.OptimizeConfigModel;
import com.squareup.picasso.Picasso;

import org.litepal.LitePal;

import cn.jpush.android.api.JPushInterface;
import timber.log.Timber;

/**
 * RunApplication
 * Created by andrie on 18/10/2016.
 */

public class RunApplication extends Application {

    // for MI-push--------start-----------------
    private static final String MI_APP_ID = "2882303761517525173";
    private static final String MI_APP_KEY = "5701752515173";

    public static final String TAG = "com.runnerfun.android";

    private static RunApplication _instance;

    public static RunApplication getAppContex() {
        return _instance;
    }

    public SharedPreferences sharedPreferences;
    public Picasso picasso;

    @Override
    public void onCreate() {
        super.onCreate();

        _instance = this;

        sharedPreferences = getSharedPreferences("RunApplication", Activity.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this);
            // unrelated, just make sure cookies are generally allowed
            android.webkit.CookieManager.getInstance().setAcceptCookie(true);
            CookieSyncManager.getInstance().sync();
        }
        CookieManager.getInstance().setAcceptCookie(true);
        // magic starts here
        Timber.plant(new Timber.DebugTree());

        // init Picasso
        picasso = Picasso.with(this);
        ConfigModel.instance.load(this);
        OptimizeConfigModel.instance.load(this);

        // init database
        LitePal.initialize(this);

        // for lean-cloud analytic & feedback.
        AVOSCloud.initialize(this, "OQpEDpAfPU5fxknXO4YWuV6J-gzGzoHsz", "huXzXqNd6uGPi8yI8tG2pwnj");
        AVAnalytics.enableCrashReport(this, true);

        // for JiGuang-push
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        JPushInterface.stopCrashHandler(this); //close crash report, duplicate with LeanCloud.
    }

}
