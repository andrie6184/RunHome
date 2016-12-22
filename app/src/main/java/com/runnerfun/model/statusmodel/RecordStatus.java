package com.runnerfun.model.statusmodel;


import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by andrie on 22/11/2016.
 */

public abstract class RecordStatus {
    protected long mStartTime = 0;
    protected long mTimeOffset = 0;
    protected List<LatLng> mCache = null;
    protected List<Long> mTime = null;

    RecordStatus(RecordStatus from){
        if(from == null){
            mCache = new ArrayList<>();
            mTime = new ArrayList<>();
            mStartTime = 0;
            mTimeOffset = 0;
        }
        else{
            mStartTime = from.mStartTime;
            mTimeOffset = from.mTimeOffset;
            if(from.mCache != null){
                mCache = new ArrayList<>(from.mCache);
                mTime = new ArrayList<>(from.mTime);
            }
            else{
                mCache = new ArrayList<>();
                mTime = new ArrayList<>();
            }
        }
    }

    public void initCache(List<LatLng> ll){
        mCache.clear();
        mCache.addAll(ll);
        mTime.clear();
    }
    abstract public long getRecordTime();
    abstract public void addRecord(LatLng ll);

    public void addMockRecord(){
        if(mCache != null && mCache.size() > 0){
            mCache.add(mCache.get(mCache.size() - 1));
        }
        mTime.add(System.currentTimeMillis());
    }

    public void clearRecord(){
        mCache.clear();
        mTime.clear();
    };
    /**
     * m
     * @return
     */
    public float getDistance(){
        if(mCache.size() <= 1){
            return 0;
        }
        LatLng start = mCache.get(0);//TODO: 加上海拔……
        float distance = 0;
        for(LatLng ll : mCache){
            distance += AMapUtils.calculateLineDistance(start, ll);
            start = ll;
        }

        return distance;
    }

    public List<Long> readTimeCache(){
        return Collections.unmodifiableList(mTime);
    }
    public List<LatLng> readCache(){
        return Collections.unmodifiableList(mCache);
    }

    public LatLng lastLatLng(){
        return mCache.size() > 0 ? mCache.get(mCache.size() - 1) : null;
    }

    public LatLng firstLatLng() {
        return mCache.size() > 0 ? mCache.get(0) : null;
    }

    public float lastDistance(){
        if(mCache.size() > 2){
            return AMapUtils.calculateLineDistance(mCache.get(mCache.size() -1), mCache.get(mCache.size() - 2));
        }
        return 0;
    }
}
