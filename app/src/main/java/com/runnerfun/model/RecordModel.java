package com.runnerfun.model;

import com.amap.api.maps2d.model.LatLng;
import com.google.gson.Gson;
import com.runnerfun.RunApplication;
import com.runnerfun.UserFragment;
import com.runnerfun.beans.UserInfo;
import com.runnerfun.model.statusmodel.RecordStatus;
import com.runnerfun.model.statusmodel.StartStatus;
import com.runnerfun.model.statusmodel.StopStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixiaoyang on 18/11/2016.
 */
//转为单进程Service时需要改为 ContentProvider实现。
public class RecordModel {
    public static interface RecordChangeListener {
        public void onRecordChange(LatLng ll);
    }

    private List<RecordChangeListener> listeners = new ArrayList<>();

    public void addListener(RecordChangeListener l) {
        listeners.add(l);
    }

    public void removeListener(RecordChangeListener l) {
        listeners.remove(l);
    }

    public static final RecordModel instance = new RecordModel();
    private RecordStatus mStatus = new StopStatus(null);
    private long mID = -1;

    public void start(long id) {
        mID = id;
        mStatus = new StartStatus(null);
    }

    public long getID() {
        return mID;
    }

    ;

    public void stop() {
        mStatus = new StopStatus(mStatus);
    }

    public void pause() {
        mStatus = new StopStatus(mStatus);
    }

    public void resume() {
        mStatus = new StartStatus(mStatus);
    }

    public float getCal() {
        UserInfo userInfo = new Gson().fromJson(RunApplication.getAppContex().sharedPreferences
                .getString(UserFragment.SP_KEY_USER_INFO, ""), UserInfo.class);
        return Float.valueOf(userInfo.getWeight()) * getDistance() * 1.036f;
    }

    public boolean isRecording() {
        return mStatus instanceof StartStatus;
    }

    public float getSpeed() {
        long time = mStatus.getRecordTime();
        return time == 0 ? 0 : mStatus.getDistance() / mStatus.getRecordTime();// km/s = m/ms
    }

    public long getRecordTime() {
        return mStatus.getRecordTime();
    }

    /**
     * km
     *
     * @return
     */
    public float getDistance() {
        return mStatus.getDistance();
    }

    public List<LatLng> readCache() {
        return mStatus.readCache();
    }

    public LatLng firstLatLng() {
        return mStatus.firstLatLng();
    }

    public void addRecord(LatLng ll) {
        mStatus.addRecord(ll);
        for (RecordChangeListener l : listeners) {
            l.onRecordChange(ll);
        }
    }

    private void uploadRecord() {
        //TODO:upload
    }
}
