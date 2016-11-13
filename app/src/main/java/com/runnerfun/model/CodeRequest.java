package com.runnerfun.model;

import com.runnerfun.beans.ResponseBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by andrie on 13/10/2016.
 */

public interface CodeRequest {
    @GET("/user/sendcode")
    Observable<ResponseBean<String>> sendCode(@Query(value = "tel") String tel,
                                              @Query(value = "type") int type,
                                              @Query(value = "sign") String sign);
}
