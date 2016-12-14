package com.runnerfun.model.thirdpart;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * WeixinUserInfoRequest
 * Created by andrie on 16/11/25.
 */

public interface WeixinTokenRequest {
    @GET("/sns/oauth2/access_token")
    Observable<WeixinTokenBean> getOauthToken(@Query(value = "appid") String appid,
                                              @Query(value = "secret") String secret,
                                              @Query(value = "code") String code,
                                              @Query(value = "grant_type") String type);
}
