package com.runnerfun.model;

import com.runnerfun.beans.ResponseBean;

import retrofit2.http.Field;
import retrofit2.http.POST;
import rx.Observable;

/**
 * RunDeleteRequest
 * Created by andrie on 16/11/7.
 */

public interface RunDeleteRequest {
    @POST("/running/deleterunningrecord")
    Observable<ResponseBean<String>> deleteRunRecord(@Field("rids") int rids);
}
