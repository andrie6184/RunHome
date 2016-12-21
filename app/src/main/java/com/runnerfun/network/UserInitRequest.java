package com.runnerfun.network;

import com.runnerfun.beans.ResponseBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * UserInitRequest
 * Created by andrie on 16/12/21.
 */

public interface UserInitRequest {
    @FormUrlEncoded
    @POST("/user/modify")
    Observable<ResponseBean<Object>> initUserInfo(@Field("age") int age,
                                                  @Field("height") int height,
                                                  @Field("sexy") String sexy,
                                                  @Field("weight") int weight
    );
}
