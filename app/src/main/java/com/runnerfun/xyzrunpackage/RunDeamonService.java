package com.runnerfun.xyzrunpackage;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.runnerfun.R;
import com.runnerfun.RunnerConnection;

import timber.log.Timber;

public class RunDeamonService extends Service {


    public static final String ACTION_DEAMON_SERVICE_START = "com.runnerfun.deamon.service.action.start.service";
    public static final String ACTION_DEAMON_SERVICE_STOP = "com.runnerfun.deamon.service.action.stop.service";

    private RunBinder binder;
    // private PendingIntent intent;
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
        if (i0 != null && i0.getAction().equals(ACTION_DEAMON_SERVICE_START)) {
            doStart(startId);
            return START_STICKY;
        } else {
            doStop();
            return START_STICKY_COMPATIBILITY;
        }
    }

    private void doStart(int startId) {
        bindService(new Intent(this, RunRecordService.class), connection, Context.BIND_IMPORTANT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker("跑步进行中...")
                .setContentText("跑步进行中...")
                .setContentTitle("跑步之家")
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setContentIntent(intent)
                .setWhen(System.currentTimeMillis());
        Notification notification = builder.build();

        //设置service为前台进程，避免手机休眠时系统自动杀掉该服务
        startForeground(startId, notification);
    }

    private void doStop() {
        try {
            unbindService(connection);
        } catch (Exception e) {
            Log.e("local", "unbind error " + e.getLocalizedMessage());
        }
        stopForeground(true);
        stopSelf();
        // AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        // am.cancel(intent);
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
            Toast.makeText(RunDeamonService.this, "RunRecordService断开，重新启动", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RunDeamonService.this, RunRecordService.class);
            intent.setAction(RunRecordService.ACTION_RECORD_SERVICE_START);
            RunDeamonService.this.startService(intent);
            RunDeamonService.this.bindService(intent, connection, Context.BIND_IMPORTANT);
        }

    }

    class RunBinder extends RunnerConnection.Stub {
        @Override
        public String getProName() throws RemoteException {
            return "RunDeamonService";
        }
    }

}
