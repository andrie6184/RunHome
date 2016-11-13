package com.runnerfun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.runnerfun.model.AccountModel;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

    }

    @OnClick(R.id.cancel_btn)
    void cancelClicked(View view) {
        finish();
    }

    @OnClick(R.id.speaking_image)
    void speakingClicked(View view) {

    }

    @OnClick(R.id.push_image)
    void pushClicked(View view) {

    }

    @OnClick(R.id.voice_image)
    void voiceClicked(View view) {

    }

    @OnClick(R.id.vibrate_image)
    void vibrateClicked(View view) {

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

    }

    @OnClick(R.id.user_protocol)
    void userProtocolClicked(View view) {

    }

    @OnClick(R.id.about_us)
    void aboutUsClicked(View view) {

    }

    @OnClick(R.id.logout_btn)
    void logoutBtnClicked(View view) {
        AccountModel.instance.logout(new Subscriber<String>() {
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
        finish();
        // TODO ??? jump where???
    }

}
