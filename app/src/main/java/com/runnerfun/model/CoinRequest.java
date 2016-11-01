package com.runnerfun.model;

import com.runnerfun.beans.CoinBean;
import com.runnerfun.beans.ResponseBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * CoinRequest
 * Created by andrie on 16/11/1.
 */

public interface CoinRequest {
    @GET("/points/show")
    Observable<ResponseBean<CoinBean>> list(@Query(value = "type") int type,
                                            @Query(value = "page") int page,
                                            @Query(value = "pagesize") int pagesize);
}
