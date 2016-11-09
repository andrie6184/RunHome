package com.runnerfun.beans;

import java.io.Serializable;

/**
 * Created by andrie on 16/11/7.
 */

public class RunWeekBean implements Serializable {

    private String distance;
    private String weekday;
    private String date;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
