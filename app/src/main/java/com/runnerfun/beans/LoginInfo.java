package com.runnerfun.beans;

import java.io.Serializable;

/**
 * Created by lixiaoyang on 12/10/2016.
 */

public class LoginInfo implements Serializable {
    public static class Data {
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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    private int code;
    private String msg;
    private String data;

}
