package com.runnerfun.beans;

import java.io.Serializable;

/**
 * ThirdLoginBean
 * Created by andrie on 12/10/2016.
 */

public class ThirdLoginBean implements Serializable {

    private String sid;
    private String nick;
    private int firstlogin;

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

    public int getFirstlogin() {
        return firstlogin;
    }

    public void setFirstlogin(int firstlogin) {
        this.firstlogin = firstlogin;
    }

}
