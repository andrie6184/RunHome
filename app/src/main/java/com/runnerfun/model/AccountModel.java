package com.runnerfun.model;


import com.runnerfun.beans.CodeBean;
import com.runnerfun.beans.LoginInfo;
import com.runnerfun.beans.RegisterInfo;
import com.runnerfun.beans.UserBean;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lixiaoyang on 12/10/2016.
 */

public class AccountModel {
    public static final AccountModel instance = new AccountModel();

    private AccountModel() {
    }

    private Retrofit retrofitApi = new Retrofit.Builder()
            .baseUrl("http://api.paobuzhijia.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();

    private Retrofit retrofitIP = new Retrofit.Builder()
            .baseUrl("http://101.200.180.172/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();


    public void login(String tel, String pwd, Subscriber<LoginInfo> callback) {
        String code = md5(tel + pwd);
        LoginRequest request = retrofitApi.create(LoginRequest.class);
        request.login(tel, pwd, code).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public void regiseter(String tel, String pwd, String code, Subscriber<RegisterInfo> callback) {
        RegisterRequest request = retrofitIP.create(RegisterRequest.class);
        request.register(tel, pwd, code).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public void sendCode(String tel, int type, Subscriber<CodeBean> callback) {
        String code = md5(tel + type);
        CodeRequest request = retrofitApi.create(CodeRequest.class);
        request.sendCode(tel, type, code).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public void updateUserInfo(String name, int age, String headimg, String remarks, String sexy, int height, Subscriber<UserBean> callback) {
        UserInfoRequest request = retrofitApi.create(UserInfoRequest.class);
        request.register(name, age, headimg, remarks, height, sexy).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public boolean hasLoginInfo() {
        return false;
    }

    private String md5(String info) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes());
            byte[] m = md5.digest();//加密
            return new String(m);
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }
}
