package com.runnerfun;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.runnerfun.tools.ApplicationUtils;
import com.runnerfun.tools.RxUtils;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {

    private Subscription _subscription;

    @BindView(R.id.loading_image)
    ImageView _loadingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        _loadingImage.setBackgroundResource(R.drawable.splash_animtion);
        AnimationDrawable animation = (AnimationDrawable) _loadingImage.getBackground();
        animation.setOneShot(true);
        animation.start();
        navigateToMain();
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

    private void navigateToMain() {
        _subscription = Observable.timer(2972, TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                if (!RunApplication.getAppContex().sharedPreferences.getBoolean("startGuide", false)) {
                    RunApplication.getAppContex().sharedPreferences.edit().putBoolean("startGuide", true).apply();
                    startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                } else {
                    ApplicationUtils.navigationActivityWithCheckLogin(SplashActivity.this,
                            new Intent(SplashActivity.this, MainActivity.class));
                }
                finish();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Timber.e(throwable, "open MainActivity error", "SplashActivity");
            }
        });
    }

}
