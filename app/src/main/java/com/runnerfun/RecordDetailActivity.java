package com.runnerfun;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordDetailActivity extends BaseFragmentActivity {

    @BindView(R.id.personal_record_selected)
    ImageView mUserTabSelected;
    @BindView(R.id.week_record_selected)
    ImageView mWeekTabSelected;
    @BindView(R.id.total_record_selected)
    ImageView mTotalTabSelected;
    
    @BindView(R.id.record_fragment_pager)
    ViewPager mContentPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mContentPager.setAdapter(new RecordPagerAdapter(getSupportFragmentManager()));
        mContentPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mUserTabSelected.setVisibility(View.VISIBLE);
                    mWeekTabSelected.setVisibility(View.GONE);
                    mTotalTabSelected.setVisibility(View.GONE);
                } else if (position == 1) {
                    mUserTabSelected.setVisibility(View.GONE);
                    mWeekTabSelected.setVisibility(View.VISIBLE);
                    mTotalTabSelected.setVisibility(View.GONE);
                } else {
                    mUserTabSelected.setVisibility(View.GONE);
                    mWeekTabSelected.setVisibility(View.GONE);
                    mTotalTabSelected.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @OnClick(R.id.personal_record)
    void onPersonalRecordClicked(View view) {
        mContentPager.setCurrentItem(0);
    }

    @OnClick(R.id.week_record)
    void onWeekRecordClicked(View view) {
        mContentPager.setCurrentItem(1);
    }

    @OnClick(R.id.total_record)
    void onTotalRecordClicked(View view) {
        mContentPager.setCurrentItem(2);
    }

    @OnClick(R.id.return_btn)
    void onReturnClicked(View view) {
        finish();
    }

    private class RecordPagerAdapter extends FragmentPagerAdapter {

        public RecordPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 1) {
                return WeekRecordFragment.newInstance();
            } else if (position == 2) {
                return TotalRecordFragment.newInstance();
            } else {
                return PersonalRecordFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
