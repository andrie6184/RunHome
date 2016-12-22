package com.runnerfun.model.statusmodel;

import android.os.SystemClock;

import com.amap.api.maps.model.LatLng;
import com.runnerfun.model.TimeLatLng;

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
    public void addRecord(TimeLatLng ll) {
        mCache.add(ll);
    }

    @Override
    public void clearRecord() {

    }
}
