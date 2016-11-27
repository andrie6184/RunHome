package com.runnerfun.model;

import android.os.SystemClock;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.runnerfun.model.statusmodel.RecordStatus;
import com.runnerfun.model.statusmodel.StartStatus;
import com.runnerfun.model.statusmodel.StopStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lixiaoyang on 18/11/2016.
 */
//转为单进程Service时需要改为 ContentProvider实现。
public class RecordModel {

    public static final RecordModel instance = new RecordModel();
    private RecordStatus mStatus = new StopStatus(null);

    public void start(){
        mStatus = new StartStatus(null);
    }

    public void stop(){
        mStatus = new StopStatus(mStatus);
    }

    public void pause(){
        mStatus = new StopStatus(mStatus);
    }

    public void resume(){
        mStatus = new StartStatus(mStatus);
    }

    public float getCal(){
//        long cal = weight * distance * 1.036;
        return -1f;
    }

    public float getSpeed(){
        long time = mStatus.getRecordTime();
        return time == 0 ? 0 : mStatus.getDistance() / mStatus.getRecordTime();// km/s = m/ms
    }

    public long getRecordTime(){
        return mStatus.getRecordTime();
    }

    /**
     * km
     * @return
     */
    public float getDistance(){
        return mStatus.getDistance();
    }

    public List<LatLng> readCache(){
        return mStatus.readCache();
    }

    public void addRecord(LatLng ll){
        mStatus.addRecord(ll);
    }

    private void uploadRecord(){
        //TODO:upload
    }
}
