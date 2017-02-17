package com.runnerfun;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import timber.log.Timber;

import static com.runnerfun.RecordService.ACTION_START_RECORD;
import static com.runnerfun.RecordService.ACTION_STOP_RECORD;

/**
 * RecordDeamonService
 * Created by andrie on 17/2/16.
 */

public class RecordDeamonService extends Service {

    RunBinder binder;
    private PendingIntent intent;
    RunServiceConnection connection;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (binder == null) {
            binder = new RunBinder();
        }
        connection = new RunServiceConnection();
    }

    @Override
    public int onStartCommand(Intent i0, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }
        switch (i0.getAction()) {
            case ACTION_START_RECORD:
                doStart();
                break;
            case ACTION_STOP_RECORD:
                doStop();
                break;
            default:
                break;
        }
        return START_STICKY;
    }

    private void doStart() {
        bindService(new Intent(this, RecordService.class), connection, Context.BIND_IMPORTANT);

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        android.support.v4.app.NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("")
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("跑步中")
                .setContentIntent(pendingIntent);
        Notification notification = mNotifyBuilder.build();

        startForeground(1234, notification);
    }

    private void doStop() {
        unbindService(connection);
        Intent intent = new Intent("MY_LOCATION");
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(pi);
        stopForeground(true);
    }

    class RunServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            Timber.i("runnerfun", "本地服务连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // 连接出现了异常断开了，RecordService被杀死了
            // 启动RecordService
            RecordDeamonService.this.startService(new Intent(RecordDeamonService.this, RecordService.class));
            RecordDeamonService.this.bindService(new Intent(RecordDeamonService.this, RecordService.class),
                    connection, Context.BIND_IMPORTANT);
        }

    }

    class RunBinder extends RunnerConnection.Stub {

        @Override
        public String getProName() throws RemoteException {
            return "RecordDeamonService";
        }

    }

}
