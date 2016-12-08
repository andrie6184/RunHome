package com.runnerfun;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

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
    ImageView mVoiceSwitch;
    @BindView(R.id.countdown)
    Spinner mCountdownSpiner;
    @BindView(R.id.map_type)
    Spinner mMapType;

    boolean isOpenVoice = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_config);
        setTheme(R.style.Transparent);
        ButterKnife.bind(this);

        mOnlineSwitch.setChecked(ConfigModel.instance.ismHideOnline());
        if (ConfigModel.instance.ismUserVoice()) {
            mVoiceSwitch.setImageResource(R.drawable.switch_btn_on);
        } else {
            mVoiceSwitch.setImageResource(R.drawable.switch_btn_off);
        }
        isOpenVoice = ConfigModel.instance.ismUserVoice();
        List<String> countdown = new ArrayList<>();
        countdown.add("10秒");
        countdown.add("5秒");
        countdown.add("3秒");
        countdown.add("不倒数");
        mCountdownSpiner.setAdapter(new ArrayAdapter<String>(this, R.layout.dropdown_item, countdown));
        mCountdownSpiner.setSelection(countdown.indexOf(getCountDownString(
                ConfigModel.instance.getmCountDownSecond())));

        List<String> mapType = new ArrayList<>();
        mapType.add("标准地图");
        mapType.add("卫星地图");

        mMapType.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_item, mapType));
        mMapType.setSelection(ConfigModel.instance.getmMapType());
    }

    @OnClick(R.id.ok)
    void save() {
        ConfigModel.instance.setmCountDownSecond(getCountDownSetting(mCountdownSpiner.getSelectedItemPosition()));
        ConfigModel.instance.setmHideOnline(mOnlineSwitch.isChecked());
        ConfigModel.instance.setmUserVoice(isOpenVoice);
        ConfigModel.instance.setmMapType(mMapType.getSelectedItemPosition());
        ConfigModel.instance.save(this);
        finish();
    }

    @OnClick(R.id.voice_notice)
    void onVoiceClicked(View view) {
        isOpenVoice = !isOpenVoice;
        if (isOpenVoice) {
            mVoiceSwitch.setImageResource(R.drawable.switch_btn_on);
        } else {
            mVoiceSwitch.setImageResource(R.drawable.switch_btn_off);
        }
    }

    private int getCountDownSetting(int selection) {
        final int[] array = {10, 5, 3, 0};
        return array[selection];
    }

    private String getCountDownString(int countSecond) {
        if (countSecond > 0) {
            return countSecond + "秒";
        } else {
            return "不倒数";
        }
    }

}
