package com.runnerfun;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.runnerfun.model.AccountModel;
import com.runnerfun.widget.DisableScrollViewPager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_IMAGE_PICKER = 1001;
    public static final String SELECTED_USER_AVATAR_ACTION = "SELECTED_USER_AVATAR_ACTION";
    public static final String INTENT_PARAMS_USER_AVATAR_PATH = "INTENT_PARAMS_USER_AVATAR_PATH";

    @BindView(R.id.fragment_content)
    DisableScrollViewPager mContents;
    @BindView(R.id.tabs)
    TabLayout mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContents.setScrollEnabled(false);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == REQUEST_CODE_IMAGE_PICKER) {
                Serializable serials = data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (serials != null && serials instanceof ArrayList) {
                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) serials;
                    if (images.size() > 0) {
                        Intent intent = new Intent(SELECTED_USER_AVATAR_ACTION);
                        intent.putExtra(INTENT_PARAMS_USER_AVATAR_PATH, images.get(0).path);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    }
                }
            }
        }
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
