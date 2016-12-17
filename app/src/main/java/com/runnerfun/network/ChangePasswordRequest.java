package com.runnerfun.network;

import com.runnerfun.beans.ResponseBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * ChangePasswordRequest
 * Created by andrie on 16/12/17.
 */

public interface ChangePasswordRequest {
    @GET("/user/changepwd")
    Observable<ResponseBean<Object>> changePwd(@Query(value = "tel") String tel,
                                               @Query(value = "code") String code,
                                               @Query(value = "pwd") String pwd,
                                               @Query(value = "pwd1") String pwd1,
                                               @Query(value = "sign") String sign);
}
