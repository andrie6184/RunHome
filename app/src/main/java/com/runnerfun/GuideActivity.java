package com.runnerfun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.runnerfun.tools.ApplicationUtils;
import com.runnerfun.widget.RecyclingPagerAdapter;
import com.runnerfun.widget.TransformViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuideActivity extends Activity {

    @BindView(R.id.guide_viewpager)
    TransformViewPager mViewPager;
    @BindView(R.id.start_btn)
    ImageView mStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        GuideAdapter mPagerAdapter = new GuideAdapter(this);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    mStartBtn.setVisibility(View.VISIBLE);
                } else {
                    mStartBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @OnClick(R.id.start_btn)
    void onStartClick(View view) {
        ApplicationUtils.navigationActivityWithCheckLogin(this, new Intent(this, MainActivity.class));
        finish();
    }

    public class GuideAdapter extends RecyclingPagerAdapter {

        private final Context mContext;
        private int[] images = {R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};

        public GuideAdapter(Context context) {
            mContext = context;

        }

        public int getItem(int position) {
            return images[position];
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            ImageView imageView = null;
            if (convertView == null) {
                imageView = new ImageView(mContext);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setTag(position);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageResource(getItem(position));
            return imageView;
        }

    }

}
