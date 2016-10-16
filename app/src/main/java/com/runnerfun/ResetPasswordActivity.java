package com.runnerfun;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.runnerfun.beans.CodeBean;
import com.runnerfun.beans.RegisterInfo;
import com.runnerfun.model.AccountModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * Created by lixiaoyang on 05/10/2016.
 */

public class ResetPasswordActivity extends AppCompatActivity {
    @BindView(R.id.tel)
    EditText mTel;
    @BindView(R.id.code)
    EditText mCode;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.confirm)
    EditText mConfirm;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("密码重置");
    }


    @OnClick(R.id.send_code)
    void sendCode(){
        String tel = mTel.getText().toString();

        AccountModel.instance.sendCode(tel, 2, new Subscriber<CodeBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(ResetPasswordActivity.this, "send code fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(CodeBean codeBean) {
                Toast.makeText(ResetPasswordActivity.this, "send code success" + codeBean.msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.register)
    void resetPwd(){
        String tel = mTel.getText().toString();
        String code = mCode.getText().toString();
        String password = mPassword.getText().toString();
        String confirm = mConfirm.getText().toString();

        if(!confirm.equals(password)){
            Toast.makeText(ResetPasswordActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        AccountModel.instance.regiseter(tel, password, code, new Subscriber<RegisterInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(ResetPasswordActivity.this, "register fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(RegisterInfo registerInfo) {
                Toast.makeText(ResetPasswordActivity.this, "register success"+ registerInfo.msg, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ResetPasswordActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
