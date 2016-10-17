package com.runnerfun.model;

import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.UserInfo;

import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by lixiaoyang on 13/10/2016.
 */

public interface UserInfoRequest {
    @POST("/user/modify")
    Observable<ResponseBean<UserInfo>> register(@Query(value = "user_name") String user_name,
                                                @Query(value = "age") int age,
                                                @Query(value = "headimg", encoded = true) String headimg,
                                                @Query(value = "headimg", encoded = true) String remarks,
                                                @Query(value = "height") int height,
                                                @Query(value = "sexy") String sexy
    );
}
