package com.runnerfun.model.statusmodel;


import android.os.SystemClock;

import com.amap.api.maps.model.LatLng;

/**
 * Created by andrie on 22/11/2016.
 * 照这个破设计似乎还没什么卵用
 */

public class PauseStatus extends RecordStatus {

    public PauseStatus(RecordStatus from) {
        super(from);
        if(from != null) {
            mTimeOffset += SystemClock.elapsedRealtime() - mStartTime;
        }
        mStartTime = 0;
    }

    @Override
    public long getRecordTime() {
        return mTimeOffset;
    }

    @Override
    public void addRecord(LatLng ll) {

    }

    @Override
    public void clearRecord() {
        mCache.clear();
        mTime.clear();
    }
}
