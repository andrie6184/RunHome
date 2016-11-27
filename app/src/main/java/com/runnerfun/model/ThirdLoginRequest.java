package com.runnerfun.model;

import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.ThirdLoginBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * ThirdLoginRequest
 * Created by andrie on 16/11/21.
 */

public interface ThirdLoginRequest {
    @FormUrlEncoded
    @POST("user/loginbind")
    Observable<ResponseBean<ThirdLoginBean>> loginWithThird(@Field("bid") String bid,
                                                            @Field("type") String type,
                                                            @Field("name") String name,
                                                            @Field("headimg") String headimg,
                                                            @Field("sign") String sign);
}
