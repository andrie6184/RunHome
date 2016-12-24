package com.runnerfun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.runnerfun.model.ConfigModel;
import com.runnerfun.model.RecordModel;
import com.runnerfun.model.TimeLatLng;
import com.runnerfun.tools.ThirdpartAuthManager;
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

public class MapActivity extends BaseActivity implements AMapLocationListener,
        RecordModel.RecordChangeListener {
    public static final String DISPLAY_MODE = "display_mode";

    @BindView(R.id.map)
    MapView mMap;
    @BindView(R.id.pause)
    View mPauseView;
    @BindView(R.id.stop)
    View mStopView;
    @BindView(R.id.panel)
    MapBtnWidget mPanelWidget;
    private TextView mPauseBtn;
    @BindView(R.id.share)
    ImageView mShareView;
    @BindView(R.id.back)
    ImageView mBackView;

    @BindView(R.id.relativeLayout)
    RelativeLayout topBar;
    private TextView mCalValue;
    private TextView mSpeedValue;
    private TextView mDisValue;
    private TextView mTimeValue;

    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient = null;
    private Typeface boldTypeFace;
    private Subscription mTimer = null;
    private DecimalFormat decimalFormat = new DecimalFormat("0.000");

    private String rid;
    private String coin;

    public static void startWithDisplayMode(Context context, String distance, String speed,
                                            String time, String cal, String rid, String coin) {
        Intent i = new Intent(context, MapActivity.class);
        i.putExtra(DISPLAY_MODE, true);
        i.putExtra("intent_param_distance", distance);
        i.putExtra("intent_param_speed", speed);
        i.putExtra("intent_param_time", time);
        i.putExtra("intent_param_cal", cal);
        i.putExtra("intent_param_rid", rid);
        i.putExtra("intent_param_coin", coin);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        init();
        mMap.onCreate(savedInstanceState);

        if (ConfigModel.instance.getmMapType() == 1) {
            mMap.getMap().setMapType(AMap.MAP_TYPE_SATELLITE);
        } else if (ConfigModel.instance.getmMapType() == 2) {
            mMap.getMap().setMapType(AMap.MAP_TYPE_NIGHT);
        }
        mPauseBtn = (TextView) mPanelWidget.findViewById(R.id.pause);
        run = BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),
                        R.drawable.run));
        stop = BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),
                        R.drawable.zhong));
        start_ic = BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),
                        R.drawable.shi));
    }

    private void init() {
        mlocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        mlocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mlocationClient.setLocationOption(mLocationOption);

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

        boldTypeFace = Typeface.createFromAsset(getAssets(), "fonts/dincond-bold.otf");
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMap.onResume();
        CameraUpdate cu = CameraUpdateFactory.zoomTo(14.f);
        mMap.getMap().moveCamera(cu);
        boolean displayMode = getIntent().getBooleanExtra(DISPLAY_MODE, false);
        if (displayMode) {
            mPanelWidget.setVisibility(View.GONE);
            LatLng ll = RecordModel.instance.firstLatLng().getLatlnt();
            if (ll != null) {
                showRecord(RecordModel.instance.readCache());
            } else {
                mlocationClient.startLocation();
            }
            // mShareView.setVisibility(View.VISIBLE);
            topBar.setVisibility(View.VISIBLE);
            mBackView.setVisibility(View.GONE);

            mSpeedValue.setText(getIntent().getStringExtra("intent_param_speed"));
            mDisValue.setText(getIntent().getStringExtra("intent_param_distance"));
            mTimeValue.setText(getIntent().getStringExtra("intent_param_time"));
            mCalValue.setText(getIntent().getStringExtra("intent_param_cal"));

            rid = getIntent().getStringExtra("intent_param_rid");
            ThirdpartAuthManager.setLastRidForShare(rid);
            coin = getIntent().getStringExtra("intent_param_coin");
            ThirdpartAuthManager.setLastCoinForShare(rid);
        } else {
            if (RecordModel.instance.isPause()) {
                mPauseBtn.setText("继续");
            } else if (RecordModel.instance.isRecording()) {
                mPauseBtn.setText("暂停");
            } else {
                mPanelWidget.setVisibility(View.GONE);
            }
            if (mTimer != null) {
                mTimer.unsubscribe();
            }
            mTimer = Observable.interval(1000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            updateVvalue();
                        }
                    });
            mlocationClient.startLocation();
            drawLines(RecordModel.instance.readCache());
        }
        RecordModel.instance.addListener(this);
    }

    private void updateVvalue() {
        if (RecordModel.instance.getRecordTime() > 0 && RecordModel.instance.getDistance() > 0) {
            float seconds = RecordModel.instance.getRecordTime() / RecordModel.instance.getDistance();
            int minutes = (int) seconds  / 60;
            String speedShow = String.format(Locale.getDefault(), "%d'%d\"", minutes, (int) (seconds % 60));
            mSpeedValue.setText(speedShow);
        } else {
            mSpeedValue.setText("0'00\"");
        }
        String distance = decimalFormat.format(RecordModel.instance.getDistance() / 1000);
        mDisValue.setText("" + distance + "km");
        mTimeValue.setText(TimeStringUtils.getTime(RecordModel.instance.getRecordTime()));
        mCalValue.setText("" + decimalFormat.format(RecordModel.instance.getCal()) + "kcal");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMap.onDestroy();
        mlocationClient.stopLocation();
        mlocationClient.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMap.onPause();
        RecordModel.instance.removeListener(this);
        if (mTimer != null) {
            mTimer.unsubscribe();
        }
    }

    private void pause() {
        //TODO: resume
        if (RecordModel.instance.isRecording()) {
            RecordService.pauseRecord(this);
            mPauseBtn.setText("继续");
        } else {
            RecordService.resumeRecord(this);
            mPauseBtn.setText("暂停");
        }
    }

    private void stop() {
        //TODO: start
        RecordService.stopRecord(this);
        mPanelWidget.setVisibility(View.GONE);
        // mShareView.setVisibility(View.VISIBLE);
        topBar.setVisibility(View.VISIBLE);
        mBackView.setVisibility(View.GONE);
        List<LatLng> lls = TimeLatLng.toLatLngList(RecordModel.instance.readCache());
//        LatLng l = PathImageCreator.getCenter(lls);

//        CameraUpdate c =CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
//                .target(l).zoom(mMap.getMap().getCameraPosition().zoom).build());
//        mMap.getMap().moveCamera(c);

        LatLngBounds.Builder llb = LatLngBounds.builder();
        for (LatLng a : lls) {
            llb.include(a);
        }
        CameraUpdate c = CameraUpdateFactory.newLatLngBounds(llb.build(), 10);
        mMap.getMap().moveCamera(c);
        if (mTimer != null) {
            mTimer.unsubscribe();
        }
        drawEnd();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMap.onSaveInstanceState(outState);
    }

    @OnClick(R.id.share_btn)
    void share() {
        ShareTargetActivity.startWithShareData(this, getIntent().getStringExtra("intent_param_distance"),
                getIntent().getStringExtra("intent_param_speed"), getIntent().getStringExtra("intent_param_time"));
    }

    @OnClick(R.id.cancel_btn)
    void onCancel() {
        finish();
    }

    @OnClick(R.id.back)
    void onBack() {
        finish();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        mlocationClient.stopLocation();
        if(amapLocation != null && amapLocation.getErrorCode() == 0){
            SharedPreferences sp = getSharedPreferences("location", Context.MODE_PRIVATE);
            sp.edit().putString("location", amapLocation.getCountry() + '·' + amapLocation.getCity()).apply();
            if(RecordModel.instance.isRecording() || RecordModel.instance.isPause()){
                return;//正在记录则丢弃当前定位
            }
            else{
                moveTo(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()));
                mMap.getMap().moveCamera(CameraUpdateFactory.zoomTo(14f));
                drawStart();
            }
        }
    }

    private void moveTo(LatLng ll) {
        CameraUpdate c = CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .target(ll).build());
        mMap.getMap().moveCamera(c);
    }

    private BitmapDescriptor run = null;
    private BitmapDescriptor stop = null;
    private BitmapDescriptor start_ic = null;

    private void drawLines(List<TimeLatLng> records) {
        if (records == null || records.size() <= 0) {
            return;
        }

        TimeLatLng start = records.get(0);
        PolylineOptions po = new PolylineOptions();
        List<Integer> colors = new ArrayList<>();
        for (TimeLatLng ll : records) {
            if (ll.speed(start) > 12.2f) {
                colors.add(Color.RED);
            } else {
                colors.add(Color.GREEN);
            }
            start = ll;
        }
//        po.useGradient(true);
        po.colorValues(colors);
        po.addAll(TimeLatLng.toLatLngList(records));
        po.width(10f);
        mMap.getMap().clear();
        mMap.getMap().addPolyline(po);

        drawStart();
        if(RecordModel.instance.isPause() || RecordModel.instance.isRecording()){
            drawCurrent();
        }
        else {
            drawEnd();
        }
    }

    private void showRecord(List<TimeLatLng> records) {
        if (records == null || records.size() <= 0) {
            return;
        }

        TimeLatLng start = records.get(0);
        PolylineOptions po = new PolylineOptions();
        List<Integer> colors = new ArrayList<>();
        for (TimeLatLng ll : records) {
            if (ll.getSpeed() > (12.2f / 1000f)) {
                colors.add(Color.RED);
            } else {
                colors.add(Color.GREEN);
            }
        }
//        po.useGradient(true);
        po.colorValues(colors);
        po.addAll(TimeLatLng.toLatLngList(records));
        po.width(10f);
        mMap.getMap().clear();
        mMap.getMap().addPolyline(po);

        drawStart();
        drawEnd();
        zoomToBound(TimeLatLng.toLatLngList(records));
    }

    @Override
    public void onRecordChange(LatLng ll) {
        drawLines(RecordModel.instance.readCache());
        CameraUpdate c = CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(ll)
                .zoom(mMap.getMap().getCameraPosition().zoom).build());
        mMap.getMap().moveCamera(c);
    }

    private void drawStart(){
        LatLng start = RecordModel.instance.firstLatLng().getLatlnt();
        if(start == null){
            return;
        }
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(start);
        markerOption.draggable(true);
        markerOption.icon(start_ic);
        markerOption.anchor(0.5f, 0.5f);
        markerOption.setFlat(true);

        mMap.getMap().addMarker(markerOption);
    }

    private void drawCurrent(){
        LatLng start = RecordModel.instance.lastLatLng().getLatlnt();
        if(start == null){
            return;
        }
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(start);
        markerOption.draggable(true);
        markerOption.icon(run);
        markerOption.anchor(0.5f, 0.5f);
        markerOption.setFlat(true);

        mMap.getMap().addMarker(markerOption);
    }

    private void drawEnd(){
        LatLng start = RecordModel.instance.lastLatLng().getLatlnt();
        if(start == null){
            return;
        }
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(start);
        markerOption.draggable(true);
        markerOption.icon(stop);
        markerOption.anchor(0.5f, 0.5f);
        markerOption.setFlat(true);

        mMap.getMap().addMarker(markerOption);
    }

    private void zoomToBound(List<LatLng> records){
        LatLngBounds.Builder llb = LatLngBounds.builder();
        for (LatLng a : records) {
            llb.include(a);
        }
        CameraUpdate c = CameraUpdateFactory.newLatLngBounds(llb.build(), 10);
        mMap.getMap().moveCamera(c);
    }

}
