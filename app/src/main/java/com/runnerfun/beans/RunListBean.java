package com.runnerfun.beans;

import java.io.Serializable;

/**
 * Created by andrie on 16/11/7.
 */

public class RunListBean implements Serializable {

    private String rid;
    private String calorie;
    private String startTime;
    private String distance;
    private String total_distance;
    private String highSpeed;
    private String location;
    private String speed;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(String total_distance) {
        this.total_distance = total_distance;
    }

    public String getHighSpeed() {
        return highSpeed;
    }

    public void setHighSpeed(String highSpeed) {
        this.highSpeed = highSpeed;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }
}
