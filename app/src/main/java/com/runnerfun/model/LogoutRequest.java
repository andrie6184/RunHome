package com.runnerfun.model;

import com.runnerfun.beans.ResponseBean;

import retrofit2.http.GET;
import rx.Observable;

/**
 * LogoutRequest
 * Created by andrie on 16/10/27.
 */

public interface LogoutRequest {
    @GET("/user/logout")
    Observable<ResponseBean<String>> logout();
}
