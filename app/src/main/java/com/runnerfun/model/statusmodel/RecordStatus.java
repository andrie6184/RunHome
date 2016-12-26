package com.runnerfun.model.statusmodel;


import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.runnerfun.model.TimeLatLng;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by andrie on 22/11/2016.
 */

public abstract class RecordStatus {
    protected long mStartTime = 0;
    protected long mTimeOffset = 0;
    protected List<TimeLatLng> mCache = null;

    RecordStatus(RecordStatus from){
        if(from == null){
            mCache = new ArrayList<>();
            mStartTime = 0;
            mTimeOffset = 0;
        }
        else{
            mStartTime = from.mStartTime;
            mTimeOffset = from.mTimeOffset;
            if(from.mCache != null){
                mCache = new ArrayList<>(from.mCache);
            }
            else{
                mCache = new ArrayList<>();
            }
        }
    }

    public void initCache(List<TimeLatLng> ll){
        mCache.clear();
        mCache.addAll(ll);
    }
    abstract public long getRecordTime();
    abstract public void addRecord(TimeLatLng ll);

    public void clearRecord(){
        mStartTime = 0;
        mTimeOffset = 0;
        mCache.clear();
    };
    /**
     * m
     * @return
     */
    public float getDistance(){
        if(mCache.size() <= 1){
            return 0;
        }
        TimeLatLng start = mCache.get(0);//TODO: 加上海拔……
        float distance = 0;

        for(TimeLatLng ll : mCache){
            distance += ll.distance(start); //AMapUtils.calculateLineDistance(start, ll);
            start = ll;
        }

        return distance;
    }

    public List<TimeLatLng> readCache(){
        return Collections.unmodifiableList(mCache);
    }

    public TimeLatLng lastLatLng(){
        return mCache.size() > 0 ? mCache.get(mCache.size() - 1) : null;
    }

    public TimeLatLng firstLatLng() {
        return mCache.size() > 0 ? mCache.get(0) : null;
    }

    public float lastDistance(){
        if(mCache.size() > 2){
            return  mCache.get(mCache.size() -1).distance(mCache.get(mCache.size() -2));
        }
        return 0;
    }
}
