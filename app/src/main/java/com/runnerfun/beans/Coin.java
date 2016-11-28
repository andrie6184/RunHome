package com.runnerfun.beans;

import java.io.Serializable;

/**
 * Coin
 * Created by andrie on 16/11/1.
 */

public class Coin implements Serializable {

    private String id;
    private String num;
    private String the_time;
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getThe_time() {
        return the_time;
    }

    public void setThe_time(String the_time) {
        this.the_time = the_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
