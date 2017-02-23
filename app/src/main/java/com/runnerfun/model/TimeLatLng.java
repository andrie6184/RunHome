package com.runnerfun.model;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * TimeLatLng
 * Created by andrie on 2016/12/23.
 */

public class TimeLatLng  {
    public LatLng getLatlnt() {
        return latlnt;
    }

    public void setLatlnt(LatLng latlnt) {
        this.latlnt = latlnt;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    private LatLng latlnt;
    private long time;
    private float speed;

    public TimeLatLng(LatLng l){
        latlnt = l;
        time = System.currentTimeMillis();
    }

    public float distance(LatLng other){
        return AMapUtils.calculateLineDistance(other, latlnt);
    }

    public float distance(TimeLatLng other){
        return distance(other.getLatlnt());
    }

    // m/s
    public float speed(TimeLatLng other){
        return distance(other.getLatlnt()) * 1000 / abs(time - other.getTime());
    }

    public static List<LatLng> toLatLngList(List<TimeLatLng> tl){
        List<LatLng> res = new ArrayList<>();
        for(TimeLatLng t : tl){
            res.add(t.getLatlnt());
        }

        return res;
    }

    public static List<TimeLatLng> toTimeLatlngList(List<LatLng> ll){
        List<TimeLatLng> res = new ArrayList<>();
        for(LatLng l : ll){
            res.add(new TimeLatLng(l));
        }
        return res;
    }


}
