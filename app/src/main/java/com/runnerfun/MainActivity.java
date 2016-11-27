package com.runnerfun;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.runnerfun.model.AccountModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

//import com.lzy.imagepicker.ImagePicker;
//import com.lzy.imagepicker.bean.ImageItem;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_IMAGE_PICKER = 1001;
    public static final String SELECTED_USER_AVATAR_ACTION = "SELECTED_USER_AVATAR_ACTION";
    public static final String INTENT_PARAMS_USER_AVATAR_PATH = "INTENT_PARAMS_USER_AVATAR_PATH";

    private boolean quitFlag = false;

    @BindView(R.id.fragment_content)
    ViewPager mContents;
    @BindView(R.id.tabs)
    TabLayout mTabs;

    @BindView(R.id.new_login_gift)
    RelativeLayout gift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        installTabs();

        if (getIntent().getBooleanExtra("isFirstLogin", false)) {
            gift.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (quitFlag) {
            finish();
        } else {
            quitFlag = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            Observable.timer(2000, TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    quitFlag = false;
                }
            });
        }
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
        mTabs.getTabAt(0).setText("商城").setIcon(R.drawable.icon_store_hui).setTag("0");
        mTabs.getTabAt(1).setText("运动").setIcon(R.drawable.icon_yundong_bai).setTag("1");
        mTabs.getTabAt(2).setText("我的").setIcon(R.drawable.icon_wo_hui).setTag("2");
        mContents.setCurrentItem(1);

        mTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mTabs.getTabAt(0).setIcon(R.drawable.icon_store_hui);
                mTabs.getTabAt(1).setIcon(R.drawable.icon_yundong_hui);
                mTabs.getTabAt(2).setIcon(R.drawable.icon_wo_hui);
                if (tab.getTag().equals("0")) {
                    tab.setIcon(R.drawable.icon_store_bai);
                } else if (tab.getTag().equals("1")) {
                    tab.setIcon(R.drawable.icon_yundong_bai);
                } else if (tab.getTag().equals("2")) {
                    tab.setIcon(R.drawable.icon_wo_bai);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @OnClick(R.id.gift_close)
    void onCloseClicked(View view) {
        gift.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
//            if (data != null && requestCode == REQUEST_CODE_IMAGE_PICKER) {
//                Serializable serials = data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
//                if (serials != null && serials instanceof ArrayList) {
//                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) serials;
//                    if (images.size() > 0) {
//                        Intent intent = new Intent(SELECTED_USER_AVATAR_ACTION);
//                        intent.putExtra(INTENT_PARAMS_USER_AVATAR_PATH, images.get(0).path);
//                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//                    }
//                }
//            }
//        }
    }

    class ContentFragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList;

        ContentFragmentAdapter(FragmentManager fm) {
            super(fm);
            mFragmentList = new ArrayList<Fragment>();
            mFragmentList.add(new ShopFragment());
            mFragmentList.add(new RunFragment());
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
