package com.runnerfun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.runnerfun.network.NetworkManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

public class UserSettingActivity extends Activity {

    @BindView(R.id.speaking_image)
    ImageView speaking;
    @BindView(R.id.push_image)
    ImageView pushImage;
    @BindView(R.id.voice_image)
    ImageView voiceImage;
    @BindView(R.id.vibrate_image)
    ImageView vibrate;
    @BindView(R.id.count_down_text)
    TextView countDown;

    private AlertDialog countDownDialog;
    final String[] countDownArray = {"3秒", "5秒"};

    private String sid = "-1";

    private boolean speak;
    private boolean push;
    private boolean voice;
    private boolean vib;

    private static final String USER_SPEAK_SETTING = "USER_SPEAK_SETTING";
    private static final String USER_PUSH_SETTING = "USER_PUSH_SETTING";
    private static final String USER_VOICE_SETTING = "USER_VOICE_SETTING";
    private static final String USER_VIB_SETTING = "USER_VIB_SETTING";
    private static final String USER_COUNT_DOWN_SETTING = "USER_COUNT_DOWN_SETTING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        sid = getSettingPrefix();
        if (getSpeakingSetting()) {
            speaking.setImageResource(R.drawable.switch_btn_on);
        } else {
            speaking.setImageResource(R.drawable.switch_btn_off);
        }
        if (getPushSetting()) {
            pushImage.setImageResource(R.drawable.switch_btn_on);
        } else {
            pushImage.setImageResource(R.drawable.switch_btn_off);
        }
        if (getVoiceSetting()) {
            voiceImage.setImageResource(R.drawable.switch_btn_on);
        } else {
            voiceImage.setImageResource(R.drawable.switch_btn_off);
        }
        if (getVibrateSetting()) {
            vibrate.setImageResource(R.drawable.switch_btn_on);
        } else {
            vibrate.setImageResource(R.drawable.switch_btn_off);
        }
        countDown.setText(countDownArray[RunApplication.getAppContex().sharedPreferences.getInt(USER_COUNT_DOWN_SETTING, 0)]);
    }

    @OnClick(R.id.cancel_btn)
    void cancelClicked(View view) {
        finish();
    }

    @OnClick(R.id.speaking_image)
    void speakingClicked(View view) {
        speak = !speak;
        if (speak) {
            speaking.setImageResource(R.drawable.switch_btn_on);
        } else {
            speaking.setImageResource(R.drawable.switch_btn_off);
        }
        RunApplication.getAppContex().sharedPreferences.edit().putBoolean(sid + USER_SPEAK_SETTING, speak).apply();
    }

    @OnClick(R.id.push_image)
    void pushClicked(View view) {
        push = !push;
        if (push) {
            pushImage.setImageResource(R.drawable.switch_btn_on);
        } else {
            pushImage.setImageResource(R.drawable.switch_btn_off);
        }
        RunApplication.getAppContex().sharedPreferences.edit().putBoolean(sid + USER_PUSH_SETTING, push).apply();
    }

    @OnClick(R.id.voice_image)
    void voiceClicked(View view) {
        voice = !voice;
        if (voice) {
            voiceImage.setImageResource(R.drawable.switch_btn_on);
        } else {
            voiceImage.setImageResource(R.drawable.switch_btn_off);
        }
        RunApplication.getAppContex().sharedPreferences.edit().putBoolean(sid + USER_VOICE_SETTING, voice).apply();
    }

    @OnClick(R.id.vibrate_image)
    void vibrateClicked(View view) {
        vib = !vib;
        if (vib) {
            vibrate.setImageResource(R.drawable.switch_btn_on);
        } else {
            vibrate.setImageResource(R.drawable.switch_btn_off);
        }
        RunApplication.getAppContex().sharedPreferences.edit().putBoolean(sid + USER_VIB_SETTING, vib).apply();
    }

    @OnClick(R.id.pwd_modify)
    void pwdModifyClicked(View view) {
        startActivity(new Intent(this, ResetPasswordActivity.class));
    }

    @OnClick(R.id.profile_modify)
    void profileModifyClicked(View view) {
        startActivity(new Intent(this, UserInfoEditActivity.class));
    }

    @OnClick(R.id.count_down)
    void countDownClicked(View view) {
        if (countDownDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请选择倒数时间");
            builder.setItems(countDownArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    countDown.setText(countDownArray[which]);
                    RunApplication.getAppContex().sharedPreferences.edit().putInt(USER_COUNT_DOWN_SETTING, which).apply();
                }
            });
            countDownDialog = builder.create();
        }
        if (countDownDialog.isShowing()) {
            countDownDialog.dismiss();
        }
        countDownDialog.show();
    }

    @OnClick(R.id.user_protocol)
    void userProtocolClicked(View view) {

    }

    @OnClick(R.id.about_us)
    void aboutUsClicked(View view) {

    }

    @OnClick(R.id.logout_btn)
    void logoutBtnClicked(View view) {
        NetworkManager.instance.logout(new Subscriber<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(String s) {
            }
        });
        new SharedPrefsCookiePersistor(RunApplication.getAppContex()).clear();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public static boolean getSpeakingSetting() {
        String sid = "-1";
        if (NetworkManager.instance.hasLoginInfo()) {
            sid = NetworkManager.instance.getUserSid();
        }
        return RunApplication.getAppContex().sharedPreferences.getBoolean(sid + USER_SPEAK_SETTING, false);
    }

    public static boolean getPushSetting() {
        return RunApplication.getAppContex().sharedPreferences.getBoolean(getSettingPrefix() + USER_PUSH_SETTING, true);
    }

    public static boolean getVoiceSetting() {
        return RunApplication.getAppContex().sharedPreferences.getBoolean(getSettingPrefix() + USER_VOICE_SETTING, false);
    }

    public static boolean getVibrateSetting() {
        return RunApplication.getAppContex().sharedPreferences.getBoolean(getSettingPrefix() + USER_VIB_SETTING, true);
    }

    public static int getCountDownSetting() {
        final int[] array = {3, 5};
        int vibrate = RunApplication.getAppContex().sharedPreferences.getInt(USER_COUNT_DOWN_SETTING, 0);
        return array[vibrate];
    }

    private static String getSettingPrefix() {
        String sid = "-1";
        if (NetworkManager.instance.hasLoginInfo()) {
            sid = NetworkManager.instance.getUserSid();
        }
        return sid;
    }

}
