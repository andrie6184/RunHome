package com.runnerfun.model;

import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.TotalRunBean;

import retrofit2.http.GET;
import rx.Observable;

/**
 * TotalRunRequest
 * Created by andrie on 16/11/7.
 */

public interface TotalRunRequest {
    @GET("/running/gettotalrunningrecord")
    Observable<ResponseBean<TotalRunBean>> getTotal();
}
