package com.runnerfun;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.runnerfun.beans.RegisterInfo;
import com.runnerfun.model.AccountModel;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.tel)
    EditText mTel;
    @BindView(R.id.code)
    EditText mCode;
    @BindView(R.id.password)
    EditText mPassword;

    private Long mHourGlass = 60L * 1000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.get_code)
    void getCode(final Button view) {
        String tel = mTel.getText().toString();
        if (TextUtils.isEmpty(tel)) {
            Toast.makeText(RegisterActivity.this, "请先输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        view.setEnabled(false);
        view.setClickable(false);
        AccountModel.instance.sendCode(tel, 1, new Subscriber<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                view.setEnabled(true);
                view.setClickable(true);
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(String codeBean) {
                // Toast.makeText(RegisterActivity.this, "get code success" + codeBean, Toast.LENGTH_SHORT).show();
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

    @OnClick(R.id.register)
    void register(final View view) {
        String tel = mTel.getText().toString();
        String code = mCode.getText().toString();
        String pwd = mPassword.getText().toString();

        view.setEnabled(false);
        view.setClickable(false);
        AccountModel.instance.register(tel, pwd, code, new Subscriber<RegisterInfo>() {
            @Override
            public void onCompleted() {
                view.setEnabled(true);
                view.setClickable(true);
            }

            @Override
            public void onError(Throwable e) {
                view.setEnabled(true);
                view.setClickable(true);
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(RegisterInfo registerInfo) {
                Toast.makeText(RegisterActivity.this, "注册成功, 已为您登录" + registerInfo, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, InitUserInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @OnClick(R.id.back_btn)
    void back() {
        finish();
    }

}
