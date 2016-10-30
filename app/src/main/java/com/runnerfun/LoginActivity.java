package com.runnerfun;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.runnerfun.beans.LoginBean;
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

    @OnClick(R.id.login_btn)
    void login() {
        String tel = mTel.getText().toString();
        String pwd = mPassword.getText().toString();
        String code = mPassword.getText().toString();
        AccountModel.instance.login(tel, pwd, new Subscriber<LoginBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(LoginActivity.this, "login failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(LoginBean loginBean) {
                Toast.makeText(LoginActivity.this, "login success" + loginBean.getSid(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }

    @OnClick(R.id.register_btn)
    void register() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @OnClick(R.id.forgot_password)
    void resetPassword() {
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
    }

    @OnClick(R.id.login_weixin)
    void weixinLogin() {
        // TODO
    }

    @OnClick(R.id.login_qq)
    void qqLogin() {
        // TODO
    }

    @OnClick(R.id.login_weibo)
    void weiboLogin() {
        // TODO
    }

}
