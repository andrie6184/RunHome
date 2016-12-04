package com.runnerfun.model;

/**
 * Created by lixiaoyang on 04/12/2016.
 */

public class ConfigModel {
    public static final ConfigModel instance = new ConfigModel();

    public String getLocalCity() {
        return localCity;
    }

    public void setLocalCity(String localCity) {
        this.localCity = localCity;
    }

    public boolean useCountdown() {
        return mCountDown;
    }

    public void setmCountDown(boolean mCountDown) {
        this.mCountDown = mCountDown;
    }

    public int getmMapType() {
        return mMapType;
    }

    public void setmMapType(int mMapType) {
        this.mMapType = mMapType;
    }

    public int getmCountDownSecond() {
        return mCountDownSecond;
    }

    public void setmCountDownSecond(int mCountDownSecond) {
        this.mCountDownSecond = mCountDownSecond;
    }

    private String localCity = "中国*北京";
    private boolean mCountDown = true;
    private int mMapType = 1;//1 normal, 2 real
    private int mCountDownSecond = 3;


}
