package com.runnerfun.model;

import com.runnerfun.beans.LoginBean;
import com.runnerfun.beans.ResponseBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by lixiaoyang on 04/10/2016.
 */

public interface LoginRequest {
    @GET("/user/login")
    Observable<ResponseBean<LoginBean>> login(@Query(value = "tel") String tel,
                                              @Query(value = "pwd") String pwd,
                                              @Query(value = "sign") String sign);
}
