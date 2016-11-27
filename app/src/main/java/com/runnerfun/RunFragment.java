package com.runnerfun;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.runnerfun.model.RecordModel;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
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

    private  Subscription mTimer = null;
    private Subscription mCounter = null;
    private Animation mScaleAnimation = new ScaleAnimation(1.f, 0.f, 1.f, 0.f
            , Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_run, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/dincond.otf");
        Typeface boldTypeFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/dincond-bold.otf");
        mClockView.setTypeface(boldTypeFace);
        mMoney.setTypeface(typeFace);
        mMoney.setText("700");

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
    }

    @OnClick(R.id.btn_map)
    void map(){
        startActivity(new Intent(getActivity(), MapActivity.class));
    }

    @OnClick(R.id.btn_run)
    void start(){
        mCountDownView.setText("");
        mCountDownView.setVisibility(View.VISIBLE);
        //TODO:3,2,1 动画
        if(mCounter != null){
            mCounter.unsubscribe();
        }
        mCounter = Observable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if(aLong >= 3){
                            mCounter.unsubscribe();
                            mCountDownView.setVisibility(View.GONE);
                            doStart();
                        }
                        else{
                            mCountDownView.setText(String.valueOf(3 - aLong));
                            mCountDownView.clearAnimation();
                            mCountDownView.startAnimation(mScaleAnimation);
                        }
                    }
                });
       // startActivity(new Intent(getActivity(), MapActivity.class));
    }

    private void doStart(){
        RecordService.startRecord(getActivity());
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
        if(mTimer != null){
            mTimer.unsubscribe();
        }
    }

    private void refreshResult(){
        mClockView.setText(msToString(RecordModel.instance.getRecordTime()));
        mKmValue.setText(String.valueOf((int)RecordModel.instance.getDistance()));
        mKaclValue.setText(String.valueOf((int)RecordModel.instance.getCal()));
        mSpeedValue.setText(String.valueOf((int)RecordModel.instance.getSpeed()));
    }

    private String msToString(long ms){
        long hour = ms / 3600;
        long minute = (ms % 3600) / 60;
        long second = (ms % 3600 % 60);

        Log.d("TIMER", "current time = " + ms / 1000);
        return String.valueOf(ms / 1000);
    }

    @OnClick(R.id.config)
    void mapConfig(){
        startActivity(new Intent(getActivity(), ShareActivity.class));
        //TODO:startActivity(new Intent(getActivity(), MapConfigActivity.class));
    }


}
