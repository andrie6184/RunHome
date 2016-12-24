package com.runnerfun.beans;

import java.io.Serializable;

/**
 * LoginBean
 * Created by andrie on 12/10/2016.
 */

public class LoginBean implements Serializable {

    private String sid;
    private String uid;
    private String nick;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

}
