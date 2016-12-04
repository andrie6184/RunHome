package com.runnerfun.network;

import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.UserInfo;

import retrofit2.http.GET;
import rx.Observable;

/**
 * UserInfoRequest
 * Created by andrie on 16/11/4.
 */

public interface UserInfoRequest {
    @GET("/user/show")
    Observable<ResponseBean<UserInfo>> getUserInfo();
}
