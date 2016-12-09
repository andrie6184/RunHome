package com.runnerfun.beans;

import java.io.Serializable;

/**
 * RunRecordBean
 * Created by andrie on 16/11/7.
 */

public class RunTrackBean implements Serializable {

    private String startTime;
    private String distance;
    private String total_distance;
    private String total_time;
    private String calorie;
    private String highSpeed;
    private String position;
    private String track;
    private String rid;
    private String get_score;
    private String speed;

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

    public String getTotal_time() {
        return total_time;
    }

    public void setTotal_time(String total_time) {
        this.total_time = total_time;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getHighSpeed() {
        return highSpeed;
    }

    public void setHighSpeed(String highSpeed) {
        this.highSpeed = highSpeed;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getGet_score() {
        return get_score;
    }

    public void setGet_score(String get_score) {
        this.get_score = get_score;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

}
