package com.runnerfun.network;

import com.runnerfun.beans.RegisterInfo;
import com.runnerfun.beans.ResponseBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * RegisterRequest
 * Created by andrie on 13/10/2016.
 */

public interface RegisterRequest {
    @GET("/user/register")
    Observable<ResponseBean<RegisterInfo>> register(@Query(value = "tel") String tel,
                                                    @Query(value = "pwd") String pwd,
                                                    @Query(value = "code") String code);
}
