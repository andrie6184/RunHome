package com.runnerfun.xyzrunpackage;

import android.text.TextUtils;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.google.gson.Gson;
import com.runnerfun.RunApplication;
import com.runnerfun.UserFragment;
import com.runnerfun.beans.UserInfo;
//import com.runnerfun.mock.LogToFile;
import com.runnerfun.model.ConfigModel;
import com.runnerfun.model.TimeLatLng;
import com.runnerfun.tools.SpeechUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * RunModel
 * Created by andrie on 17/2/20.
 */

public class RunModel {

    public interface RecordChangeListener {
        void onRecordChange(TimeLatLng ll);
    }

    public static final RunModel instance = new RunModel();

    public static final int RUN_STATE_RUNNING = 0x00022;
    public static final int RUN_STATE_PAUSE = 0x00023;
    public static final int RUN_STATE_STOP = 0x00024;

    public static final String RUN_SERVICE_START_ACTION = "com.runnerfun.action.RUN_SERVICE_START_ACTION";

    private List<RecordChangeListener> callbacks = new ArrayList<>();

    private RunRecord record;
    private float weight;
    private SpeechUtil speech = new SpeechUtil();

    private RunModel() {
        readCacheRecord();
        String info = RunApplication.getAppContex()
                .sharedPreferences.getString(UserFragment.SP_KEY_USER_INFO, "");
        if (!TextUtils.isEmpty(info)) {
            UserInfo userInfo = new Gson().fromJson(info, UserInfo.class);
            weight = Float.parseFloat(userInfo.getWeight());
        }
    }

    public int getState() {
        if (record != null) {
            return record.state;
        }
        return RUN_STATE_STOP;
    }

    public RunRecord getRecord() {
        return record;
    }

    public void addCallbacks(RecordChangeListener callback) {
        callbacks.add(callback);
    }

    public void removeCallbacks(RecordChangeListener callback) {
        callbacks.remove(callback);
    }

    public void startRecord() {

        // Log.d("RunModel", "startRecord() is record null: " + (record == null));

        if (record == null) {
            record = new RunRecord();
            record.startTime = System.currentTimeMillis();
        }
        record.state = RUN_STATE_RUNNING;
        record.tracks = new ArrayList<>();
    }

    public void pauseRecord() {
        record.state = RUN_STATE_PAUSE;
        record.lastPauseTime = System.currentTimeMillis();
        if (ConfigModel.instance.ismUserVoice()) {
            speech.speak("跑步暂停");
        }
    }

    public void resumeRecord() {
        record.state = RUN_STATE_RUNNING;
        record.lastPauseTime = 0;
        if (ConfigModel.instance.ismUserVoice()) {
            speech.speak("跑步开始");
        }
    }

    public void updateRecord(TimeLatLng latLng, boolean isCell) {

        // Timber.d("record.state: " + record.state);

        if (record.state == RUN_STATE_STOP) {
            return;
        }

        if (record.state == RUN_STATE_PAUSE) {
            record.pauseTime = System.currentTimeMillis() - record.lastPauseTime;
        } else {
            if (record.tracks == null) {
                record.tracks = new ArrayList<>();
            }
            if (record.tracks.size() == 0) {
                record.tracks.add(latLng);
            } else {
                TimeLatLng last = record.tracks.get(record.tracks.size() - 1);
                float dis = AMapUtils.calculateLineDistance(latLng.getLatlnt(), last.getLatlnt());

                String log1 = "dis: " + dis + " record.lastDistance: " + record.lastDistance;
                Timber.d(log1);
                // LogToFile.d("updateRecord", log1);

                // TODO check the condition!!!
                if (dis > 50 && record.lastDistance > 5 && (dis / record.lastDistance > 3)) {
                    return;
                } else if (latLng.speed(last) < 12.2f && !isCell) { // TODO maybe 7.2f
                    record.distance += dis;
                } else if (latLng.speed(last) < 20.2f && isCell) {
                    record.distance += dis;
                }
                record.lastDistance = dis;
                record.totalDistance += dis;

                String log2 = "record.lastDistance: " + record.lastDistance + " record.totalDistance: "
                        + record.totalDistance;
                Timber.d(log2);
                // LogToFile.d("updateRecord", log2);

                record.calorie = weight * (record.distance / 1000f) * 1.036f;
                record.tracks.add(latLng);

                String log3 = "record.tracks.size(): " + record.tracks.size();
                Timber.d(log3);
                // LogToFile.d("updateRecord", log3);
            }
            // save to database.
            DataSupport.deleteAll(RunRecordDB.class);
            RunRecordDB db = RunRecordDB.fromRunRecord(record);
            db.save();
            for (RecordChangeListener callback : callbacks) {
                callback.onRecordChange(latLng);
            }
        }
    }

    public void stopRecord() {
        record = null;
        DataSupport.deleteAll(RunRecordDB.class);

        // Log.d("RunModel", "stopRecord() record size: " + DataSupport.count(RunRecordDB.class));
    }

    private void readCacheRecord() {
        RunRecordDB db = DataSupport.findFirst(RunRecordDB.class);
        if (db != null) {
            record = RunRecord.fromRunRecordDB(db);
        }

        // Log.d("RunModel", "readCacheRecord() is record null: " + (record == null));
    }

    public long getStartTime() {
        if (record != null && record.startTime != 0) {
            return record.startTime;
        }
        return 0;
    }

    public long getRecordTime() {
        if (record != null && record.startTime != 0) {
            return System.currentTimeMillis() - record.startTime - record.pauseTime;
        }
        return 0;
    }

    public float getDistance() {
        if (record != null && record.distance != 0) {
            return record.distance * 0.92f;
        }
        return 0;
    }

    public float getTotalDistance() {
        if (record != null && record.totalDistance != 0) {
            return record.totalDistance * 0.92f;
        }
        return 0;
    }

    public float getCalorie() {
        if (record != null && record.calorie != 0) {
            return record.calorie;
        }
        return 0f;
    }

    public float getSpeed() {
        if (record != null) {
            long time = getRecordTime();
            // km/s = m/ms
            return time == 0 ? 0 : (record.distance / 1000) / (getRecordTime() / 3600);
        }
        return 0;
    }

    public static List<TimeLatLng> parseTrack2LatLng(String track) {
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
