package com.runnerfun.model;

import android.text.TextUtils;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.google.gson.Gson;
import com.runnerfun.RunApplication;
import com.runnerfun.UserFragment;
import com.runnerfun.beans.UserInfo;
import com.runnerfun.model.statusmodel.PauseStatus;
import com.runnerfun.model.statusmodel.RecordStatus;
import com.runnerfun.model.statusmodel.ResumeStatus;
import com.runnerfun.model.statusmodel.StartStatus;
import com.runnerfun.model.statusmodel.StopStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * RecordModel
 * Created by andrie on 18/11/2016.
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

    public void stop() {
        mStatus = new StopStatus(mStatus);
    }

    public void pause() {
        mStatus = new PauseStatus(mStatus);
    }

    public void resume() {
        mStatus = new ResumeStatus(mStatus);
    }

    /**
     * get expend hot
     * @return K cal
     */
    public float getCal() {
        String info = RunApplication.getAppContex().sharedPreferences.getString(
                UserFragment.SP_KEY_USER_INFO, "");
        if (!TextUtils.isEmpty(info)) {
            UserInfo userInfo = new Gson().fromJson(info, UserInfo.class);
            return Float.valueOf(userInfo.getWeight()) * getDistance() * 1.036f / 1000;
        } else {
            return 0f;
        }
    }

    public boolean isRecording() {
        return mStatus instanceof StartStatus || mStatus instanceof ResumeStatus;
    }

    public boolean isPause() {
        return mStatus instanceof PauseStatus;
    }

    public float getSpeed() {
        long time = mStatus.getRecordTime();
        float distance = mStatus.getDistance();
        return time == 0 ? 0 : (mStatus.getDistance()/1000)/ (mStatus.getRecordTime()/3600);// km/s = m/ms
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

    public float getRealDistance() {
        List<LatLng> ll = readCache();
        float distance = 0.f;
        if (ll == null || ll.size() <= 0) {
            return distance;
        }

        LatLng start = ll.get(0);
        for (LatLng l : ll) {
            float dis = AMapUtils.calculateLineDistance(start, l);
            if (dis <= 3.6f) {
                distance += dis;
            }
            start = l;
        }

        return distance;
    }

    public List<LatLng> readCache() {
        return mStatus.readCache();
    }

    public LatLng firstLatLng() {
        return mStatus.firstLatLng();
    }

    public void initRecord(List<LatLng> ll) {
        mStatus.initCache(ll);
    }

    public void addRecord(LatLng ll) {
        LatLng last = mStatus.lastLatLng();
        if(last != null){
            float distance = AMapUtils.calculateLineDistance(ll, last);
            float lastDistance = mStatus.lastDistance();
            if(distance > 50 && mStatus.lastDistance() > 5 && (distance /lastDistance > 3) ){
                addMockRecord();
                return;
            }
        }

        mStatus.addRecord(ll);
        //TODO: aaa
        for (RecordChangeListener l : listeners) {
            l.onRecordChange(ll);
        }
    }

    public void addMockRecord(){
        mStatus.addMockRecord();
        for (RecordChangeListener l : listeners) {
            l.onRecordChange(mStatus.lastLatLng());
        }
    }

    public static List<LatLng> parseStringToLatLng(String track) {
        List<LatLng> result = new ArrayList<>();
        String temp = track.substring(1, track.length() - 1);
        List<String> lats = new ArrayList<>();
        List<String> lans = new ArrayList<>();
        String[] itemTemps = temp.split(",");
        for (String itemTemp : itemTemps) {
            String trimTemp = itemTemp.trim();
            if (trimTemp.contains("[")) {
                lans.add(trimTemp.substring(trimTemp.indexOf("[") + 1).trim());
            }
            if (trimTemp.contains("]")) {
                lats.add(trimTemp.substring(0, trimTemp.indexOf("]")).trim());
            }
        }

        for (int x = 0; x < lats.size() && x < lans.size(); x++) {
            LatLng item = new LatLng(Double.parseDouble(lats.get(x)), Double.parseDouble(lans.get(x)));
            result.add(item);
        }
        return result;
    }

}
