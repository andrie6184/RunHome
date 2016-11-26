package com.runnerfun.model.thirdpart;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * WeixinUserInfoRequest
 * Created by andrie on 16/11/25.
 */

public interface WeixinUserInfoRequest {
    @GET("/cgi-bin/user/info")
    Observable<String> getUserInfo(@Query(value = "access_token") String token,
                                   @Query(value = "openid") String openid);
}
