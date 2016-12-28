package com.runnerfun.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.amap.api.location.AMapLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * OptimizeConfigModel
 * Created by andrie on 16/12/28.
 */

public class OptimizeConfigModel {

    private boolean mCheckGPSState = false;
    private List<String> mSourceType = new ArrayList<>();
    private float mGPSAccuracy = 50f;
    private float mLengthPercent = 1.0f;

    public static final OptimizeConfigModel instance = new OptimizeConfigModel();

    public boolean isCheckGPSState() {
        return mCheckGPSState;
    }

    public void setCheckGPSState(boolean mCheckGPSState) {
        this.mCheckGPSState = mCheckGPSState;
    }

    public List<String> getSourceType() {
        return mSourceType;
    }

    public void setSourceType(List<String> mSourceType) {
        this.mSourceType = mSourceType;
    }

    public float getGPSAccuracy() {
        return mGPSAccuracy;
    }

    public void setGPSAccuracy(float mGPSAccuracy) {
        this.mGPSAccuracy = mGPSAccuracy;
    }

    public float getLengthPercent() {
        return mLengthPercent;
    }

    public void setLengthPercent(float mLengthPercent) {
        this.mLengthPercent = mLengthPercent;
    }

    public void save(Context context) {
        SharedPreferences sp = context.getSharedPreferences("optimizeConfig", Context.MODE_PRIVATE);
        sp.edit().putBoolean("optimize_mCheckGPSState", mCheckGPSState)
                .putString("optimize_mSourceType", encodeSourceType(mSourceType))
                .putFloat("optimize_mGPSAccuracy", mGPSAccuracy)
                .putFloat("optimize_mLengthPercent", mLengthPercent)
                .apply();
    }

    public void load(Context context) {
        SharedPreferences sp = context.getSharedPreferences("optimizeConfig", Context.MODE_PRIVATE);
        mCheckGPSState = sp.getBoolean("optimize_mCheckGPSState", false);
        mSourceType = decodeSourceType(sp.getString("optimize_mSourceType", ""));
        mGPSAccuracy = sp.getFloat("map_type", 50f);
        mLengthPercent = sp.getFloat("second", 1.0f);
    }

    private List<String> decodeSourceType(String source) {
        List<String> result = new ArrayList<>();
        String[] split = source.split(",");
        for (String item : split) {
            if (item.equals("WIFI")) {
                result.add(String.valueOf(AMapLocation.LOCATION_TYPE_WIFI));
            } else if (item.equals("CELL")) {
                result.add(String.valueOf(AMapLocation.LOCATION_TYPE_CELL));
            } else if (item.equals("AMAP")) {
                result.add(String.valueOf(AMapLocation.LOCATION_TYPE_AMAP));
            }
        }
        return result;
    }

    private String encodeSourceType(List<String> source) {
        StringBuilder builder = new StringBuilder();
        for (String item : source) {
            if (item.equals(String.valueOf(AMapLocation.LOCATION_TYPE_WIFI))) {
                builder.append("WIFI").append(",");
            } else if (item.equals(String.valueOf(AMapLocation.LOCATION_TYPE_CELL))) {
                builder.append("CELL").append(",");
            } else if (item.equals(String.valueOf(AMapLocation.LOCATION_TYPE_AMAP))) {
                builder.append("AMAP").append(",");
            }
        }
        return builder.toString();
    }

}
