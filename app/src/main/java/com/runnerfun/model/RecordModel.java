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

import static java.lang.Math.abs;

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
            return Float.valueOf(userInfo.getWeight()) * (getRealDistance() / 1000f) * 1.036f;
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

    public boolean isStop() {
        return mStatus instanceof StopStatus;
    }

    public float getSpeed() {
        long time = getRecordTime();
        float distance = mStatus.getDistance();
        return time == 0 ? 0 : (mStatus.getDistance()/1000)/ (mStatus.getRecordTime()/3600);// km/s = m/ms
    }

    public long getRecordTime() {
//        List<TimeLatLng> cache = mStatus.readCache();
//        if(cache == null || cache.size() <= 0){
//            return 0;
//        }
//        return abs(cache.get(0).getTime() - cache.get(cache.size() - 1).getTime());
        return mStatus.getRecordTime();
    }

    /**
     * km
     *
     * @return
     */
    public float getDistance() {
        return mStatus.getDistance() * 0.92f;
    }

    public float getRealDistance() {
        List<TimeLatLng> ll = mStatus.readCache();
        if(ll == null || ll .size() <= 0){
            return 0;
        }
        float distance = 0.f;

        TimeLatLng start = ll.get(0);
        for(TimeLatLng tl : ll){
            if(tl.speed(start) > 12.2f){
                continue;
            }
            distance += tl.distance(start);
            start = tl;
        }

        return distance * 0.92f;
    }

    public List<TimeLatLng> readCache() {
        return mStatus.readCache();
    }

    public void clear() {
        mStatus.clearRecord();
    }

    public TimeLatLng firstLatLng() {
        return mStatus.firstLatLng();
    }

    public TimeLatLng lastLatLng(){
        return mStatus.lastLatLng();
    }

    public void initRecord(List<TimeLatLng> ll) {
        mStatus.initCache(ll); //TODO: 初始的数据最好改为TIMELATLNG
    }

    public void addRecord(LatLng ll) {
        TimeLatLng last = mStatus.lastLatLng();
        if(last != null){
            float distance = AMapUtils.calculateLineDistance(ll, last.getLatlnt());
            float lastDistance = mStatus.lastDistance();
            if(distance > 50 && mStatus.lastDistance() > 5 && (distance /lastDistance > 3) ){
                //TODO: 定位不靠谱的情况下是否需要mock 一个点？
                return;
            }
        }

        mStatus.addRecord(new TimeLatLng(ll));
        //TODO: aaa
        for (RecordChangeListener l : listeners) {
            l.onRecordChange(ll);
        }
    }

    public static List<TimeLatLng> parseStringToLatLng(String track) { //TODO:能改成TimeLatLng最好
        List<TimeLatLng> result = new ArrayList<>();
        String temp = track.substring(1, track.length() - 1);
        List<String> lats = new ArrayList<>();
        List<String> lans = new ArrayList<>();
        List<String> speeds = new ArrayList<>();

        String[] itemTemps = temp.split(",");
        for (String itemTemp : itemTemps) {
            String trimTemp = itemTemp.trim();
            if (trimTemp.contains("[")) {
                lans.add(trimTemp.substring(trimTemp.indexOf("[") + 1).trim());
            }
            if (trimTemp.contains("]")) {
                lats.add(trimTemp.substring(0, trimTemp.indexOf("]")).trim());
            }
            if (!trimTemp.contains("[") && !trimTemp.contains("]")) {
                speeds.add(trimTemp);
            }
        }

        for (int x = 0; x < lats.size() && x < lans.size(); x++) {
            TimeLatLng item = new TimeLatLng(new LatLng(Double.parseDouble(lats.get(x)),
                    Double.parseDouble(lans.get(x))));
            item.setSpeed(Float.valueOf(speeds.get(x)));
            result.add(item);
        }
        return result;
    }

}
