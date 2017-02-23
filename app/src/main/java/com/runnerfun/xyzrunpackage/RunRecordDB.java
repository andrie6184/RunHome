package com.runnerfun.xyzrunpackage;

import com.google.gson.Gson;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * RunRecordDB
 * Created by andrie on 17/2/20.
 */

public class RunRecordDB extends DataSupport {

    @Column(unique = true)
    private long id;
    private int state;
    private long startTime;
    private int mileFlag;
    private long lastMileTime;
    private long pauseTime;
    private long lastPauseTime;
    private float lastDistance;
    private float totalDistance;
    private float distance;
    private float calorie;
    private String position;
    private String tempTrack;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getMileFlag() {
        return mileFlag;
    }

    public void setMileFlag(int mileFlag) {
        this.mileFlag = mileFlag;
    }

    public long getLastMileTime() {
        return lastMileTime;
    }

    public void setLastMileTime(long lastMileTime) {
        this.lastMileTime = lastMileTime;
    }

    public long getPauseTime() {
        return pauseTime;
    }

    public void setPauseTime(long pauseTime) {
        this.pauseTime = pauseTime;
    }

    public long getLastPauseTime() {
        return lastPauseTime;
    }

    public void setLastPauseTime(long lastPauseTime) {
        this.lastPauseTime = lastPauseTime;
    }

    public float getLastDistance() {
        return lastDistance;
    }

    public void setLastDistance(float lastDistance) {
        this.lastDistance = lastDistance;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(float totalDistance) {
        this.totalDistance = totalDistance;
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

    public String getTempTrack() {
        return tempTrack;
    }

    public void setTempTrack(String tempTrack) {
        this.tempTrack = tempTrack;
    }

    public static RunRecordDB fromRunRecord(RunRecord record) {
        RunRecordDB db = new RunRecordDB();
        if (record != null) {
            db.setState(record.state);
            db.setStartTime(record.startTime);
            db.setMileFlag(record.mileFlag);
            db.setLastMileTime(record.lastMileTime);
            db.setPauseTime(record.pauseTime);
            db.setLastPauseTime(record.lastPauseTime);
            db.setLastDistance(record.lastDistance);
            db.setTotalDistance(record.totalDistance);
            db.setDistance(record.distance);
            db.setCalorie(record.calorie);
            db.setPosition(record.position);
            db.setTempTrack(new Gson().toJson(record.tracks));
        }
        return db;
    }

}
