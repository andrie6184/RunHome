package com.runnerfun.xyzrunpackage;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.runnerfun.R;
import com.runnerfun.RunApplication;
import com.runnerfun.RunConfigActivity;
import com.runnerfun.UserFragment;
import com.runnerfun.beans.UserInfo;
import com.runnerfun.model.ConfigModel;
import com.runnerfun.tools.SpeechUtil;
import com.runnerfun.tools.TimeStringUtils;
import com.runnerfun.tools.UITools;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class RunStateFragment extends Fragment {

    @BindView(R.id.chronometer)
    TextView mClockView;
    @BindView(R.id.money_value)
    TextView mCoin;
    @BindView(R.id.counter)
    TextView mCountDownView;
    @BindView(R.id.counter_bg)
    View mCountDownBG;
    @BindView(R.id.start_panel)
    View mStartPanel;
    @BindView(R.id.stop_panel)
    View mStopPanel;

    private TextView mKmValue;
    private TextView mSpeedValue;
    private TextView mKaclValue;

    private Animation mScaleAnimation = new ScaleAnimation(1.f, 0.f, 1.f, 0.f
            , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

    private LocalBroadcastManager mLocalManager;
    private UserCoinReceiver mReceiver;

    private DecimalFormat decimalFormat = new DecimalFormat("0.000");
    private SpeechUtil mSpeechUtil = new SpeechUtil();

    private Subscription timeSub;
    private Subscription counter;

    public RunStateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_run, container, false);
        ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/dincond.otf");
        Typeface boldTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/dincond-bold.otf");
        mClockView.setTypeface(boldTypeFace);

        setTextFontAndValue(mCoin, typeFace, "0");

        View km = view.findViewById(R.id.km);
        View speed = view.findViewById(R.id.speed);
        View kacl = view.findViewById(R.id.kacl);

        setTextFontAndValue(km, R.id.en_title, typeFace, "km");
        ((TextView) km.findViewById(R.id.value)).setTypeface(typeFace);

        setTextFontAndValue(speed, R.id.en_title, typeFace, "km/s");
        ((TextView) speed.findViewById(R.id.value)).setTypeface(typeFace);

        setTextFontAndValue(kacl, R.id.en_title, typeFace, "kacl");
        ((TextView) kacl.findViewById(R.id.value)).setTypeface(typeFace);

        ((TextView) km.findViewById(R.id.zh_title)).setText("距离");
        ((TextView) speed.findViewById(R.id.zh_title)).setText("配速");
        ((TextView) kacl.findViewById(R.id.zh_title)).setText("消耗");

        mKmValue = (TextView) km.findViewById(R.id.value);
        mSpeedValue = (TextView) speed.findViewById(R.id.value);
        mKaclValue = (TextView) kacl.findViewById(R.id.value);
        mScaleAnimation.setDuration(1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        setMoney();
        readCacheState();
    }

    @Override
    public void onPause() {
        super.onPause();
        endAutoRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalManager.unregisterReceiver(mReceiver);
    }

    @OnClick(R.id.config)
    void onConfigClicked(View view) {
        startActivity(new Intent(getActivity(), RunConfigActivity.class));
    }

    @OnClick(R.id.btn_map)
    void onMapClicked(View view) {
        startActivity(new Intent(getActivity(), RunMapActivity.class));
    }

    @OnClick(R.id.btn_run)
    void onRunClicked(View view) {
        if (RunModel.instance.getState() == RunModel.RUN_STATE_RUNNING
                || RunModel.instance.getState() == RunModel.RUN_STATE_PAUSE) {
            Toast.makeText(getActivity(), "跑步已经开始", Toast.LENGTH_SHORT).show();
            return;
        }
        final int count = ConfigModel.instance.getmCountDownSecond();
        if (count > 0) {
            showCountDown(count);
            if (ConfigModel.instance.ismUserVoice()) {
                mSpeechUtil.speak(String.valueOf(count));
            }
            counter = Observable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            if (aLong >= count - 1) {
                                counter.unsubscribe();
                                mCountDownView.setVisibility(View.GONE);
                                mCountDownBG.setVisibility(View.GONE);

                                doStart();
                                if (ConfigModel.instance.ismUserVoice()) {
                                    mSpeechUtil.speak("跑步开始");
                                }
                            } else {
                                showCountDown(count - aLong - 1);
                                if (ConfigModel.instance.ismUserVoice()) {
                                    mSpeechUtil.speak(String.valueOf(count - aLong - 1));
                                }
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Timber.e(throwable, "RunStateFragment countDown error");
                        }
                    });
        } else {
            doStart();
        }
    }

    @OnClick(R.id.stop_btn)
    void onStopClicked(View view) {
        if (RunModel.instance.getState() == RunModel.RUN_STATE_RUNNING
                || RunModel.instance.getState() == RunModel.RUN_STATE_PAUSE) {
            doStop();
        }
    }

    @OnClick(R.id.resume_btn)
    void onResumeClicked(View view) {
        if (RunModel.instance.getState() == RunModel.RUN_STATE_RUNNING) {
            RunModel.instance.pauseRecord();
            ((ImageView) mStopPanel.findViewById(R.id.resume_btn)).setImageResource(R.drawable.continue_icon);
        } else if (RunModel.instance.getState() == RunModel.RUN_STATE_PAUSE) {
            RunModel.instance.resumeRecord();
            ((ImageView) mStopPanel.findViewById(R.id.resume_btn)).setImageResource(R.drawable.resume);
        }
    }

    private void init() {
        mLocalManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter filter = new IntentFilter(UserFragment.USER_INFO_RELOADED_ACTION);
        mReceiver = new UserCoinReceiver();
        mLocalManager.registerReceiver(mReceiver, filter);
    }

    private void readCacheState() {
        if (RunModel.instance.getState() == RunModel.RUN_STATE_STOP) {
            mStartPanel.setVisibility(View.VISIBLE);
            mStopPanel.setVisibility(View.GONE);
        } else if (RunModel.instance.getState() == RunModel.RUN_STATE_RUNNING
                || RunModel.instance.getState() == RunModel.RUN_STATE_PAUSE) {
            mStartPanel.setVisibility(View.GONE);
            mStopPanel.setVisibility(View.VISIBLE);
            doAutoRefresh();
        }
        if (RunModel.instance.getState() == RunModel.RUN_STATE_RUNNING) {
            ((ImageView) mStopPanel.findViewById(R.id.resume_btn)).setImageResource(R.drawable.resume);
        } else if (RunModel.instance.getState() == RunModel.RUN_STATE_PAUSE) {
            ((ImageView) mStopPanel.findViewById(R.id.resume_btn)).setImageResource(R.drawable.continue_icon);
        }
    }

    public void doStart() {
        mStartPanel.setVisibility(View.GONE);
        mStopPanel.setVisibility(View.VISIBLE);
        ((ImageView) mStopPanel.findViewById(R.id.resume_btn)).setImageResource(R.drawable.resume);
        RunRecordService.startRun(getActivity());
        doAutoRefresh();
        getActivity().startActivity(new Intent(getActivity(), RunMapActivity.class));
    }

    public void doStop() {
        mStartPanel.setVisibility(View.VISIBLE);
        mStopPanel.setVisibility(View.GONE);
        RunRecordService.stopRun(getActivity());
        endAutoRefresh();
    }

    public void doAutoRefresh() {
        if (timeSub == null) {
            timeSub = Observable.interval(1000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            refreshResult();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Timber.e(throwable, "RunFragment onResume error");
                        }
                    });
        }
    }

    public void endAutoRefresh() {
        if (timeSub != null) {
            timeSub.unsubscribe();
            timeSub = null;
        }
    }

    private void refreshResult() {
        mClockView.setText(TimeStringUtils.getTime(RunModel.instance.getRecordTime()));
        mKmValue.setText(decimalFormat.format(RunModel.instance.getDistance() / 1000));
        mKaclValue.setText(UITools.numberFormat(RunModel.instance.getCalorie() / 1000));

        if (RunModel.instance.getRecordTime() > 0 && RunModel.instance.getDistance() > 0) {
            float seconds = RunModel.instance.getRecordTime() / RunModel.instance.getDistance();
            int minutes = (int) seconds / 60;
            String speedShow = String.format(Locale.getDefault(), "%d'%d\"", minutes, (int) (seconds % 60));
            mSpeedValue.setText(speedShow);
        } else {
            mSpeedValue.setText("0'00\"");
        }
    }

    private void showCountDown(long second) {
        mCountDownView.setVisibility(View.VISIBLE);
        mCountDownBG.setVisibility(View.VISIBLE);

        mCountDownView.setText(String.valueOf(second));
        mCountDownView.clearAnimation();
        mCountDownView.startAnimation(mScaleAnimation);
    }

    private void setMoney() {
        String info = RunApplication.getAppContex().sharedPreferences
                .getString(UserFragment.SP_KEY_USER_INFO, "");
        if (!TextUtils.isEmpty(info)) {
            UserInfo userInfo = new Gson().fromJson(info, UserInfo.class);
            mCoin.setText(userInfo.getTotal_score());
        }
    }

    private void setTextFontAndValue(View rootView, int id, Typeface typeface, String value) {
        setTextFontAndValue(((TextView) rootView.findViewById(id)), typeface, value);
    }

    private void setTextFontAndValue(TextView textView, Typeface typeface, String value) {
        textView.setTypeface(typeface);
        textView.setText(value);
    }

    private class UserCoinReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setMoney();
        }
    }

}
