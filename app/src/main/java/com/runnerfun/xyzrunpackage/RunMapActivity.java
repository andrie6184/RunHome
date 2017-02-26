package com.runnerfun.xyzrunpackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.runnerfun.BaseActivity;
import com.runnerfun.R;
import com.runnerfun.model.ConfigModel;
import com.runnerfun.model.TimeLatLng;
import com.runnerfun.tools.TimeStringUtils;
import com.runnerfun.widget.MapBtnWidget;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class RunMapActivity extends BaseActivity implements AMapLocationListener,
        RunModel.RecordChangeListener {

    public static final String RUN_MAP_FINISH_ACTION = "com.runnerfun.action.RUN_MAP_FINISH_ACTION";

    @BindView(R.id.map)
    MapView mMap;
    @BindView(R.id.panel)
    MapBtnWidget mPanelWidget;

    private TextView mCalValue;
    private TextView mSpeedValue;
    private TextView mDisValue;
    private TextView mTimeValue;

    private TextView mPauseBtn;

    private AMapLocationClient client = null;

    private Subscription timer = null;
    private DecimalFormat decimalFormat = new DecimalFormat("0.000");

    private BitmapDescriptor run = null;
    private BitmapDescriptor start = null;
    private BitmapDescriptor stop = null;

    private LocalBroadcastManager manager;
    private RunMapReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_map);
        ButterKnife.bind(this);
        mMap.onCreate(savedInstanceState);

        manager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter(RUN_MAP_FINISH_ACTION);
        filter.addAction(RunModel.RUN_SERVICE_START_ACTION);
        receiver = new RunMapReceiver();
        manager.registerReceiver(receiver, filter);

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.unregisterReceiver(receiver);
        if (timer != null) {
            timer.unsubscribe();
            timer = null;
        }
        RunModel.instance.removeCallbacks(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMap.onSaveInstanceState(outState);
    }

    private void init() {
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setInterval(2000);

        client = new AMapLocationClient(this);
        client.setLocationListener(this);
        client.setLocationOption(option);

        mPanelWidget.setOnClickListener(new MapBtnWidget.OnButtonClick() {
            @Override
            public void onLeftClick() {
                pause();
            }

            @Override
            public void onRightClick() {
                stop();
            }
        });

        Typeface boldTypeFace = Typeface.createFromAsset(getAssets(), "fonts/dincond-bold.otf");
        ((TextView) findViewById(R.id.cal).findViewById(R.id.attr_name)).setText("卡路里");
        ((TextView) findViewById(R.id.speed).findViewById(R.id.attr_name)).setText("配速");
        ((TextView) findViewById(R.id.distance).findViewById(R.id.attr_name)).setText("距离");
        ((TextView) findViewById(R.id.time).findViewById(R.id.attr_name)).setText("时间");

        mCalValue = (TextView) findViewById(R.id.cal).findViewById(R.id.attr_value);
        mSpeedValue = (TextView) findViewById(R.id.speed).findViewById(R.id.attr_value);
        mDisValue = (TextView) findViewById(R.id.distance).findViewById(R.id.attr_value);
        mTimeValue = (TextView) findViewById(R.id.time).findViewById(R.id.attr_value);

        mCalValue.setTypeface(boldTypeFace);
        mSpeedValue.setTypeface(boldTypeFace);
        mDisValue.setTypeface(boldTypeFace);
        mTimeValue.setTypeface(boldTypeFace);

        if (ConfigModel.instance.getmMapType() == 1) {
            mMap.getMap().setMapType(AMap.MAP_TYPE_SATELLITE);
        } else if (ConfigModel.instance.getmMapType() == 2) {
            mMap.getMap().setMapType(AMap.MAP_TYPE_NIGHT);
        }
        mPauseBtn = (TextView) mPanelWidget.findViewById(R.id.pause);

        run = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.run));
        start = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.shi));
        stop = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.zhong));

        Log.d("RunMapActivity", "init() state " + RunModel.instance.getState());

        if (RunModel.instance.getState() == RunModel.RUN_STATE_STOP) {
            mPanelWidget.setVisibility(View.GONE);
            client.startLocation();
        } else {
            mPanelWidget.setVisibility(View.VISIBLE);
            if (RunModel.instance.getState() == RunModel.RUN_STATE_RUNNING) {
                mPauseBtn.setText("暂停");
            } else if (RunModel.instance.getState() == RunModel.RUN_STATE_PAUSE) {
                mPauseBtn.setText("继续");
            }
        }

        if (RunModel.instance.getState() == RunModel.RUN_STATE_RUNNING) {
            startAutoRefresh();
        } else {
            stopAutoRefresh();
        }

        RunModel.instance.addCallbacks(this);
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        client.stopLocation();
        if (location != null && location.getErrorCode() == 0) {
            SharedPreferences sp = getSharedPreferences("location", Context.MODE_PRIVATE);
            sp.edit().putString("location", location.getCountry() + '·' + location.getCity()).apply();
            moveTo(new LatLng(location.getLatitude(), location.getLongitude()));
            mMap.getMap().moveCamera(CameraUpdateFactory.zoomTo(18.0f));
            /*if (RunModel.instance.getState() == RunModel.RUN_STATE_RUNNING) {
                drawStart();
            }*/
        }
    }

    @Override
    public void onRecordChange(TimeLatLng ll) {

        Log.d("RunMapActivity", "onRecordChange(): " + ll.getTime());

        drawLines(RunModel.instance.getRecord().tracks);
        // zoom(mMap.getMap().getCameraPosition().zoom)
        CameraUpdate c = CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .target(ll.getLatlnt()).zoom(18.0f).build());
        mMap.getMap().moveCamera(c);
    }

    @OnClick(R.id.back)
    void onBackClicked(View view) {
        finish();
    }

    private void pause() {
        if (RunModel.instance.getState() == RunModel.RUN_STATE_RUNNING) {
            RunModel.instance.pauseRecord();
            mPauseBtn.setText("继续");
            stopAutoRefresh();
        } else if (RunModel.instance.getState() == RunModel.RUN_STATE_PAUSE) {
            RunModel.instance.resumeRecord();
            mPauseBtn.setText("暂停");
            startAutoRefresh();
        }
    }

    private void stop() {
        if (RunModel.instance.getState() == RunModel.RUN_STATE_RUNNING
                || RunModel.instance.getState() == RunModel.RUN_STATE_PAUSE) {
            mPanelWidget.setVisibility(View.GONE);
            RunRecordService.stopRun(this);
            if (timer != null) {
                timer.unsubscribe();
                timer = null;
            }
        }
    }

    private void startAutoRefresh() {
        if (timer == null) {
            timer = Observable.interval(1000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            updateRecordValue();
                            // drawLines(RunModel.instance.getRecord().tracks);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Timber.e(throwable, "RunMapActivity timer error");
                        }
                    });
        }
    }

    private void stopAutoRefresh() {
        if (timer != null) {
            timer.unsubscribe();
            timer = null;
        }
    }

    private void updateRecordValue() {

        Log.d("RunMapActivity", "updateRecordValue() recordTime: " + RunModel.instance.getRecordTime());

        if (RunModel.instance.getRecordTime() > 0 && RunModel.instance.getDistance() > 0) {
            float seconds = RunModel.instance.getRecordTime() / RunModel.instance.getDistance();
            int minutes = (int) seconds / 60;
            String speedShow = String.format(Locale.getDefault(), "%d'%d\"", minutes, (int) (seconds % 60));
            mSpeedValue.setText(speedShow);
        } else {
            mSpeedValue.setText("0'00\"");
        }
        String distance = decimalFormat.format(RunModel.instance.getDistance() / 1000);
        mDisValue.setText("" + distance + "km");
        mTimeValue.setText(TimeStringUtils.getTime(RunModel.instance.getRecordTime()));
        mCalValue.setText("" + decimalFormat.format(RunModel.instance.getCalorie() / 1000) + "kcal");
    }

    private void moveTo(LatLng ll) {
        CameraUpdate c = CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .target(ll).build());
        mMap.getMap().moveCamera(c);
    }

    private void drawLines(List<TimeLatLng> records) {
        if (records == null || records.size() <= 0) {
            return;
        }

        TimeLatLng start = records.get(0);
        PolylineOptions po = new PolylineOptions();
        List<Integer> colors = new ArrayList<>();
        for (TimeLatLng ll : records) {
            if (ll.speed(start) > 12.2f) { // TODO maybe 7.2f
                colors.add(Color.RED);
            } else {
                colors.add(Color.GREEN);
            }
            start = ll;
        }
        po.colorValues(colors);
        po.addAll(TimeLatLng.toLatLngList(records));
        po.width(10f);
        mMap.getMap().clear();
        mMap.getMap().addPolyline(po);

        drawStart();
        if (RunModel.instance.getState() == RunModel.RUN_STATE_RUNNING) {
            drawCurrent();
        } else {
            drawEnd();
        }
    }

    private void drawStart() {
        if (RunModel.instance.getRecord().tracks == null
                || RunModel.instance.getRecord().tracks.size() == 0) {
            return;
        }
        LatLng first = RunModel.instance.getRecord().tracks.get(0).getLatlnt();
        if (first == null) {
            return;
        }
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(first);
        markerOption.draggable(true);
        markerOption.icon(start);
        markerOption.anchor(0.5f, 0.5f);
        markerOption.setFlat(true);

        mMap.getMap().addMarker(markerOption);
    }

    private void drawCurrent() {
        if (RunModel.instance.getRecord().tracks == null
                || RunModel.instance.getRecord().tracks.size() == 0) {
            return;
        }
        LatLng current = RunModel.instance.getRecord().tracks.get(0).getLatlnt();
        if (current == null) {
            return;
        }
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(current);
        markerOption.draggable(true);
        markerOption.icon(run);
        markerOption.anchor(0.5f, 0.5f);
        markerOption.setFlat(true);

        mMap.getMap().addMarker(markerOption);
    }

    private void drawEnd() {
        List<TimeLatLng> tracks = RunModel.instance.getRecord().tracks;
        if (tracks == null || tracks.size() == 0) {
            return;
        }
        TimeLatLng last = tracks.get(tracks.size() - 1);
        if (last == null) {
            return;
        }
        LatLng point = last.getLatlnt();
        if (point == null) {
            return;
        }
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(point);
        markerOption.draggable(true);
        markerOption.icon(stop);
        markerOption.anchor(0.5f, 0.5f);
        markerOption.setFlat(true);

        mMap.getMap().addMarker(markerOption);
    }

    private class RunMapReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RunModel.RUN_SERVICE_START_ACTION)) {
                startAutoRefresh();
            } else if (intent.getAction().equals(RUN_MAP_FINISH_ACTION)) {
                finish();
            }
        }
    }

}
