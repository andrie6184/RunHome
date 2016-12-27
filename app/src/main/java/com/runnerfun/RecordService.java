package com.runnerfun;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
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
import com.runnerfun.beans.RunUploadDB;
import com.runnerfun.mock.TrackMocker;
import com.runnerfun.model.ConfigModel;
import com.runnerfun.model.RecordModel;
import com.runnerfun.model.TimeLatLng;
import com.runnerfun.network.NetworkManager;
import com.runnerfun.tools.SpeechUtil;
import com.runnerfun.tools.ThirdpartAuthManager;
import com.runnerfun.tools.UITools;

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

    public static final String ACTION_START_RECORD = "com.runnerfun.service.action.start";
    public static final String ACTION_STOP_RECORD = "com.runnerfun.service.action.stop";
    public static final String ACTION_CLEAR_RECORD = "com.runnerfun.service.action.clear";
    public static final String ACTION_PAUSE_RECORD = "com.runnerfun.service.action.pause";
    public static final String ACTION_RESUME_RECORD = "com.runnerfun.service.action.resume";
    public static final String ID_ARGS = "id";

    public String firstPoi;
    public long startTime;
    public long lastMileTime;
    public int mileFlag;

    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient = null;
    private Subscription mUploadTimer = null;
    private SpeechUtil mSpeechUtil = null;

    private String speakVoice = "恭喜你，已经跑了%s公里，上一公里配速%s，您总共用时%s";

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

    private int ignore = 2;

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (--ignore > 0) {
            return;
        }
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0 && aMapLocation.getAccuracy() < 50f
                && aMapLocation.getLocationType() == AMapLocation.LOCATION_TYPE_GPS) {
            RecordModel.instance.addRecord(new LatLng(aMapLocation.getLatitude(),
                    aMapLocation.getLongitude()));

            if (TextUtils.isEmpty(firstPoi) && !TextUtils.isEmpty(aMapLocation.getCity())) {
                firstPoi = aMapLocation.getCountry() + aMapLocation.getCity() + aMapLocation.getDistrict();
                // Toast.makeText(RunApplication.getAppContex(), firstPoi, Toast.LENGTH_LONG).show();
            }
        } else {
            //TODO: do nothing
        }

        // speak speed.
        if (ConfigModel.instance.ismUserVoice() && RecordModel.instance.getRealDistance() >= (mileFlag * 1000)) {
            try {
                long now = System.currentTimeMillis();

                String dis = UITools.numberFormat(RecordModel.instance.getRealDistance() / 1000);

                int speedMinute = (int) ((now - lastMileTime) / 1000 / 60);
                int speedSecond = (int) ((now - lastMileTime) / 1000 % 60);
                String speed = String.format(Locale.getDefault(), "%d分%d秒", speedMinute, speedSecond);

                int totalMinute = (int) ((now - startTime) / 1000 / 60);
                int totalSecond = (int) ((now - startTime) / 1000 % 60);
                String total = String.format(Locale.getDefault(), "%d分%d秒", totalMinute, totalSecond);

                mSpeechUtil.speak(String.format(Locale.getDefault(), speakVoice, dis, speed, total));

                lastMileTime = now;
                mileFlag += 1;
            } catch (Exception e) {
                e.printStackTrace();
                Timber.e(e, "service speak error!");
            }
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
                doStart(id);
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

        IntentFilter mScreenOffFilter = new IntentFilter();
        mScreenOffFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenOffReceiver, mScreenOffFilter);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (RecordModel.instance.isRecording() || RecordModel.instance.isPause()) {
//            doStop();
//        }
        unregisterReceiver(mScreenOffReceiver);
        // not here RecordModel.instance.clear();
    }

    private void uploadServiceData() {
        final RunUploadBean bean = new RunUploadBean();
        final String track = getTrack(RecordModel.instance.readCache());
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd hh:mm:ss", Locale.getDefault());
        bean.startTime = format.format(new Date(startTime));
        bean.calorie = RecordModel.instance.getCal();
        bean.distance = RecordModel.instance.getRealDistance() / 1000; //上传公里
        bean.endTime = format.format(new Date(System.currentTimeMillis()));
        bean.total_time = RecordModel.instance.getRecordTime() / 1000; //上传秒
        bean.total_distance = RecordModel.instance.getDistance() / 1000; //上传公里
        bean.position = firstPoi;

        if ((bean.total_distance * 1000) <= 10) {
            Toast.makeText(RunApplication.getAppContex(), "跑步距离太短,本次记录无效", Toast.LENGTH_SHORT).show();
            stopSelf();
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

                        LocalBroadcastManager.getInstance(RunApplication.getAppContex())
                                .sendBroadcast(new Intent(UserFragment.USER_INFO_CHANGED_ACTION));
                        Timber.d("hallucination", "trigger");
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
                Toast.makeText(RunApplication.getAppContex(), "上传失败,已保存至本地,将会稍后重新上传!",
                        Toast.LENGTH_SHORT).show();
                RunUploadDB saveModel = new RunUploadDB(bean);
                saveModel.setId(System.currentTimeMillis());
                saveModel.setTrack(track);
                saveModel.save();
                Timber.e(throwable.toString());
                stopSelf();
            }
        });
    }

    private void doPause() {
        if (ConfigModel.instance.ismUserVoice()) {
            mSpeechUtil.speak("跑步暂停");
        }
        mlocationClient.stopLocation();
        if (mUploadTimer != null) {
            mUploadTimer.unsubscribe();
        }
        RecordModel.instance.pause();
    }

    private void doStop() {
        if (ConfigModel.instance.ismUserVoice()) {
            mSpeechUtil.speak("跑步结束");
        }
        if (mUploadTimer != null) {
            mUploadTimer.unsubscribe();
        }
        RecordModel.instance.stop();
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mSpeechUtil = null;
        stopForeground(true);
        Intent intent = new Intent("MY_LOCATION");
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(pi);

        uploadServiceData();
        //TrackMocker.instance.stopMock();
    }

    private void doStart(long id) {
        ignore = 2;
        startTime = System.currentTimeMillis();
        lastMileTime = System.currentTimeMillis();
        mileFlag = 1;
        if (mlocationClient != null) {
            mlocationClient.onDestroy();
        }
        mlocationClient = new AMapLocationClient(this);
        mlocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(false);
        mLocationOption.setGpsFirst(true);
        mLocationOption.setMockEnable(false);
        mLocationOption.setInterval(2000);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();
        if (mUploadTimer != null) {
            mUploadTimer.unsubscribe();
        }
        if (mSpeechUtil == null) {
            mSpeechUtil = new SpeechUtil();
        }
        RecordModel.instance.start(id);
        //TrackMocker.instance.startMock();
        startUploadTimer();
        // start forground
        useForeground("跑步中...");
        /* no use Intent intent = new Intent("MY_LOCATION");
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 2 * 1000, pi);*/
    }

    public void useForeground(String currSong) {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        android.support.v4.app.NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("")
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(currSong)
                .setContentIntent(pendingIntent);
        Notification notification = mNotifyBuilder.build();
        startForeground(1234, notification);
    }

    private void doClear() {
    }

    private void doResume() {
        if (ConfigModel.instance.ismUserVoice()) {
            mSpeechUtil.speak("跑步开始");
        }
        RecordModel.instance.resume();
    }

    private void startUploadTimer() {
    }

    private String getTrack(List<TimeLatLng> records) {
        if (records == null || records.size() == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append("{");
        TimeLatLng temp = null;
        for (int po = 0; po < records.size(); po++) {
            TimeLatLng item = records.get(po);
            if (item != null) {
                result.append("[");
                result.append(item.getLatlnt().longitude).append(",");
                if (temp != null) {
                    float distance = AMapUtils.calculateLineDistance(temp.getLatlnt(), item.getLatlnt());
                    result.append(distance / Math.abs(item.getTime() - temp.getTime())).append(",");
                } else {
                    result.append("0").append(",");
                }
                result.append(item.getLatlnt().latitude);
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

    private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Intent mLockIntent = new Intent(context, LockScreenActivity.class);
                mLockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(mLockIntent);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Timber.i("RecordService", "onBind(), Thread: " + Thread.currentThread().getName());
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (intent != null) {
            Timber.i("RecordService", "onUnbind(), from:" + intent.getStringExtra("from"), "");
        }
        return false;
    }

    public class RecordServiceBinder extends Binder {
        public RecordService getService() {
            return RecordService.this;
        }
    }

    private RecordServiceBinder binder = new RecordServiceBinder();

}
