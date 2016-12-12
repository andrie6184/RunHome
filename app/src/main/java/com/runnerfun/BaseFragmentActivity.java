package com.runnerfun;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

import com.runnerfun.tools.RxUtils;
import com.runnerfun.widget.SystemStatusBarManager;

import rx.Subscription;

/**
 * BaseFragmentActivity
 * Created by andrie on 16/12/12.
 */

public class BaseFragmentActivity extends FragmentActivity {

    protected Subscription _subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemStatusBarManager statusManager = new SystemStatusBarManager(this);
        statusManager.setStatusBarTintEnabled(true);
        statusManager.setTintColor(getResources().getColor(R.color.header_background));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // AVAnalytics.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // AVAnalytics.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtils.unSubscribeIfNotNull(_subscription);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
