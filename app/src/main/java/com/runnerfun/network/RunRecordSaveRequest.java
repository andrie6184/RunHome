package com.runnerfun.network;

import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.RunSaveResultBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * RunRecordSaveRequest
 * Created by andrie on 16/12/9.
 */

public interface RunRecordSaveRequest {
    @FormUrlEncoded
    @POST("/running/save")
    Observable<ResponseBean<RunSaveResultBean>> saveRecord(@Field("data") String data);
}
