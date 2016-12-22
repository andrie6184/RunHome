package com.runnerfun.model.statusmodel;

import android.os.SystemClock;

import com.amap.api.maps.model.LatLng;

/**
 * ResumeStatus
 * Created by andrie on 06/12/2016.
 */

public class ResumeStatus extends RecordStatus {

    public ResumeStatus(RecordStatus from) {
        super(from);
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
