package com.runnerfun.model.statusmodel;

import android.os.SystemClock;

import com.amap.api.maps.model.LatLng;
import com.runnerfun.model.TimeLatLng;

/**
 * StopStatus
 * Created by andrie on 22/11/2016.
 */

public class StopStatus extends RecordStatus {
    public StopStatus(RecordStatus from) {
        super(from);
        if (from != null && from.mStartTime != 0) {
            mTimeOffset += SystemClock.elapsedRealtime() - mStartTime;
        }
        mStartTime = 0;
    }

    @Override
    public long getRecordTime() {
        return mTimeOffset;
    }

    @Override
    public void addRecord(TimeLatLng ll) {
        throw new IllegalArgumentException("has stopped, should not add new record");

    }

    @Override
    public void clearRecord() {
        mCache.clear();
    }
}
