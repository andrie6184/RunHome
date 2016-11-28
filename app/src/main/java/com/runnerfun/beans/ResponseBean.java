package com.runnerfun.beans;

import java.io.Serializable;

/**
 * Created by andrie on 12/10/2016.
 */

public class ResponseBean<T> implements Serializable {

    private String msg;
    private T data;
    private int code;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}

