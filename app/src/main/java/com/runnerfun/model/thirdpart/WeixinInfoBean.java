package com.runnerfun.model.thirdpart;

import java.io.Serializable;

/**
 * WeixinInfoBean
 * Created by andrie on 16/12/14.
 */

public class WeixinInfoBean implements Serializable {

    private String openid;
    private String nickname;
    private String headimgurl;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

}
