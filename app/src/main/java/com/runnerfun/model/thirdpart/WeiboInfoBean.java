package com.runnerfun.model.thirdpart;

import java.io.Serializable;

/**
 * WeiboInfoBean
 * Created by andrie on 16/12/14.
 */

public class WeiboInfoBean implements Serializable {

    private String name;
    private String profile_image_url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

}
