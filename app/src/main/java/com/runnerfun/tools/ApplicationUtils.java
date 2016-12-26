package com.runnerfun.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.view.WindowManager;

import com.runnerfun.LoginActivity;
import com.runnerfun.RunApplication;
import com.runnerfun.network.NetworkManager;

/**
 * ApplicationUtils
 * Created by andrie on 16/11/20.
 */

public class ApplicationUtils {

    public static void navigationActivityWithCheckLogin(Context context, Intent navigate) {
        if (NetworkManager.instance.hasLoginInfo()) {
            context.startActivity(navigate);
        } else {
            context.startActivity(new Intent(context, LoginActivity.class));
        }
    }

    public static String getAppName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = RunApplication.getAppContex().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(RunApplication.getAppContex().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        return (String) packageManager.getApplicationLabel(applicationInfo);
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) RunApplication.getAppContex()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
            return true;
        } else if (gprs == State.CONNECTED || gprs == State.CONNECTING) {
            return true;
        }
        return false;
    }

    public static float getDeviceWidth() {
        WindowManager wm = (WindowManager) RunApplication.getAppContex()
                .getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point.x;
    }

}
