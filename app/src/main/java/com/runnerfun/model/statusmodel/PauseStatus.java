package com.runnerfun.model.statusmodel;

import com.amap.api.maps2d.model.LatLng;

/**
 * Created by lixiaoyang on 22/11/2016.
 * 照这个破设计似乎还没什么卵用
 */

public class PauseStatus extends RecordStatus {
    public PauseStatus(RecordStatus from) {
        super(from);
    }

    @Override
    public long getRecordTime() {
        return mTimeOffset;
    }

    @Override
    public void addRecord(LatLng ll) {
        //TODO : do nothing
    }

    @Override
    public void clearRecord() {
        //TODO: do nothing
    }
}
