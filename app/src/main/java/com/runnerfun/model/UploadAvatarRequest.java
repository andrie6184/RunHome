package com.runnerfun.model;

import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.UploadResult;

import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * UploadAvatarRequest
 * Created by andrie on 16/11/4.
 */

public interface UploadAvatarRequest {
    @Multipart
    @POST("/upimg")
    Observable<ResponseBean<UploadResult>> uploadAvatar(@Part("fileName") String description,
                                                        @Part("file\"; filename=\"image.png\"") RequestBody imgs);
}
