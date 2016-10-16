package com.runnerfun;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.runnerfun.model.AccountModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.fragment_content)
    ViewPager mContents;
    @BindView(R.id.tabs)
    TabLayout mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        installTabs();
    }

    private void installTabs() {
        mContents.setAdapter(new ContentFragmentAdapter(getSupportFragmentManager()));
        mContents.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2 && !AccountModel.instance.hasLoginInfo()) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabs.setTabMode(TabLayout.MODE_FIXED);
        mTabs.setupWithViewPager(mContents);
        mTabs.getTabAt(0).setText("商城").setIcon(R.drawable.icon_store);
        mTabs.getTabAt(1).setText("运动").setIcon(R.drawable.icon_yundong);
        mTabs.getTabAt(2).setText("我的").setIcon(R.drawable.icon_wo);
        mContents.setCurrentItem(1);
    }

    private void initMainUI() {

    }

    class ContentFragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList;

        ContentFragmentAdapter(FragmentManager fm) {
            super(fm);
            mFragmentList = new ArrayList<Fragment>();
            mFragmentList.add(new ShopFragment());
            mFragmentList.add(new MainFragment());
            mFragmentList.add(new UserFragment());
        }

        @Override
        public int getCount() {
            return mFragmentList.size();//页卡数
        }


        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

    }

}
