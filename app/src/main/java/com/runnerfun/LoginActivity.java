package com.runnerfun;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.runnerfun.beans.LoginBean;
import com.runnerfun.network.NetworkManager;
import com.runnerfun.tools.ThirdpartAuthManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

public class LoginActivity extends BaseActivity implements ThirdpartAuthManager.ThirdPartActionListener {
    @BindView(R.id.tel)
    EditText mTel;
    @BindView(R.id.password)
    EditText mPassword;

    @BindView(R.id.loading_view)
    RelativeLayout loading;

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
        loading.setVisibility(View.VISIBLE);

        view.setEnabled(false);
        view.setClickable(false);
        NetworkManager.instance.login(tel, pwd, new Subscriber<LoginBean>() {
            @Override
            public void onCompleted() {
                loading.setVisibility(View.GONE);
                view.setEnabled(true);
                view.setClickable(true);
            }

            @Override
            public void onError(Throwable e) {
                loading.setVisibility(View.GONE);
                view.setEnabled(true);
                view.setClickable(true);
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(LoginBean loginBean) {
                // Toast.makeText(LoginActivity.this, "login success" + loginBean.getSid(), Toast.LENGTH_SHORT).show();
                NetworkManager.instance.setLoginInfo();
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
        loading.setVisibility(View.VISIBLE);
        ThirdpartAuthManager.instance().startWeixinLogin(this, this);
    }

    @OnClick(R.id.login_qq)
    void qqLogin() {
        loading.setVisibility(View.VISIBLE);
        ThirdpartAuthManager.instance().startQQLogin(this, this);
    }

    @OnClick(R.id.login_weibo)
    void weiboLogin() {
        loading.setVisibility(View.VISIBLE);
        ThirdpartAuthManager.instance().startWeiboLogin(this, this);
    }

    @Override
    public void onSuccess(int action, int type, boolean isFirst) {
        loading.setVisibility(View.GONE);
        if (action == ThirdpartAuthManager.ACTION_TAG_LOGIN) {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("isFirstLogin", isFirst);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onFailed(int action, int type, String result) {
        loading.setVisibility(View.GONE);
        if (action == ThirdpartAuthManager.ACTION_TAG_LOGIN) {
            Toast.makeText(this, "第三方登录失败,请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCanceled(int action, int type) {
        loading.setVisibility(View.GONE);
        if (action == ThirdpartAuthManager.ACTION_TAG_LOGIN) {
            Toast.makeText(this, "取消第三方登录", Toast.LENGTH_SHORT).show();
        }
    }

}
