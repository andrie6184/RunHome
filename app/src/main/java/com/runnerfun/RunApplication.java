package com.runnerfun;

import android.app.Application;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.runnerfun.tools.PicassoImageLoader;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

/**
 * RunApplication
 * Created by lixiaoyang on 18/10/2016.
 */

public class RunApplication extends Application {

    private static RunApplication _instance;

    public static RunApplication getAppContex() {
        return _instance;
    }

    public Picasso picasso;

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
    }

}
