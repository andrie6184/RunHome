package com.runnerfun;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserInfoEditActivity extends Activity {

    @BindView(R.id.user_avatar)
    ImageView avatar;
    @BindView(R.id.nick_name_text)
    TextView nickName;
    @BindView(R.id.signature_text)
    TextView signature;
    @BindView(R.id.gender_text)
    TextView gender;
    @BindView(R.id.age_text)
    TextView age;
    @BindView(R.id.height_text)
    TextView height;
    @BindView(R.id.weight_text)
    TextView weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

    }

    @OnClick(R.id.user_avatar)
    void avatarClicked(View view) {

    }

    @OnClick(R.id.nick_name)
    void nickNameClicked(View view) {

    }

    @OnClick(R.id.personal_signature)
    void signatureClicked(View view) {

    }

    @OnClick(R.id.gender)
    void genderClicked(View view) {

    }

    @OnClick(R.id.age)
    void ageClicked(View view) {

    }

    @OnClick(R.id.height)
    void heightClicked(View view) {

    }

    @OnClick(R.id.weight)
    void weightClicked(View view) {

    }

}
