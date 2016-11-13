package com.runnerfun;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.runnerfun.tools.PicassoImageLoader;
import com.squareup.picasso.Picasso;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

import timber.log.Timber;

/**
 * RunApplication
 * Created by andrie on 18/10/2016.
 */

public class RunApplication extends Application {

    // for MI-push--------start-----------------
    // user your appid the key.
    // TODO private static final String APP_ID = "2882303761517522582";
    // user your appid the key.
    // TODO private static final String APP_KEY = "5321752268582";
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

        // init photo picker
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new PicassoImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);
        imagePicker.setCrop(true);
        imagePicker.setSaveRectangle(false);
        imagePicker.setMultiMode(false);
        imagePicker.setStyle(CropImageView.Style.CIRCLE);
        imagePicker.setFocusWidth(800);
        imagePicker.setFocusHeight(800);
        imagePicker.setOutPutX(800);
        imagePicker.setOutPutY(800);

        // init Picasso
        picasso = Picasso.with(this);

        // for lean-cloud analytic & feedback.
        // TODO AVOSCloud.initialize(this, "r1QGocPzMUuYGjpSvulhVN6N-gzGzoHsz", "4KjO9YOWwQY9WdoTBNMyFtJg");
        // TODO AVAnalytics.enableCrashReport(this, true);

        // for MI-push
        /* if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
            String account = "a" + getIMEI();
            MiPushClient.setAlias(this, account, "ANDROID_USER");
        }
        // init Mi-push log.
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);*/
    }

    /*private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    private String getIMEI() {
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }*/

}
