package com.runnerfun.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by andrie on 04/12/2016.
 */

public class ConfigModel {

    private String localCity = "中国*北京";
    private boolean mCountDown = true;
    private int mMapType = 1;//1 normal, 2 night, 3 satellite
    private int mCountDownSecond = 3;
    private boolean mHideOnline = false;
    private boolean mUserVoice = true;

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

    public boolean ismHideOnline() {
        return mHideOnline;
    }

    public void setmHideOnline(boolean mHideOnline) {
        this.mHideOnline = mHideOnline;
    }

    public boolean ismUserVoice() {
        return mUserVoice;
    }

    public void setmUserVoice(boolean mUserVoice) {
        this.mUserVoice = mUserVoice;
    }

    public void save(Context c){
        SharedPreferences sp = c.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putBoolean("hide_onlie", mHideOnline)
        .putBoolean("use_voice", mUserVoice)
        .putInt("map_type", mMapType)
        .putInt("second", mCountDownSecond)
        .apply();
    }

    public void load(Context c){
        SharedPreferences sp = c.getSharedPreferences("config", Context.MODE_PRIVATE);
        mHideOnline = sp.getBoolean("hide_onlie", false);
        mUserVoice = sp.getBoolean("use_voice", true);
        mMapType = sp.getInt("map_type", 0);
        mCountDownSecond = sp.getInt("second", 3);
        // mMapType = mMapType % 3;
        // mCountDownSecond = mCountDownSecond % 4;
    }

}
