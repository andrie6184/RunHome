package com.runnerfun;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.Subject;

/**
 * Created by lixiaoyang on 02/11/2016.
 */

public class RecordService extends IntentService implements AMapLocationListener {
    public static final String ACTION_START_RECORD = "com.runnerfun.start";
    public static final String ACTION_STOP_RECORD = "com.runnerfun.stop";
    public static final String ACTION_CLEAR_RECORD = "com.runnerfun.doClear";

    private List<LatLng> mCoordinates = new ArrayList<>();
    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient = null;
    private Subscription mUploadTimer = null;
    private long mStartTime = 0;

    public static void startRecord(Context c){
        Intent i = new Intent(c, RecordService.class);
        i.setAction(ACTION_START_RECORD);
        c.startService(i);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RecordService(String name) {
        super(name);
        mlocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        mlocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();
    }

    public RecordService(){
        super("");
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(mCoordinates.size() > 1) {
            LatLng ll = mCoordinates.get(mCoordinates.size() - 1);
            if (Double.compare(ll.latitude, aMapLocation.getLatitude()) == 0
                    && Double.compare(ll.longitude, aMapLocation.getLongitude()) == 0) {
                return;
            }
        }
        mCoordinates.add(new LatLng(aMapLocation.getLatitude()
                , aMapLocation.getLongitude()));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getAction()){
            case ACTION_START_RECORD:
                doStart();//TODO:是否需要notification?
                break;
            case ACTION_STOP_RECORD:
                doStop();
                break;
            case ACTION_CLEAR_RECORD:
                doClear();
                break;
            default:
                break;
        }
    }

    private void uploadData(){
        //TODO: 接口
    }

    private void doStop(){
        mlocationClient.stopLocation();
        if(mUploadTimer != null){
            mUploadTimer.unsubscribe();
        }
        uploadData();
    }

    private void doStart(){
        mlocationClient.stopLocation();
        mCoordinates.clear();
        mlocationClient.startLocation();
        if(mUploadTimer != null){
            mUploadTimer.unsubscribe();
        }
        mStartTime = SystemClock.elapsedRealtime();//使用系统启动时间计算流逝时间,加上base时间
        mUploadTimer = Subject.timer(60 * 1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        uploadData();
                    }
                });//TODO: 处理cancel?
    }

    private void doClear(){
        mCoordinates.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doStop();
    }
}
