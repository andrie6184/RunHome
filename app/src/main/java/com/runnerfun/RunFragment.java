package com.runnerfun;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lixiaoyang on 16/10/2016.
 */

public class RunFragment extends Fragment {
    @BindView(R.id.chronometer)
    Chronometer mChronometer;
    private TextView mKmValue;
    private TextView mSpeedValue;
    private TextView mKaclValue;
    @BindView(R.id.money_value)
    TextView mMoney;

    private boolean mIsRunning = false;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_run, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mChronometer.setFormat("11:44:77");
        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/dincond.otf");
        Typeface boldTypeFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/dincond-bold.otf");
        mChronometer.setTypeface(boldTypeFace);
        mChronometer.setBase(SystemClock.elapsedRealtime());
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
    }

    @OnClick(R.id.btn_run)
    void start(){
        Fragment f = getParentFragment();
        if(f instanceof MainFragment){
            ((MainFragment)f).switchToMap();
        }
    }

}
