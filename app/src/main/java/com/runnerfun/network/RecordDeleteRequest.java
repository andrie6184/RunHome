package com.runnerfun.network;

import com.runnerfun.beans.ResponseBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * RecordDeleteRequest
 * Created by andrie on 16/12/2.
 */

public interface RecordDeleteRequest {

    @FormUrlEncoded
    @POST("/running/deleterunningrecord")
    Observable<ResponseBean<Object>> delete(@Field("rid") String id);

}
