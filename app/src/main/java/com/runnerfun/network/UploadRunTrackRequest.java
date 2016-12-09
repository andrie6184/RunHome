package com.runnerfun.network;

import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.UploadResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * UploadRunTrackRequest
 * Created by andrie on 16/12/9.
 */

public interface UploadRunTrackRequest {
    @FormUrlEncoded
    @POST("/running/saverunningrecord")
    Observable<ResponseBean<String>> uploadTrack(@Field("track") String track,
                                                        @Field("run_record_id") String id);
}
