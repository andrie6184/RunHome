package com.runnerfun.model.statusmodel;

import android.os.SystemClock;

import com.amap.api.maps.model.LatLng;

/**
 * StartStatus
 * Created by andrie on 22/11/2016.
 */

public class StartStatus extends RecordStatus {

    public StartStatus(RecordStatus from) {
        super(null);
        mTimeOffset = 0;
        mStartTime = SystemClock.elapsedRealtime();
    }

    @Override
    public long getRecordTime() {
        return SystemClock.elapsedRealtime() - mStartTime + mTimeOffset;
    }

    @Override
    public void addRecord(LatLng ll) {
        mCache.add(ll);
    }

    @Override
    public void clearRecord() {

    }
}
