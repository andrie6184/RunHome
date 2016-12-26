package com.runnerfun.beans;

import java.io.Serializable;

/**
 * RunRecordBean
 * Created by andrie on 16/11/7.
 */

public class PersonalRunRecordBean implements Serializable {

    private long id;
    private String track;
    private String endTime;

    private String rid;
    private String calorie;
    private String startTime;
    private String distance;
    private String total_distance;
    private String total_time;
    private String highSpeed;
    private String speed;
    private String position;
    private String ridnew;
    private String deleted;
    private String get_score;

    public PersonalRunRecordBean(RunRecordBean bean) {
        this.track = "";

        this.rid = bean.getRid();
        this.calorie = bean.getCalorie();
        this.startTime = bean.getStartTime();
        this.distance = bean.getDistance();
        this.total_distance = bean.getTotal_distance();
        this.total_time = bean.getTotal_time();
        this.highSpeed = bean.getHighSpeed();
        this.speed = bean.getSpeed();
        this.position = bean.getPosition();
        this.ridnew = bean.getRidnew();
        this.deleted = bean.getDeleted();
        this.get_score = bean.getGet_score();
    }

    public PersonalRunRecordBean(RunUploadDB db) {
        this.id = db.getId();
        this.startTime = db.getStartTime();
        this.total_distance = String.valueOf(db.getTotal_distance()); //此处单位为千米;
        this.total_time = String.valueOf(db.getTotal_time()); //此处单位为秒;
        this.distance = String.valueOf(db.getDistance()); //此处单位为千米;
        this.calorie = String.valueOf(db.getCalorie()); //此处单位为卡;
        this.position = db.getPosition();
        this.endTime = db.getEndTime();
        this.track = db.getTrack();

        this.rid = "-1";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

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

    public String getTotal_time() {
        return total_time;
    }

    public void setTotal_time(String total_time) {
        this.total_time = total_time;
    }

}
