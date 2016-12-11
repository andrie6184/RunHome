package com.runnerfun.beans;

import java.io.Serializable;

/**
 * RunRecordBean
 * Created by andrie on 16/11/7.
 */

public class RunRecordBean implements Serializable {

    private String rid;
    private String calorie;
    private String startTime;
    private String distance;
    private String total_distance;
    private String highSpeed;
    private String speed;
    private String position;
    private String ridnew;
    private String deleted;
    private String get_score;

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

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRidnew() {
        return ridnew;
    }

    public void setRidnew(String ridnew) {
        this.ridnew = ridnew;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getGet_score() {
        return get_score;
    }

    public void setGet_score(String get_score) {
        this.get_score = get_score;
    }

}
