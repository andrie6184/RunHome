package com.runnerfun.model.statusmodel;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lixiaoyang on 22/11/2016.
 */

public abstract class RecordStatus {
    protected long mStartTime = 0;
    protected long mTimeOffset = 0;
    protected List<LatLng> mCache = null;



    protected long mRecordId = -1; // 本地ID

    public RecordStatus(RecordStatus from){
        if(from == null){
            mCache = new ArrayList<>();
        }
        else{
            mStartTime = from.mStartTime;
            mTimeOffset = from.mTimeOffset;
            mRecordId = from.mRecordId;
            mCache = new ArrayList<>();
            if(from.mCache != null){
                Collections.copy(mCache, from.mCache);
            }
        }
    }

    abstract public long getRecordTime();
    abstract public void addRecord(LatLng ll);
    abstract public void clearRecord();
    /**
     * km
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

    public List<LatLng> readCache(){
        return Collections.unmodifiableList(mCache);
    }

    public long getmRecordId() {
        return mRecordId;
    }

    public void setmRecordId(long mRecordId) {
        this.mRecordId = mRecordId;
    }
}
