package com.runnerfun.model.statusmodel;

import android.os.SystemClock;

import com.amap.api.maps.model.LatLng;

/**
 * Created by lixiaoyang on 22/11/2016.
 */

public class StopStatus extends RecordStatus {
    public StopStatus(RecordStatus from) {
        super(from);
        if(from != null && from.mStartTime != 0) {
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
    }
}
