package com.runnerfun;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.runnerfun.beans.LoginBean;
import com.runnerfun.model.AccountModel;
import com.runnerfun.tools.ThirdPartAuthManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

public class LoginActivity extends AppCompatActivity implements ThirdPartAuthManager.ThirdPartActionListener {
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
    void login(final View view) {
        String tel = mTel.getText().toString();
        String pwd = mPassword.getText().toString();

        view.setEnabled(false);
        view.setClickable(false);
        AccountModel.instance.login(tel, pwd, new Subscriber<LoginBean>() {
            @Override
            public void onCompleted() {
                view.setEnabled(true);
                view.setClickable(true);
            }

            @Override
            public void onError(Throwable e) {
                view.setEnabled(true);
                view.setClickable(true);
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(LoginBean loginBean) {
                // Toast.makeText(LoginActivity.this, "login success" + loginBean.getSid(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
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
        ThirdPartAuthManager.instance().startWeixinLogin(this, this);
    }

    @OnClick(R.id.login_qq)
    void qqLogin() {
        ThirdPartAuthManager.instance().startQQLogin(this, this);
    }

    @OnClick(R.id.login_weibo)
    void weiboLogin() {
        ThirdPartAuthManager.instance().startWeiboLogin(this, this);
    }

    @Override
    public void onSuccess(int action, int type, boolean isFirst) {
        if (action == ThirdPartAuthManager.ACTION_TAG_LOGIN) {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("isFirstLogin", isFirst);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onFailed(int action, int type, String result) {
        if (action == ThirdPartAuthManager.ACTION_TAG_LOGIN) {
            Toast.makeText(this, "第三方登录失败,请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCanceled(int action, int type) {
        if (action == ThirdPartAuthManager.ACTION_TAG_LOGIN) {
            Toast.makeText(this, "取消第三方登录", Toast.LENGTH_SHORT).show();
        }
    }

}
