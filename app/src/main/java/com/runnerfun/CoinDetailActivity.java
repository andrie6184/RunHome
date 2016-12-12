package com.runnerfun;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CoinDetailActivity extends BaseFragmentActivity {

    @BindView(R.id.user_record_selected)
    ImageView mUserTabSelected;
    @BindView(R.id.system_record_selected)
    ImageView mSystemTabSelected;

    @BindView(R.id.coin_fragment_pager)
    ViewPager mContentPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_detail);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mContentPager.setAdapter(new CoinPagerAdapter(getSupportFragmentManager()));
        mContentPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mUserTabSelected.setVisibility(View.VISIBLE);
                    mSystemTabSelected.setVisibility(View.GONE);
                } else {
                    mUserTabSelected.setVisibility(View.GONE);
                    mSystemTabSelected.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @OnClick(R.id.user_record)
    void onUserRecordClicked(View view) {
        mContentPager.setCurrentItem(0);
    }

    @OnClick(R.id.system_record)
    void onSystemRecordClicked(View view) {
        mContentPager.setCurrentItem(1);
    }

    @OnClick(R.id.return_btn)
    void onReturnClicked(View view) {
        finish();
    }

    private class CoinPagerAdapter extends FragmentPagerAdapter {

        public CoinPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return CoinDetailFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
