package com.runnerfun.beans;

import java.io.Serializable;

/**
 * Created by andrie on 16/11/7.
 */

public class RunTotalBean implements Serializable {

    private String maxHighSpeed;
    private String sumTimes;
    private String sumDistance;
    private String sumTotalDistance;
    private String sumCalorie;

    public String getMaxHighSpeed() {
        return maxHighSpeed;
    }

    public void setMaxHighSpeed(String maxHighSpeed) {
        this.maxHighSpeed = maxHighSpeed;
    }

    public String getSumTimes() {
        return sumTimes;
    }

    public void setSumTimes(String sumTimes) {
        this.sumTimes = sumTimes;
    }

    public String getSumDistance() {
        return sumDistance;
    }

    public void setSumDistance(String sumDistance) {
        this.sumDistance = sumDistance;
    }

    public String getSumTotalDistance() {
        return sumTotalDistance;
    }

    public void setSumTotalDistance(String sumTotalDistance) {
        this.sumTotalDistance = sumTotalDistance;
    }

    public String getSumCalorie() {
        return sumCalorie;
    }

    public void setSumCalorie(String sumCalorie) {
        this.sumCalorie = sumCalorie;
    }
}
