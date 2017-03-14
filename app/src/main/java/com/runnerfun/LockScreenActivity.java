package com.runnerfun;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.runnerfun.tools.TimeStringUtils;
import com.runnerfun.widget.UnderView;
import com.runnerfun.xyzrunpackage.RunModel;
import com.runnerfun.xyzrunpackage.RunRecordService;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class LockScreenActivity extends BaseActivity {

    @BindView(R.id.chronometer)
    TextView chronometer;
    @BindView(R.id.restart_btn)
    ImageView restartBtn;

    @BindView(R.id.move_view)
    View moveView;
    @BindView(R.id.under_view)
    UnderView underView;

    private Subscription mTimer = null;
    // private RecordService service;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
//            RecordService.RecordServiceBinder myBinder = (RecordService.RecordServiceBinder) binder;
//            service = myBinder.getService();
            Timber.i("LockScreenActivity", "LockScreenActivity onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Timber.i("LockScreenActivity", "LockScreenActivity onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        ButterKnife.bind(this);

        Typeface boldTypeFace = Typeface.createFromAsset(getAssets(), "fonts/dincond-bold.otf");
        chronometer.setTypeface(boldTypeFace);

        underView.mMoveView = moveView;
        underView.mHandler = new Handler();
        underView.mActivity = this;

        Intent intent = new Intent(this, RecordService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTimer = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        chronometer.setText(TimeStringUtils.getTime(RunModel.instance.getRecordTime()));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e(throwable, "LockScreen onResume error");
                    }
                });

        boolean isRecording = RunModel.instance.getState() == RunModel.RUN_STATE_RUNNING;
        restartBtn.setImageResource(isRecording ? R.drawable.resume : R.drawable.continue_icon);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTimer != null) {
            mTimer.unsubscribe();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int key = event.getKeyCode();
        switch (key) {
            case KeyEvent.KEYCODE_BACK: {
                return true;
            }
            case KeyEvent.KEYCODE_MENU: {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.restart_btn)
    void onFlagClicked(View view) {
        boolean isRecording = RunModel.instance.getState() == RunModel.RUN_STATE_RUNNING;
        if (isRecording) {
            RunModel.instance.pauseRecord();
            restartBtn.setImageResource(R.drawable.continue_icon);
        } else {
            RunModel.instance.resumeRecord();
            restartBtn.setImageResource(R.drawable.resume);
        }
    }

    @OnClick(R.id.stop_btn)
    void onStopClicked(View view) {
        unbindService(mConnection);
        // RunRecordService.stopRun(this);
        finish();
    }

}
