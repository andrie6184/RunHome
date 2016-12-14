package com.runnerfun.model.thirdpart;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by andrie on 16/12/14.
 */

public interface WXRefreshTokenRequest {

    @GET("/sns/oauth2/refresh_token")
    Observable<WeixinTokenBean> refreshOauthToken(@Query(value = "appid") String appid,
                                                  @Query(value = "refresh_token") String token,
                                                  @Query(value = "grant_type") String type);

}
