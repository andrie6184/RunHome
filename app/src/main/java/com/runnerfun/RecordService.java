package com.runnerfun;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.model.LatLng;
import com.runnerfun.mock.TrackMocker;
import com.runnerfun.model.RecordModel;

import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.Subject;

/**
 * Created by andrie on 02/11/2016.
 */
public class RecordService extends Service implements AMapLocationListener {
    public static final String ACTION_START_RECORD = "com.runnerfun.start";
    public static final String ACTION_STOP_RECORD = "com.runnerfun.stop";
    public static final String ACTION_CLEAR_RECORD = "com.runnerfun.clear";
    public static final String ACTION_PAUSE_RECORD = "com.runnerfun.pause";
    public static final String ACTION_RESUME_RECORD = "com.runnerfun.resume";
    public static final String ID_ARGS = "id";

    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient = null;
    private Subscription mUploadTimer = null;

    public static void startRecord(Context c, long id){
        Intent i = new Intent(c, RecordService.class);
        i.setAction(ACTION_START_RECORD);
        i.putExtra(ID_ARGS, id);
        c.startService(i);
    }
    public static void pauseRecord(Context c){
        Intent i = new Intent(c, RecordService.class);
        i.setAction(ACTION_PAUSE_RECORD);
        c.startService(i);
    }
    public static void stopRecord(Context c){
        Intent i = new Intent(c, RecordService.class);
        i.setAction(ACTION_STOP_RECORD);
        c.startService(i);
    }

    public static void resumeRecord(Context c){
        Intent i = new Intent(c, RecordService.class);
        i.setAction(ACTION_RESUME_RECORD);
        c.startService(i);
    }

    public RecordService(){
        super();
        mlocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        mlocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mlocationClient.setLocationOption(mLocationOption);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        RecordModel.instance.addRecord(new LatLng(aMapLocation.getLatitude()
                , aMapLocation.getLongitude()));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()){
            case ACTION_START_RECORD:
                long id = intent.getLongExtra(ID_ARGS, 0);
                doStart(id);//TODO:是否需要notification?
                break;
            case ACTION_STOP_RECORD:
                doStop();
                break;
            case ACTION_CLEAR_RECORD:
                doClear();
                break;
            case ACTION_PAUSE_RECORD:
                doStop();
                break;
            case ACTION_RESUME_RECORD:
                doResume();
                break;
            default:
                break;
        }
        return Service.START_STICKY;
    }

    private void uploadData(){
        //TODO: 接口
    }

    private void doStop(){
        mlocationClient.stopLocation();
        if(mUploadTimer != null){
            mUploadTimer.unsubscribe();
        }
        RecordModel.instance.stop();
        uploadData();
    }

    private void doStart(long id){
        mlocationClient.stopLocation();
        mlocationClient.startLocation();
        if(mUploadTimer != null){
            mUploadTimer.unsubscribe();
        }
        //TODO: start upload
        RecordModel.instance.start(id);
        TrackMocker.instance.startMock();
        startUploadTimer();
    }

    private void doClear(){
    }

    private void doResume(){
        RecordModel.instance.resume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        doStop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startUploadTimer(){
        mUploadTimer = Subject.interval(6 * 1000, TimeUnit.MILLISECONDS)//TODO:  改为60秒
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        uploadData();
                    }
                });//TODO: 处理cancel?
    }

}
