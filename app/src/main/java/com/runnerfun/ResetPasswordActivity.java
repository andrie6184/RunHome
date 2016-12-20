package com.runnerfun;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.runnerfun.network.NetworkManager;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * ResetPasswordActivity
 * Created by andrie on 05/10/2016.
 */

public class ResetPasswordActivity extends BaseActivity {
    @BindView(R.id.tel)
    EditText mTel;
    @BindView(R.id.code)
    EditText mCode;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.confirm)
    EditText mConfirm;

    private Long mHourGlass = 60L * 1000L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.send_code)
    void sendCode(final Button view) {
        String tel = mTel.getText().toString();
        if (TextUtils.isEmpty(tel)) {
            Toast.makeText(ResetPasswordActivity.this, "请先输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        view.setEnabled(false);
        view.setClickable(false);
        NetworkManager.instance.sendCode(tel, 2, new Subscriber<Object>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                view.setEnabled(true);
                view.setClickable(true);
                Toast.makeText(ResetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Object codeBean) {
                Toast.makeText(ResetPasswordActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                mHourGlass = 60L * 1000L;
                _subscription = Observable.interval(1000, 1000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                mHourGlass -= 1000;
                                if (mHourGlass == 0) {
                                    _subscription.unsubscribe();
                                    view.setText("获取验证码");
                                    view.setEnabled(true);
                                    view.setClickable(true);
                                } else {
                                    view.setText(String.format(Locale.getDefault(), "%d秒后重发", mHourGlass / 1000));
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                view.setText("获取验证码");
                                view.setEnabled(true);
                                view.setClickable(true);
                            }
                        });
            }
        });
    }

    @OnClick(R.id.resetPwd)
    void resetPwd(final View view) {
        final String tel = mTel.getText().toString();
        String code = mCode.getText().toString();
        String password = mPassword.getText().toString();
        String confirm = mConfirm.getText().toString();

        if (!confirm.equals(password)) {
            Toast.makeText(ResetPasswordActivity.this, "确认密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        view.setEnabled(false);
        view.setClickable(false);
        NetworkManager.instance.changePwd(tel, code, password, confirm, new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                view.setEnabled(true);
                view.setClickable(true);
            }

            @Override
            public void onError(Throwable e) {
                view.setEnabled(true);
                view.setClickable(true);
                Toast.makeText(ResetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Object info) {
                Toast.makeText(ResetPasswordActivity.this, "密码重置成功,请使用新密码登录", Toast.LENGTH_SHORT).show();
                NetworkManager.instance.clearLoginInfo();
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @OnClick(R.id.back_btn)
    void onBackClick(View view) {
        finish();
    }

}
