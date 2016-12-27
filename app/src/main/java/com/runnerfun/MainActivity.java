package com.runnerfun;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.RunSaveResultBean;
import com.runnerfun.beans.RunUploadBean;
import com.runnerfun.beans.RunUploadDB;
import com.runnerfun.network.NetworkManager;
import com.runnerfun.tools.ApplicationUtils;
import com.runnerfun.widget.NoScrollViewPager;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends BaseFragmentActivity {

    public static final int REQUEST_CODE_IMAGE_PICKER = 1001;
    private static final String TAG = "MainActivity";

    private boolean quitFlag = false;
    private boolean refreshFlag = false;

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
    NoScrollViewPager mContents;
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
        // for JiGuang Push
        setAlias();
        // for tts settings.
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // upload local track in background.
        if (!ApplicationUtils.isNetworkAvailable()) {
            return;
        }
        List<RunUploadDB> tacks = DataSupport.findAll(RunUploadDB.class);
        Observable.from(tacks).subscribeOn(Schedulers.io()).doOnCompleted(new Action0() {
            @Override
            public void call() {
                if (refreshFlag) {
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(
                            new Intent(PersonalRecordFragment.TRACKS_REFRESH_ACTION));
                    LocalBroadcastManager.getInstance(RunApplication.getAppContex())
                            .sendBroadcast(new Intent(UserFragment.USER_INFO_CHANGED_ACTION));
                    refreshFlag = false;
                }
            }
        }).subscribe(new Action1<RunUploadDB>() {
            @Override
            public void call(final RunUploadDB db) {
                refreshFlag = true;
                RunUploadBean bean = RunUploadDB.transToRunUploadDB(db);
                NetworkManager.instance.getSaveRunRecordObservable(bean).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io()).flatMap(new Func1<ResponseBean<RunSaveResultBean>,
                        Observable<?>>() {
                    @Override
                    public Observable<?> call(ResponseBean<RunSaveResultBean> bean) {
                        Timber.d("hallucination", "trigger");
                        return NetworkManager.instance.getUploadTrackObservable(db.getTrack(), bean.getData()
                                .getId()).subscribeOn(Schedulers.io());
                    }
                }).subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        Timber.d(o.toString());
                        if (db.isSaved()) {
                            db.delete();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e(throwable, "upload local track error 1");
                    }
                });
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Timber.e(throwable, "upload local track error 2");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
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
            txt_title.setTextColor(Color.parseColor("#FFFFFF"));
            img_title.setImageResource(tabSelectedIcons[position]);
        } else {
            txt_title.setTextColor(Color.parseColor("#979797"));
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

    // for JiGuang Push
    private void setAlias() {
        String alias = RunApplication.getAppContex().sharedPreferences.getString("USER_UID", "");
        if (!TextUtils.isEmpty(alias)) {
            // 调用 Handler 来异步设置别名
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
        }
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
            Toast.makeText(MainActivity.this, logs, Toast.LENGTH_SHORT).show();
        }
    };

    private static final int MSG_SET_ALIAS = 1001;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null,
                            mAliasCallback);
                    break;
                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };

}
