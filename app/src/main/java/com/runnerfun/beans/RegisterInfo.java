package com.runnerfun.beans;

import java.io.Serializable;

/**
 * Created by andrie on 13/10/2016.
 */

public class RegisterInfo implements Serializable {

    private String uid;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
