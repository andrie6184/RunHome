package com.runnerfun.network;

import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.RunWeekBean;

import java.util.ArrayList;

import retrofit2.http.GET;
import rx.Observable;

/**
 * RunWeekRequest
 * Created by andrie on 16/11/7.
 */

public interface RunWeekRequest {
    @GET("/running/getweeklyrunningrecord")
    Observable<ResponseBean<ArrayList<RunWeekBean>>> getWeek();
}
