package com.runnerfun.network;

import com.runnerfun.beans.ResponseBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * UserEditRequest
 * Created by andrie on 13/10/2016.
 */

public interface UserEditRequest {
    @FormUrlEncoded
    @POST("/user/modify")
    Observable<ResponseBean<Object>> editUserInfo(@Field("user_name") String user_name,
                                                  @Field("age") int age,
                                                  @Field("headimg") String headimg,
                                                  @Field("remarks") String remarks,
                                                  @Field("height") int height,
                                                  @Field("sexy") String sexy,
                                                  @Field("weight") int weight
    );
}
