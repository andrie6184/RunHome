package com.runnerfun.xyzrunpackage;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
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
import com.runnerfun.LockScreenActivity;
import com.runnerfun.MainActivity;
import com.runnerfun.R;
import com.runnerfun.RecordService;
import com.runnerfun.RunApplication;
import com.runnerfun.RunnerConnection;
import com.runnerfun.UserFragment;
import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.RunSaveResultBean;
import com.runnerfun.beans.RunUploadBean;
import com.runnerfun.beans.RunUploadDB;
import com.runnerfun.model.ConfigModel;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class RunRecordService extends Service implements AMapLocationListener {

    public static final String ACTION_RECORD_SERVICE_START = "com.runnerfun.service.action.start.service";
    public static final String ACTION_RECORD_SERVICE_STOP = "com.runnerfun.service.action.stop.service";

    private static final String SPEAK_VOICE = "恭喜你，已经跑了%s公里，上一公里配速%s，您总共用时%s";

    private RecordServiceBinder binder;
    private PendingIntent intent;
    private DeamonServiceConnection connection;
    private ScreenOFFReceiver receiver;

    private AMapLocationClient client = null;
    private SpeechUtil speech = new SpeechUtil();

    public RunRecordService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (binder == null) {
            binder = new RecordServiceBinder();
        }
        connection = new DeamonServiceConnection();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver = new ScreenOFFReceiver(), filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }
        switch (intent.getAction()) {
            case ACTION_RECORD_SERVICE_START:
                doStart();
                break;
            case ACTION_RECORD_SERVICE_STOP:
                doStop();
                break;
            default:
                break;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public static void startRun(Context context) {
        Intent i = new Intent(context, RunRecordService.class);
        i.setAction(ACTION_RECORD_SERVICE_START);
        context.startService(i);
    }

    public static void stopRun(Context context) {
        Intent i = new Intent(context, RunRecordService.class);
        i.setAction(ACTION_RECORD_SERVICE_STOP);
        context.startService(i);
    }

    private void doStart() {
        Intent start = new Intent(RunRecordService.this, RunDeamonService.class);
        start.setAction(RunDeamonService.ACTION_DEAMON_SERVICE_START);
        startService(start);

        bindService(new Intent(this, RecordService.class), connection, Context.BIND_IMPORTANT);

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        android.support.v4.app.NotificationCompat.Builder mNotifyBuilder
                = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("")
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("跑步中")
                .setContentIntent(intent);
        Notification notification = mNotifyBuilder.build();

        startForeground(1234, notification);

        if (client != null) {
            client.onDestroy();
        }
        client = new AMapLocationClient(this);
        client.setLocationListener(this);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setOnceLocation(false);
        option.setGpsFirst(true);
        option.setMockEnable(false);
        option.setInterval(2000);
        client.setLocationOption(option);
        client.startLocation();

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(RunModel.RUN_SERVICE_START_ACTION));
        RunModel.instance.startRecord();
    }

    private void doStop() {
        if (ConfigModel.instance.ismUserVoice()) {
            speech.speak("跑步结束");
        }

        if (client != null) {
            client.stopLocation();
            client.onDestroy();
        }
        RunModel.instance.stopRecord();

        unbindService(connection);

        Intent stop = new Intent(RunRecordService.this, RunDeamonService.class);
        stop.setAction(RunDeamonService.ACTION_DEAMON_SERVICE_STOP);
        startService(stop);

        stopForeground(true);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(intent);

        uploadServiceData();
    }

    private void uploadServiceData() {
        final RunUploadBean bean = new RunUploadBean();
        final String track = getTrack(RunModel.instance.getRecord().tracks);
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd hh:mm:ss", Locale.getDefault());
        bean.startTime = format.format(new Date(RunModel.instance.getStartTime()));
        bean.calorie = RunModel.instance.getCalorie();
        bean.distance = RunModel.instance.getDistance() / 1000; //上传公里
        bean.endTime = format.format(new Date(System.currentTimeMillis()));
        bean.total_time = RunModel.instance.getRecordTime() / 1000; //上传秒
        bean.total_distance = RunModel.instance.getTotalDistance() / 1000; //上传公里
        bean.position = RunModel.instance.getRecord().position;

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

                        Intent intent1 = new Intent(RunRecordService.this, TrackMapActivity.class);
                        intent1.putExtra(TrackMapActivity.PARAM_TRACK_RUN_ID, bean.getData().getId());
                        startActivity(intent1);
                        LocalBroadcastManager.getInstance(RunRecordService.this).sendBroadcast(
                                new Intent(RunMapActivity.RUN_MAP_FINISH_ACTION));

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

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
//        if (aMapLocation != null) {
//            Timber.d("aMapLocation.getErrorCode(): " + aMapLocation.getErrorCode());
//            Timber.d("aMapLocation.getAccuracy(): " + aMapLocation.getAccuracy());
//            Timber.d("aMapLocation.getLocationType(): " + aMapLocation.getLocationType());
//            Timber.d("aMapLocation.getLatitude(): " + aMapLocation.getLatitude()
//                    + "  aMapLocation.getLongitude(): " + aMapLocation.getLongitude());
//        }

        if (aMapLocation != null && aMapLocation.getErrorCode() == 0 && aMapLocation.getAccuracy() < 50f
                && (aMapLocation.getLocationType() == AMapLocation.LOCATION_TYPE_GPS
                || aMapLocation.getLocationType() == AMapLocation.LOCATION_TYPE_WIFI
                || aMapLocation.getLocationType() == AMapLocation.LOCATION_TYPE_CELL)) {

            LatLng po = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            RunModel.instance.updateRecord(new TimeLatLng(po));

            if (TextUtils.isEmpty(RunModel.instance.getRecord().position)
                    && !TextUtils.isEmpty(aMapLocation.getCity())) {
                RunModel.instance.getRecord().position = aMapLocation.getCountry()
                        + aMapLocation.getCity() + aMapLocation.getDistrict();
            }
        }

        // speak speed.
        if (ConfigModel.instance.ismUserVoice() && RunModel.instance.getDistance()
                >= (RunModel.instance.getRecord().mileFlag * 1000)) {
            try {
                String dis = UITools.numberFormat(RunModel.instance.getDistance() / 1000);

                long mileMillSeconds = RunModel.instance.getRecordTime() - RunModel.instance.getRecord().lastMileTime;
                int speedMinute = (int) (mileMillSeconds / 1000 / 60);
                int speedSecond = (int) (mileMillSeconds / 1000 % 60);
                String speed = String.format(Locale.getDefault(), "%d分%d秒", speedMinute, speedSecond);

                int totalMinute = (int) (RunModel.instance.getRecordTime() / 1000 / 60);
                int totalSecond = (int) (RunModel.instance.getRecordTime() / 1000 % 60);
                String total = String.format(Locale.getDefault(), "%d分%d秒", totalMinute, totalSecond);

                speech.speak(String.format(Locale.getDefault(), SPEAK_VOICE, dis, speed, total));

                RunModel.instance.getRecord().lastMileTime += mileMillSeconds;
                RunModel.instance.getRecord().mileFlag += 1;
            } catch (Exception e) {
                e.printStackTrace();
                Timber.e(e, "service speak error!");
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Timber.i("RunRecordService", "onBind(), Thread: " + Thread.currentThread().getName());
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (intent != null) {
            Timber.i("RunRecordService", "onUnbind(), from:" + intent.getStringExtra("from"), "");
        }
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        if (intent != null) {
            Timber.i("RunRecordService", "onRebind(), from:" + intent.getStringExtra("from"), "");
        }
    }

    public class RecordServiceBinder extends RunnerConnection.Stub {
        @Override
        public String getProName() throws RemoteException {
            return "RunRecordService";
        }
    }

    class DeamonServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            Timber.i("runnerfun", "远程服务连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // 启动RemoteService
            Timber.i("runnerfun", "RunDeamonService，重新启动");
            Intent intent = new Intent(RunRecordService.this, RunDeamonService.class);
            intent.setAction(ACTION_RECORD_SERVICE_START);
            RunRecordService.this.startService(intent);
            RunRecordService.this.bindService(new Intent(RunRecordService.this, RunDeamonService.class),
                    connection, Context.BIND_IMPORTANT);
        }

    }

    private class ScreenOFFReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Intent mLockIntent = new Intent(context, LockScreenActivity.class);
                mLockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(mLockIntent);
            }
        }
    }

}
