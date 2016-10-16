package com.runnerfun.model;

import com.runnerfun.beans.RegisterInfo;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by lixiaoyang on 13/10/2016.
 */

public interface RegisterRequest {
    @GET("/user/register")
    Observable<RegisterInfo> register(@Query(value = "tel") String tel,
                                      @Query(value = "pwd") String pwd,
                                      @Query(value = "code") String code);
}
