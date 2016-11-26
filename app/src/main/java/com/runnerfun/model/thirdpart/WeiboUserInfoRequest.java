package com.runnerfun.model.thirdpart;

import com.runnerfun.beans.ResponseBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * WeiboUserInfoRequest
 * Created by andrie on 16/11/21.
 */

public interface WeiboUserInfoRequest {
    @GET("/2/users/show.json")
    Observable<String> getUserInfo(@Query(value = "access_token") String token,
                                              @Query(value = "uid") String uid);
}
