package com.runnerfun.model;


import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.runnerfun.RunApplication;
import com.runnerfun.beans.CoinBean;
import com.runnerfun.beans.LoginBean;
import com.runnerfun.beans.RegisterInfo;
import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.RunRecordBean;
import com.runnerfun.beans.RunTotalBean;
import com.runnerfun.beans.RunWeekBean;
import com.runnerfun.beans.UploadResult;
import com.runnerfun.beans.UserInfo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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
 * AccountModel
 * Created by andrie on 12/10/2016.
 */

public class AccountModel {

    public static final AccountModel instance = new AccountModel();

    private Retrofit retrofitApi = null;
    private Retrofit retrofitIP = null;
    private OkHttpClient mClient = null;

    public static final int COMMON_PAGE_SIZE = 20;

    private static final String KEY = "Paobuzhijia@163$";

    private AccountModel() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(),
                new SharedPrefsCookiePersistor(RunApplication.getAppContex()));

        mClient = new OkHttpClient.Builder()
                .addInterceptor(new RunCommonParamsInterceptor())
                .addInterceptor(logging)
                .cookieJar(cookieJar)
                .build();

        retrofitApi = new Retrofit.Builder()
                .baseUrl("http://api.paobuzhijia.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(mClient)
                .build();

        retrofitIP = new Retrofit.Builder()
                .baseUrl("http://101.200.180.172/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(mClient)
                .build();
    }

    public void login(String tel, String pwd, Subscriber<LoginBean> callback) {
        String code = toMD5(KEY + tel + pwd);
        LoginRequest request = retrofitApi.create(LoginRequest.class);
        rxRequest(request.login(tel, toMD5(pwd), code), callback);
    }

    public void logout(Subscriber<String> callback) {
        LogoutRequest request = retrofitApi.create(LogoutRequest.class);
        rxRequest(request.logout(), callback);
    }

    public void register(String tel, String pwd, String code, Subscriber<RegisterInfo> callback) {
        RegisterRequest request = retrofitApi.create(RegisterRequest.class);
        rxRequest(request.register(tel, pwd, code), callback);
    }

    public void sendCode(String tel, int type, Subscriber<String> callback) {
        String code = toMD5(KEY + tel + type);
        CodeRequest request = retrofitApi.create(CodeRequest.class);
        rxRequest(request.sendCode(tel, type, code), callback);
    }

    public void updateUserInfo(String name, int age, String headimg, String remarks, String sexy, int height, Subscriber<String> callback) {
        UserEditRequest request = retrofitApi.create(UserEditRequest.class);
        rxRequest(request.editUserInfo(name, age, headimg, remarks, height, sexy), callback);
    }

    public void getUserInfo(Subscriber<UserInfo> callback) {
        UserInfoRequest request = retrofitApi.create(UserInfoRequest.class);
        rxRequest(request.getUserInfo(), callback);
    }

    public void getUserCoins(int type, int page, Subscriber<CoinBean> callback) {
        CoinRequest request = retrofitApi.create(CoinRequest.class);
        rxRequest(request.list(type, page, COMMON_PAGE_SIZE), callback);
    }

    public void uploadAvatar(byte[] file, Subscriber<UploadResult> callback) {
        UploadAvatarRequest request = retrofitApi.create(UploadAvatarRequest.class);
        RequestBody pic = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        rxRequest(request.uploadAvatar(pic), callback);
    }

    public void getUserPRecordList(int page, Subscriber<ArrayList<RunRecordBean>> callback) {
        RunListsRequest request = retrofitApi.create(RunListsRequest.class);
        rxRequest(request.getRunLists(page), callback);
    }

    public void getUserWRecordList(Subscriber<ArrayList<RunWeekBean>> callback) {
        RunWeekRequest request = retrofitApi.create(RunWeekRequest.class);
        rxRequest(request.getWeek(), callback);
    }

    public void getUserTRecordList(Subscriber<RunTotalBean> callback) {
        RunTotalRequest request = retrofitApi.create(RunTotalRequest.class);
        rxRequest(request.getTotal(), callback);
    }

    public boolean hasLoginInfo() {
        List<Cookie> cookies = mClient.cookieJar().loadForRequest(HttpUrl.parse("http://api.paobuzhijia.com/"));
        // List<Cookie> cookies = new SharedPrefsCookiePersistor(RunApplication.getAppContex()).loadAll();

        if (cookies.size() > 0) {
            for (Cookie c : cookies) {
                if (c.name().equals("sid")) {
                    return true;
                }
            }
        }
        return false;
    }

    private <T> void rxRequest(Observable<ResponseBean<T>> response, Subscriber<T> callback) {
        response.subscribeOn(Schedulers.io())
                .map(new Func1<ResponseBean<T>, T>() {
                    @Override
                    public T call(ResponseBean<T> result) {
                        if (result.getCode() != 0) {
                            throw new IllegalArgumentException(result.getMsg());
                        }
                        return result.getData();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static String toMD5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    private MultipartBody addParamsToMultipartBody(MultipartBody body) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("ver", "20B100");

        //add original body
        for (int i = 0; i < body.size(); i++) {
            builder.addPart(body.part(i));
        }
        return builder.build();
    }

    private FormBody addParamsToFormBody(FormBody body) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("ver", "20B100");

        //add original body
        for (int i = 0; i < body.size(); i++) {
            builder.addEncoded(body.encodedName(i), body.encodedValue(i));
        }
        return builder.build();
    }

    private class RunCommonParamsInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request oriRequest = chain.request();
            RequestBody body = oriRequest.body();
            RequestBody newBody = null;
            if (body instanceof FormBody) {
                newBody = addParamsToFormBody((FormBody) body);
            } else if (body instanceof MultipartBody) {
                newBody = addParamsToMultipartBody((MultipartBody) body);
            }
            Request request = oriRequest.newBuilder().method(oriRequest.method(), newBody).build();
            return chain.proceed(request);
        }
    }

}
