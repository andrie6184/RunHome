package com.runnerfun;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import com.runnerfun.BaseActivity;
import com.runnerfun.R;
import com.runnerfun.model.ConfigModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RunConfigActivity extends BaseActivity {
    @BindView(R.id.hide_online)
    Switch mOnlineSwitch;
    @BindView(R.id.voice_notice)
    Switch mVoiceSwitch;
    @BindView(R.id.countdown)
    Spinner mCountdownSpiner;
    @BindView(R.id.map_type)
    Spinner mMapType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_config);
        ButterKnife.bind(this);

        mOnlineSwitch.setChecked(ConfigModel.instance.ismHideOnline());
        mVoiceSwitch.setChecked(ConfigModel.instance.ismUserVoice());
        List<String> countdown = new ArrayList<>();
        countdown.add("3");
        countdown.add("2");
        countdown.add("1");
        countdown.add("0");
        mCountdownSpiner.setAdapter(new ArrayAdapter<String>(this, R.layout.dropdown_item, countdown));
        mCountdownSpiner.setSelection(3 - ConfigModel.instance.getmCountDownSecond());

        List<String> mapType = new ArrayList<>();
        mapType.add("标准地图");
        mapType.add("卫星地图");

        mMapType.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_item, mapType));
        mMapType.setSelection(ConfigModel.instance.getmMapType());
    }

    @OnClick(R.id.ok)
    void save(){
        ConfigModel.instance.setmCountDownSecond(3 - mCountdownSpiner.getSelectedItemPosition());
        ConfigModel.instance.setmHideOnline(mOnlineSwitch.isChecked());
        ConfigModel.instance.setmUserVoice(mVoiceSwitch.isChecked());
        ConfigModel.instance.setmMapType(mMapType.getSelectedItemPosition());
        ConfigModel.instance.save(this);
        finish();
    }
}
