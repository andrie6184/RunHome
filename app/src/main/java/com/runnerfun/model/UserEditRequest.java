package com.runnerfun.model;

import com.runnerfun.beans.ResponseBean;

import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * UserEditRequest
 * Created by lixiaoyang on 13/10/2016.
 */

public interface UserEditRequest {
    @POST("/user/modify")
    Observable<ResponseBean<String>> editUserInfo(@Part("user_name") String user_name,
                                              @Part("age") int age,
                                              @Part("headimg") String headimg,
                                              @Part("remarks") String remarks,
                                              @Part("height") int height,
                                              @Part("sexy") String sexy
    );
}
