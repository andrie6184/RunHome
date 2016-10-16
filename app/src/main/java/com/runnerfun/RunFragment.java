package com.runnerfun;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lixiaoyang on 16/10/2016.
 */

public class RunFragment extends Fragment {
    @BindView(R.id.main_timer)
    Chronometer mTimer;
    @BindView(R.id.start)
    Button mStartBtn;

    private boolean mIsRunning = false;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_run, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTimer.setFormat("%s");
        mTimer.setBase(SystemClock.elapsedRealtime());
    }

    @OnClick(R.id.start)
    void onClickStart(){
        if(!mIsRunning) {
            mTimer.start();
            mStartBtn.setText("停止");
        }
        else {
            mTimer.stop();
            mStartBtn.setText("开始");
            mTimer.setBase(SystemClock.elapsedRealtime());
        }
        mIsRunning = !mIsRunning;
    }

    @OnClick(R.id.map)
    void onMapClick(){
        Fragment f = getParentFragment();
        if(f instanceof MainFragment){
            ((MainFragment)f).switchToMap();
        }
    }
}
