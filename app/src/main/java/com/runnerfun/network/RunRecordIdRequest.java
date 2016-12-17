package com.runnerfun.network;

import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.RunIdBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * RunRecordIdRequest
 * Created by andrie on 04/12/2016.
 */

public interface RunRecordIdRequest {
    @POST("running/idgen")
    @FormUrlEncoded
    Observable<ResponseBean<RunIdBean>> getRecordId(@Field("cookie") String token);
}
