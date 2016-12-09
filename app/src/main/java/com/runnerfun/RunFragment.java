package com.runnerfun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.runnerfun.beans.RunIdBean;
import com.runnerfun.beans.UserInfo;
import com.runnerfun.model.ConfigModel;
import com.runnerfun.model.RecordModel;
import com.runnerfun.network.NetworkManager;
import com.runnerfun.tools.TimeStringUtils;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by andrie on 16/10/2016.
 */

public class RunFragment extends Fragment {
    @BindView(R.id.chronometer)
    TextView mClockView;
    private TextView mKmValue;
    private TextView mSpeedValue;
    private TextView mKaclValue;
    @BindView(R.id.money_value)
    TextView mMoney;
    @BindView(R.id.counter)
    TextView mCountDownView;

    private Subscription mTimer = null;
    private Subscription mCounter = null;
    private Animation mScaleAnimation = new ScaleAnimation(1.f, 0.f, 1.f, 0.f
            , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

    private LocalBroadcastManager mLocalManager;
    private UserMoneyReceiver mReceiver;
    public static String USER_MONEY_CHANGED_ACTION = "USER_MONEY_CHANGED_ACTION";
    private DecimalFormat decimalFormat=new DecimalFormat("0.00");

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_run, container, false);
        ButterKnife.bind(this, rootView);

        mLocalManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter filter = new IntentFilter(USER_MONEY_CHANGED_ACTION);
        mReceiver = new UserMoneyReceiver();
        mLocalManager.registerReceiver(mReceiver, filter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/dincond.otf");
        Typeface boldTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/dincond-bold.otf");
        mClockView.setTypeface(boldTypeFace);
        mMoney.setTypeface(typeFace);
        mMoney.setText("0");

        View km = view.findViewById(R.id.km);
        View speed = view.findViewById(R.id.speed);
        View kacl = view.findViewById(R.id.kacl);

        ((TextView) km.findViewById(R.id.en_title)).setTypeface(typeFace);
        ((TextView) km.findViewById(R.id.value)).setTypeface(typeFace);
        ((TextView) speed.findViewById(R.id.en_title)).setTypeface(typeFace);
        ((TextView) speed.findViewById(R.id.value)).setTypeface(typeFace);
        ((TextView) kacl.findViewById(R.id.en_title)).setTypeface(typeFace);
        ((TextView) kacl.findViewById(R.id.value)).setTypeface(typeFace);

        ((TextView) km.findViewById(R.id.en_title)).setText("km");
        ((TextView) speed.findViewById(R.id.en_title)).setText("km/s");
        ((TextView) kacl.findViewById(R.id.en_title)).setText("kacl");
        ((TextView) km.findViewById(R.id.zh_title)).setText("距离");
        ((TextView) speed.findViewById(R.id.zh_title)).setText("配速");
        ((TextView) kacl.findViewById(R.id.zh_title)).setText("消耗");

        mKmValue = (TextView) km.findViewById(R.id.value);
        mSpeedValue = (TextView) speed.findViewById(R.id.value);
        mKaclValue = (TextView) kacl.findViewById(R.id.value);
        mScaleAnimation.setDuration(1000);

        setMoney();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalManager.unregisterReceiver(mReceiver);
    }

    @OnClick(R.id.btn_map)
    void map() {
        startActivity(new Intent(getActivity(), MapActivity.class));
    }

    //TODO: @OnClick(R.id.btn_run)
    void start() {
        if (RecordModel.instance.isRecording()) {
            Toast.makeText(getActivity(), "跑步已经开始", Toast.LENGTH_SHORT).show();
            return;
        }
        NetworkManager.instance.getRecordId(new Subscriber<RunIdBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(RunIdBean runIdBean) {
                doStart(runIdBean.getId());
            }
        });
    }

    @OnClick(R.id.btn_run)
    void test() {
        if (RecordModel.instance.isRecording() || RecordModel.instance.isPause()) {
            Toast.makeText(getActivity(), "跑步已经开始", Toast.LENGTH_SHORT).show();
            return;
        }
        if (RecordModel.instance.isRecording()) {
            RecordService.stopRecord(getActivity());
        } else {
            doStart(-1);
        }
    }

    private void doStart(final long id) {
        if (ConfigModel.instance.getmCountDownSecond() <= 0) {
            RecordService.startRecord(getActivity(), id);
            return;
        }
        mCountDownView.setText("");
        mCountDownView.setVisibility(View.VISIBLE);
        //TODO:3,2,1 动画
        if (mCounter != null) {
            mCounter.unsubscribe();
        }

        if (ConfigModel.instance.useCountdown()) {
            final long s = ConfigModel.instance.getmCountDownSecond();
            showCountDown(s);
            mCounter = Observable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            if (aLong >= s - 1) {
                                mCounter.unsubscribe();
                                mCountDownView.setVisibility(View.GONE);
                                RecordService.startRecord(getActivity(), id);
                            } else {
                                showCountDown(s - aLong - 1);
                            }
                        }
                    });
        }
    }

    private void showCountDown(long second) {
        mCountDownView.setText(String.valueOf(second));
        mCountDownView.clearAnimation();
        mCountDownView.startAnimation(mScaleAnimation);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        refreshResult();
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTimer != null) {
            mTimer.unsubscribe();
        }
    }

    private void refreshResult() {

        mClockView.setText(msToString(RecordModel.instance.getRecordTime()));
        mKmValue.setText(decimalFormat.format(RecordModel.instance.getDistance() / 1000));
        mKaclValue.setText(String.valueOf((int) RecordModel.instance.getCal()));
        mSpeedValue.setText(decimalFormat.format(RecordModel.instance.getSpeed()));
    }

    private String msToString(long ms) {
        String t = TimeStringUtils.getTime(ms);
        Log.d("TIMER", "current time = " + t);
        return t;
    }

    @OnClick(R.id.config)
    void mapConfig() {
        startActivity(new Intent(getActivity(), ShareActivity.class));
//        getActivity().startActivity(new Intent(getActivity(), RunConfigActivity.class));
    }

    private void setMoney() {
        String info = RunApplication.getAppContex().sharedPreferences.getString(
                UserFragment.SP_KEY_USER_INFO, "");
        if (!TextUtils.isEmpty(info)) {
            UserInfo userInfo = new Gson().fromJson(info, UserInfo.class);
            mMoney.setText(userInfo.getTotal_score());
        }
    }
    
    private class UserMoneyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setMoney();
        }
    }

}
