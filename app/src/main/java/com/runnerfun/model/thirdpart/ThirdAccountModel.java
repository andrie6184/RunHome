package com.runnerfun.model.thirdpart;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.runnerfun.RunApplication;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by andrie on 16/11/21.
 */

public class ThirdAccountModel {

    public static final ThirdAccountModel instance = new ThirdAccountModel();

    private Retrofit retrofitApiWeibo = null;
    private Retrofit retrofitApiWeixin = null;
    private OkHttpClient mClient = null;

    private ThirdAccountModel() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(),
                new SharedPrefsCookiePersistor(RunApplication.getAppContex()));

        mClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .cookieJar(cookieJar)
                .build();

        retrofitApiWeibo = new Retrofit.Builder()
                .baseUrl("https://api.weibo.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(mClient)
                .build();

        retrofitApiWeixin = new Retrofit.Builder()
                .baseUrl("https://api.weixin.qq.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(mClient)
                .build();
    }

    private <T> void rxRequest(Observable<T> response, Subscriber<T> callback) {
        response.subscribeOn(Schedulers.io())
                .map(new Func1<T, T>() {
                    @Override
                    public T call(T result) {
                        return result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public Observable<String> getWeiboUserInfo(String token, String uid) {
        WeiboUserInfoRequest request = retrofitApiWeibo.create(WeiboUserInfoRequest.class);
        return request.getUserInfo(token, uid);
    }

    public Observable<String> getWeixinToken(String appid, String secret, String code) {
        WeixinTokenRequest request = retrofitApiWeixin.create(WeixinTokenRequest.class);
        return request.getOauthToken(appid, secret, code, "authorization_code");
    }

    public Observable<String> getWeixinUserInfo(String token, String openId) {
        WeixinUserInfoRequest request = retrofitApiWeixin.create(WeixinUserInfoRequest.class);
        return request.getUserInfo(token, openId);
    }

}
