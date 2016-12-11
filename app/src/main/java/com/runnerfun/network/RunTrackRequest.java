package com.runnerfun.network;

import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.RunTrackBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * RunTotalRequest
 * Created by andrie on 16/11/7.
 */

public interface RunTrackRequest {
    @GET("/running/getrunningrecord")
    Observable<ResponseBean<RunTrackBean>> getTrack(@Query(value = "rid") String id);
}
