package com.runnerfun.beans;

import java.io.Serializable;

/**
 * Created by andrie on 12/10/2016.
 */

public class LoginBean implements Serializable {
    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    private String sid;
    private String nick;
}
