package com.runnerfun;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.RunSaveResultBean;
import com.runnerfun.beans.RunUploadBean;
import com.runnerfun.mock.TrackMocker;
import com.runnerfun.model.RecordModel;
import com.runnerfun.network.NetworkManager;
import com.runnerfun.tools.ThirdpartAuthManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * RecordService
 * Created by andrie on 02/11/2016.
 */
public class RecordService extends Service implements AMapLocationListener {

    public static final String ACTION_START_RECORD = "com.runnerfun.start";
    public static final String ACTION_STOP_RECORD = "com.runnerfun.stop";
    public static final String ACTION_CLEAR_RECORD = "com.runnerfun.clear";
    public static final String ACTION_PAUSE_RECORD = "com.runnerfun.pause";
    public static final String ACTION_RESUME_RECORD = "com.runnerfun.resume";
    public static final String ID_ARGS = "id";

    public String firstPoi;
    public long startTime;

    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient = null;
    private Subscription mUploadTimer = null;

    public static void startRecord(Context c, long id) {
        Intent i = new Intent(c, RecordService.class);
        i.setAction(ACTION_START_RECORD);
        i.putExtra(ID_ARGS, id);
        c.startService(i);
    }

    public static void pauseRecord(Context c) {
        Intent i = new Intent(c, RecordService.class);
        i.setAction(ACTION_PAUSE_RECORD);
        c.startService(i);
    }

    public static void stopRecord(Context c) {
        Intent i = new Intent(c, RecordService.class);
        i.setAction(ACTION_STOP_RECORD);
        c.startService(i);
    }

    public static void resumeRecord(Context c) {
        Intent i = new Intent(c, RecordService.class);
        i.setAction(ACTION_RESUME_RECORD);
        c.startService(i);
    }

    public RecordService() {
        super();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0 && aMapLocation.getAccuracy()  < 100.f){
            RecordModel.instance.addRecord(new LatLng(aMapLocation.getLatitude(),
                    aMapLocation.getLongitude()));

            if (TextUtils.isEmpty(firstPoi) && !TextUtils.isEmpty(aMapLocation.getCity())) {
                firstPoi = aMapLocation.getCountry() + aMapLocation.getCity() + aMapLocation.getDistrict();
                Toast.makeText(RunApplication.getAppContex(), firstPoi, Toast.LENGTH_LONG).show();
            }
        }
        else{
            RecordModel.instance.addMockRecord();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }
        switch (intent.getAction()) {
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
                doPause();
                break;
            case ACTION_RESUME_RECORD:
                doResume();
                break;
            default:
                break;
        }
        return START_NOT_STICKY;
    }

    private void uploadData() {
        RunUploadBean bean = new RunUploadBean();
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd hh:mm:ss", Locale.getDefault());
        bean.startTime = format.format(new Date(startTime));
        bean.calorie = RecordModel.instance.getCal();
        bean.distance = RecordModel.instance.getRealDistance() / 1000; //上传公里
        bean.endTime = format.format(new Date(System.currentTimeMillis()));
        bean.total_time = RecordModel.instance.getRecordTime() / 1000; //上传秒
        bean.total_distance = RecordModel.instance.getDistance() / 1000; //上传公里
        bean.position = firstPoi;

        if (RecordModel.instance.getDistance() <= 10) {
            Toast.makeText(RunApplication.getAppContex(), "跑步距离太短,本次记录无效", Toast.LENGTH_SHORT).show();
            return;
        }

        NetworkManager.instance.getSaveRunRecordObservable(bean)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<ResponseBean<RunSaveResultBean>,
                        Observable<?>>() {
                    @Override
                    public Observable<?> call(ResponseBean<RunSaveResultBean> bean) {
                        Toast.makeText(RunApplication.getAppContex(), String.format(Locale.getDefault(),
                                "已获得%s里币", bean.getData().getCoin()), Toast.LENGTH_SHORT).show();
                        ThirdpartAuthManager.setLastRidForShare(bean.getData().getId());
                        ThirdpartAuthManager.setLastCoinForShare(bean.getData().getCoin());

                        String track = getTrack(RecordModel.instance.readCache());
                        LocalBroadcastManager.getInstance(RunApplication.getAppContex())
                                .sendBroadcast(new Intent(UserFragment.USER_INFO_CHANGED_ACTION));
                        Log.d("hallucination", "trigger");
                        return NetworkManager.instance.getUploadTrackObservable(track, bean.getData()
                                .getId()).subscribeOn(Schedulers.io());
                    }
                }).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                Timber.d(o.toString());
                stopSelf();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Timber.e(throwable.toString());
                stopSelf();
            }
        });
    }

    private void doPause() {
        mlocationClient.stopLocation();
        if (mUploadTimer != null) {
            mUploadTimer.unsubscribe();
        }
        RecordModel.instance.pause();
    }

    private void doStop() {
        if (mUploadTimer != null) {
            mUploadTimer.unsubscribe();
        }
        RecordModel.instance.stop();
        uploadData();
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        stopForeground(true);
    }

    private void doStart(long id) {
        startTime = System.currentTimeMillis();
        if (mlocationClient != null) {
            mlocationClient.onDestroy();
        }
        mlocationClient = new AMapLocationClient(this);
        mlocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
        mLocationOption.setOnceLocation(false);
        mLocationOption.setGpsFirst(true);
        mLocationOption.setMockEnable(false);
        mLocationOption.setInterval(2000);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();
        if (mUploadTimer != null) {
            mUploadTimer.unsubscribe();
        }
        //TODO: start upload
        RecordModel.instance.start(id);
//         TrackMocker.instance.startMock();
        startUploadTimer();
        // start forground
        useForeground("跑步中...");
    }

    public void useForeground(String currSong) {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        /* Method 01
         * this method must SET SMALLICON!
         * otherwise it can't do what we want in Android 4.4 KitKat,
         * it can only show the application info page which contains the 'Force Close' button.*/
        android.support.v4.app.NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("")
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(currSong)
                .setContentIntent(pendingIntent);
        Notification notification = mNotifyBuilder.build();

        /* Method 02
        Notification notification = new Notification(R.drawable.ic_launcher, tickerText,
                System.currentTimeMillis());
        notification.setLatestEventInfo(PlayService.this, getText(R.string.app_name),
                currSong, pendingIntent);
        */

        startForeground(1234, notification);
    }

    private void doClear() {
    }

    private void doResume() {
        RecordModel.instance.resume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (RecordModel.instance.isRecording() || RecordModel.instance.isPause()) {
//            doStop();
//        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startUploadTimer() {
    }

    private String getTrack(List<LatLng> records) {
        if (records == null || records.size() == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append("{");
        LatLng temp = null;
        for (int po = 0; po < records.size(); po++) {
            LatLng item = records.get(po);
            if (item != null) {
                result.append("[");
                result.append(item.longitude).append(",");
                if (temp != null) {
                    result.append(AMapUtils.calculateLineDistance(item, temp)).append(",");
                } else {
                    result.append("0").append(",");
                }
                result.append(item.latitude);
                result.append("]");
            }
            if (po != records.size() - 1) {
                result.append(",");
            }
            temp = item;
        }
        result.append("}");
        return result.toString();
    }

}
