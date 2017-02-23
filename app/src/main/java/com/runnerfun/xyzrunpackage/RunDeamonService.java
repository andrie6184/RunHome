package com.runnerfun.xyzrunpackage;

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
import android.support.v7.app.NotificationCompat;

import com.runnerfun.MainActivity;
import com.runnerfun.R;
import com.runnerfun.RunnerConnection;

import timber.log.Timber;

import static com.runnerfun.RecordService.ACTION_START_RECORD;

public class RunDeamonService extends Service {


    public static final String ACTION_DEAMON_SERVICE_START = "com.runnerfun.deamon.service.action.start.service";
    public static final String ACTION_DEAMON_SERVICE_STOP = "com.runnerfun.deamon.service.action.stop.service";

    private RunBinder binder;
    private PendingIntent intent;
    private RunServiceConnection connection;

    public RunDeamonService() {
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
            case ACTION_DEAMON_SERVICE_START:
                doStart();
                break;
            case ACTION_DEAMON_SERVICE_STOP:
                doStop();
                break;
            default:
                break;
        }
        return START_STICKY;
    }

    private void doStart() {
        bindService(new Intent(this, RunRecordService.class), connection, Context.BIND_IMPORTANT);

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
    }

    private void doStop() {
        unbindService(connection);
        stopForeground(true);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(intent);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (intent != null) {
            Timber.i("RunDeamonService", "onUnbind(), from:" + intent.getStringExtra("from"), "");
        }
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        if (intent != null) {
            Timber.i("RunDeamonService", "onRebind(), from:" + intent.getStringExtra("from"), "");
        }
    }

    class RunServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            Timber.i("runnerfun", "本地服务连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // 连接出现了异常断开了，RecordService被杀死了
            Timber.i("runnerfun", "RunRecordService断开，重新启动");
            // 启动RecordService
            Intent intent = new Intent(RunDeamonService.this, RunRecordService.class);
            intent.setAction(ACTION_START_RECORD);
            RunDeamonService.this.startService(intent);
            RunDeamonService.this.bindService(new Intent(RunDeamonService.this, RunRecordService.class),
                    connection, Context.BIND_IMPORTANT);
        }

    }

    class RunBinder extends RunnerConnection.Stub {
        @Override
        public String getProName() throws RemoteException {
            return "RunDeamonService";
        }
    }

}
