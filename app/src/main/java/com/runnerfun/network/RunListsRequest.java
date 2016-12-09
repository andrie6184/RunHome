package com.runnerfun.network;

import com.runnerfun.beans.PersonalRecordBean;
import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.RunRecordBean;

import java.util.ArrayList;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * RunListsRequest
 * Created by andrie on 16/11/7.
 */

public interface RunListsRequest {
    @GET("/running/runningrecord")
    Observable<ResponseBean<PersonalRecordBean>> getRunLists(@Query(value = "currentPage") int currentPage);
}
