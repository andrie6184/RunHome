package com.runnerfun.model;

import com.runnerfun.beans.LoginInfo;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by lixiaoyang on 04/10/2016.
 */

public interface LoginRequest {
    @GET("/user/login")
    Observable<LoginInfo> login(@Query(value = "tel") String tel,
                                @Query(value = "pwd") String pwd,
                                @Query(value = "sign") String sign);
}
