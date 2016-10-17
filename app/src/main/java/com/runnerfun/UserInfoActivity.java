package com.runnerfun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.runnerfun.beans.RegisterInfo;
import com.runnerfun.beans.UserBean;
import com.runnerfun.beans.UserInfo;
import com.runnerfun.model.AccountModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

public class UserInfoActivity extends AppCompatActivity {
    private boolean mIsMale = true;
    @BindView(R.id.age)
    EditText mAge;
    @BindView(R.id.height)
    EditText mHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);

        ((RadioGroup)findViewById(R.id.sex)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mIsMale = checkedId == R.id.male;
            }
        });
    }

    @OnClick(R.id.skip)
    void skip(){
        startActivity(new Intent(UserInfoActivity.this, MainActivity.class));
    }

    @OnClick(R.id.next)
    void next(){
        if(mAge.getText().length() <= 0 || mHeight.getText().length() <= 0){
            Toast.makeText(UserInfoActivity.this, "年龄和身高不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        int age = Integer.parseInt(mAge.getText().toString());
        int height = Integer.parseInt(mHeight.getText().toString());
        AccountModel.instance.updateUserInfo("", age, "", "", mIsMale ? "英雄" : "美人", height, new Subscriber<UserInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(UserInfoActivity.this, "user info error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(UserInfo registerInfo) {
                finish();
                startActivity(new Intent(UserInfoActivity.this, MainActivity.class));
            }
        });

    }
}
