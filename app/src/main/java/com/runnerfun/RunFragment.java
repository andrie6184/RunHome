package com.runnerfun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import java.util.HashMap;
import java.util.Locale;
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
 * RunFragment
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
    @BindView(R.id.counter_bg)
    View mCountDownBG;
    @BindView(R.id.start_panel)
    View mStartPanel;
    @BindView(R.id.stop_panel)
    View mStopPanel;

    private SoundPool mSoundPool;
    private HashMap<Integer, Integer> mSoundPoolMap;

    private Subscription mTimer = null;
    private Subscription mCounter = null;
    private Animation mScaleAnimation = new ScaleAnimation(1.f, 0.f, 1.f, 0.f
            , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

    private LocalBroadcastManager mLocalManager;
    private UserMoneyReceiver mReceiver;

    private DecimalFormat decimalFormat = new DecimalFormat("0.000");

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_run, container, false);
        ButterKnife.bind(this, rootView);

        mLocalManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter filter = new IntentFilter(UserFragment.USER_INFO_RELOADED_ACTION);
        mReceiver = new UserMoneyReceiver();
        mLocalManager.registerReceiver(mReceiver, filter);

        mSoundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 100);
        mSoundPoolMap = new HashMap<Integer, Integer>();
        mSoundPoolMap.put(1, mSoundPool.load(getActivity(), R.raw.one, 1));
        mSoundPoolMap.put(2, mSoundPool.load(getActivity(), R.raw.two, 1));
        mSoundPoolMap.put(3, mSoundPool.load(getActivity(), R.raw.three, 1));
        mSoundPoolMap.put(4, mSoundPool.load(getActivity(), R.raw.four, 1));
        mSoundPoolMap.put(5, mSoundPool.load(getActivity(), R.raw.five, 1));
        mSoundPoolMap.put(6, mSoundPool.load(getActivity(), R.raw.six, 1));
        mSoundPoolMap.put(7, mSoundPool.load(getActivity(), R.raw.seven, 1));
        mSoundPoolMap.put(8, mSoundPool.load(getActivity(), R.raw.eight, 1));
        mSoundPoolMap.put(9, mSoundPool.load(getActivity(), R.raw.nine, 1));
        mSoundPoolMap.put(10, mSoundPool.load(getActivity(), R.raw.ten, 1));
        mSoundPoolMap.put(11, mSoundPool.load(getActivity(), R.raw.start, 1));

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

    @OnClick(R.id.config)
    void mapConfig() {
        getActivity().startActivity(new Intent(getActivity(), RunConfigActivity.class));
    }

    private void doStart(final long id) {
        if (ConfigModel.instance.getmCountDownSecond() <= 0) {
            RecordService.startRecord(getActivity(), id);
            mStartPanel.setVisibility(View.GONE);
            mStopPanel.setVisibility(View.VISIBLE);
            startActivity(new Intent(getActivity(), MapActivity.class));
            if (ConfigModel.instance.ismUserVoice()) {
                playSound(11, 0);
            }
            return;
        }
        mCountDownView.setText("");
        mCountDownView.setVisibility(View.VISIBLE);
        mCountDownBG.setVisibility(View.VISIBLE);
        // 3,2,1 动画
        if (mCounter != null) {
            mCounter.unsubscribe();
        }

        if (ConfigModel.instance.useCountdown()) {
            final long s = ConfigModel.instance.getmCountDownSecond();
            showCountDown(s);
            if (ConfigModel.instance.ismUserVoice()) {
                playSound((int) s, 0);
            }
            mCounter = Observable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            if (aLong >= s - 1) {
                                mCounter.unsubscribe();
                                mCountDownView.setVisibility(View.GONE);
                                mCountDownBG.setVisibility(View.GONE);
                                RecordService.startRecord(getActivity(), id);
                                mStartPanel.setVisibility(View.GONE);
                                mStopPanel.setVisibility(View.VISIBLE);
                                startActivity(new Intent(getActivity(), MapActivity.class));
                                if (ConfigModel.instance.ismUserVoice()) {
                                    playSound(11, 0);
                                }
                            } else {
                                showCountDown(s - aLong - 1);
                                if (ConfigModel.instance.ismUserVoice()) {
                                    playSound((int) (s - aLong - 1), 0);
                                }
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
        if (RecordModel.instance.isPause() || RecordModel.instance.isRecording()) {
            mStartPanel.setVisibility(View.GONE);
            mStopPanel.setVisibility(View.VISIBLE);
        } else {
            mStartPanel.setVisibility(View.VISIBLE);
            mStopPanel.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.stop_btn)
    void stop() {
        mStopPanel.setVisibility(View.GONE);
        mStartPanel.setVisibility(View.VISIBLE);
        RecordService.stopRecord(getActivity());
    }

    @OnClick(R.id.resume_btn)
    void pause() {
        boolean isRecording = RecordModel.instance.isRecording();
        ((ImageView) mStopPanel.findViewById(R.id.resume_btn)).setImageResource(isRecording ? R.drawable.resume : R.drawable.resume);
        if (isRecording) {
            RecordService.pauseRecord(getActivity());
        } else {
            RecordService.resumeRecord(getActivity());
        }
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

        if (RecordModel.instance.getRecordTime() > 0 && RecordModel.instance.getDistance() > 0) {
            float speedFloat = RecordModel.instance.getRecordTime() / RecordModel.instance.getDistance();
            float speedString = (float) (Math.round(speedFloat * 100) / 100);
            String speedShow = String.format(Locale.getDefault(), "%d'%d\"", (int) speedString, (int) (speedString % 1));
            mSpeedValue.setText(speedShow);
        } else {
            mSpeedValue.setText("0'00\"");
        }
    }

    private String msToString(long ms) {
        String t = TimeStringUtils.getTime(ms);
        Log.d("TIMER", "current time = " + t);
        return t;
    }

    private void setMoney() {
        String info = RunApplication.getAppContex().sharedPreferences.getString(
                UserFragment.SP_KEY_USER_INFO, "");
        if (!TextUtils.isEmpty(info)) {
            UserInfo userInfo = new Gson().fromJson(info, UserInfo.class);
            mMoney.setText(userInfo.getTotal_score());
        }
    }

    public void playSound(int sound, int loop) {
        AudioManager mgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        //参数：1、Map中取值 2、当前音量 3、最大音量 4、优先级 5、重播次数 6、播放速度
        mSoundPool.play(mSoundPoolMap.get(sound), volume, volume, 1, loop, 1f);
    }
    
    private class UserMoneyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setMoney();
        }
    }

}
