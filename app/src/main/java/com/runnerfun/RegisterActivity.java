package com.runnerfun;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.runnerfun.beans.RegisterInfo;
import com.runnerfun.model.AccountModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.tel)
    EditText mTel;
    @BindView(R.id.code)
    EditText mCode;
    @BindView(R.id.password)
    EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("注册");
    }

    @OnClick(R.id.get_code)
    void getCode() {
        String tel = mTel.getText().toString();
        AccountModel.instance.sendCode(tel, 1, new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(RegisterActivity.this, "get code fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(String codeBean) {
                Toast.makeText(RegisterActivity.this, "get code success" + codeBean, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.register)
    void register() {
        String tel = mTel.getText().toString();
        String code = mCode.getText().toString();
        String pwd = mPassword.getText().toString();
        AccountModel.instance.register(tel, pwd, code, new Subscriber<RegisterInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(RegisterActivity.this, "register fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(RegisterInfo registerInfo) {
                Toast.makeText(RegisterActivity.this, "register success" + registerInfo, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, UserInfoActivity.class));
            }
        });
    }

    @OnClick(R.id.back_btn)
    void back() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
