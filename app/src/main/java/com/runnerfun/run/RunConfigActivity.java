package com.runnerfun.run;

import android.os.Bundle;

import com.runnerfun.BaseActivity;
import com.runnerfun.R;

import butterknife.ButterKnife;

public class RunConfigActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_config);
        ButterKnife.bind(this);
    }
}
