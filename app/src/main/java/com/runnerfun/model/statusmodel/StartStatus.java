package com.runnerfun.model.statusmodel;

import android.os.SystemClock;

import com.amap.api.maps2d.model.LatLng;

/**
 * Created by lixiaoyang on 22/11/2016.
 */

public class StartStatus extends RecordStatus {

    public StartStatus(RecordStatus from) {
        super(from);
        mTimeOffset = 0;
        mStartTime = SystemClock.elapsedRealtime();
    }

    @Override
    public long getRecordTime() {
        return  SystemClock.elapsedRealtime() - mStartTime + mTimeOffset;
    }

    @Override
    public void addRecord(LatLng ll) {
        mCache.add(ll);
    }

    @Override
    public void clearRecord() {

    }
}
