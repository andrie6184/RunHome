package com.runnerfun.model;

import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.RunTotalBean;

import retrofit2.http.GET;
import rx.Observable;

/**
 * RunTotalRequest
 * Created by andrie on 16/11/7.
 */

public interface RunTotalRequest {
    @GET("/running/gettotalrunningrecord")
    Observable<ResponseBean<RunTotalBean>> getTotal();
}
