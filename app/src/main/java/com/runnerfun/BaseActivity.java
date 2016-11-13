package com.runnerfun;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.runnerfun.tools.RxUtils;

import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by andrie on 18/10/2016.
 */

abstract public class BaseActivity extends AppCompatActivity {

    protected Subscription _subscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtils.unSubscribeIfNotNull(_subscription);
    }

}
