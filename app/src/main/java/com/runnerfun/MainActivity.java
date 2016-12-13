package com.runnerfun;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

public class MainActivity extends BaseFragmentActivity {

    public static final int REQUEST_CODE_IMAGE_PICKER = 1001;

    private boolean quitFlag = false;

    private int[] tabIcons = {
            R.drawable.icon_store_hui,
            R.drawable.icon_yundong_hui,
            R.drawable.icon_wo_hui
    };

    private int[] tabSelectedIcons = {
            R.drawable.icon_store_bai,
            R.drawable.icon_yundong_bai,
            R.drawable.icon_wo_bai
    };
    private List<String> titles;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void installTabs() {
        titles = new ArrayList<>();
        titles.add("商城");
        titles.add("运动");
        titles.add("我的");

        mContents.setAdapter(new ContentFragmentAdapter(getSupportFragmentManager()));
        mContents.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < 3; i++) {
                    mTabs.getTabAt(i).setCustomView(null);
                    mTabs.getTabAt(i).setCustomView(getTabView(i, i == position));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabs.setupWithViewPager(mContents);
        mContents.setCurrentItem(1);
        mContents.setOffscreenPageLimit(2);
    }

    @OnClick(R.id.gift_close)
    void onCloseClicked(View view) {
        gift.setVisibility(View.GONE);
    }

    public View getTabView(int position, boolean selected) {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_item_tab, null);
        TextView txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setText(titles.get(position));
        ImageView img_title = (ImageView) view.findViewById(R.id.img_title);
        if (selected) {
            txt_title.setTextColor(Color.parseColor("#979797"));
            img_title.setImageResource(tabSelectedIcons[position]);
        } else {
            txt_title.setTextColor(Color.parseColor("#FFFFFF"));
            img_title.setImageResource(tabIcons[position]);
        }
        return view;
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
