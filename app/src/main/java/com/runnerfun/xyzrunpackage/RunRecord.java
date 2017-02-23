package com.runnerfun.xyzrunpackage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runnerfun.model.TimeLatLng;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.runnerfun.xyzrunpackage.RunModel.RUN_STATE_STOP;

/**
 * RunRecord
 * Created by andrie on 17/2/20.
 */

public class RunRecord implements Serializable {

    public int state = RUN_STATE_STOP;
    public long startTime;
    public int mileFlag;
    public long lastMileTime;
    public long pauseTime;
    public long lastPauseTime;
    public float lastDistance;
    public float totalDistance;
    public float distance;
    public float calorie;
    public String position = "";
    public List<TimeLatLng> tracks;

    public static RunRecord fromRunRecordDB(RunRecordDB db) {
        RunRecord result = new RunRecord();
        if (db != null) {
            result.state = db.getState();
            result.startTime = db.getStartTime();
            result.mileFlag = db.getMileFlag();
            result.lastMileTime = db.getLastMileTime();
            result.pauseTime = db.getPauseTime();
            result.lastPauseTime = db.getLastPauseTime();
            result.lastDistance = db.getLastDistance();
            result.totalDistance = db.getTotalDistance();
            result.distance = db.getDistance();
            result.calorie = db.getCalorie();
            result.position = db.getPosition();

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<TimeLatLng>>() {
            }.getType();
            result.tracks = gson.fromJson(db.getTempTrack(), type);
        }
        return result;
    }

}
