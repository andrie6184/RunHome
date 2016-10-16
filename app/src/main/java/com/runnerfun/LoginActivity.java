package com.runnerfun;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.runnerfun.beans.LoginInfo;
import com.runnerfun.model.AccountModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.tel)
    EditText mTel;
    @BindView(R.id.password)
    EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login)
    void login() {
        String tel = mTel.getText().toString();
        String pwd = mPassword.getText().toString();
        String code = mPassword.getText().toString();
        AccountModel.instance.login(tel, pwd, new Subscriber<LoginInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(LoginActivity.this, "login failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(LoginInfo loginInfo) {
                Toast.makeText(LoginActivity.this, "login success" + loginInfo.getMsg(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }

    @OnClick(R.id.register)
    void register() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @OnClick(R.id.forgot_password)
    void resetPassword() {
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
    }

}
