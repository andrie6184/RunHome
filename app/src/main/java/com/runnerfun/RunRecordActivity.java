package com.runnerfun;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.runnerfun.widget.RecyclingPagerAdapter;
import com.runnerfun.widget.ScalePageTransformer;
import com.runnerfun.widget.TransformViewPager;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public class RunRecordActivity extends BaseActivity {

    @BindView(R.id.info_viewpager)
    TransformViewPager viewPager;

//    private RecordAdapter mPagerAdapter;
//    private Typeface boldTypeFace;
//
//    private Map<String, String> runInfo;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_run_record);
//        ButterKnife.bind(this);
//        boldTypeFace = Typeface.createFromAsset(getAssets(), "fonts/dincond-bold.otf");
//        init();
//    }
//
//    private void init() {
//        viewPager.setPageTransformer(true, new ScalePageTransformer(1.2f, 0.8f));
//        mPagerAdapter = new RecordAdapter(this);
//        viewPager.setAdapter(mPagerAdapter);
//        viewPager.setOffscreenPageLimit(7);
//        viewPager.setCurrentItem(5000);
//    }
//
//    @OnTouch(R.id.viewpager_container)
//    boolean onViewTouch(View view, MotionEvent event) {
//        return viewPager.dispatchTouchEvent(event);
//    }



}
