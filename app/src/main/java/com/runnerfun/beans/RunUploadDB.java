package com.runnerfun.beans;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * RunUploadDB
 * Created by andrie on 16/12/26.
 */

public class RunUploadDB extends DataSupport {

    @Column(unique = true)
    private long id;
    private String startTime;
    private float total_distance;
    private long total_time;
    private float distance;
    private float calorie;
    private String position;
    private String endTime;
    private String track;

    public RunUploadDB() {
    }

    public RunUploadDB(RunUploadBean bean) {
        this.startTime = bean.startTime;
        this.total_distance = bean.total_distance;
        this.total_time = bean.total_time;
        this.distance = bean.distance;
        this.calorie = bean.calorie;
        this.position = bean.position;
        this.endTime = bean.endTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public float getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(float total_distance) {
        this.total_distance = total_distance;
    }

    public long getTotal_time() {
        return total_time;
    }

    public void setTotal_time(long total_time) {
        this.total_time = total_time;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getCalorie() {
        return calorie;
    }

    public void setCalorie(float calorie) {
        this.calorie = calorie;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public static RunUploadBean transToRunUploadDB(RunUploadDB db) {
        RunUploadBean bean = new RunUploadBean();
        bean.startTime = db.startTime;
        bean.total_distance = db.total_distance;
        bean.total_time = db.total_time;
        bean.distance = db.distance;
        bean.calorie = db.calorie;
        bean.position = db.position;
        bean.endTime = db.endTime;
        return bean;
    }

}
