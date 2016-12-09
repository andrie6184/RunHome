package com.runnerfun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.runnerfun.beans.Record;
import com.runnerfun.model.RecordModel;
import com.runnerfun.tools.TimeStringUtils;
import com.runnerfun.widget.MapBtnWidget;
import com.runnerfun.widget.RecyclingPagerAdapter;
import com.runnerfun.widget.ScalePageTransformer;
import com.runnerfun.widget.TransformViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class MapActivity extends AppCompatActivity implements AMapLocationListener, RecordModel.RecordChangeListener {
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

    public static void startWithDisplayMode(Context c) {
        Intent i = new Intent();
        i.putExtra(DISPLAY_MODE, true);
        c.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        init();
        mMap.onCreate(savedInstanceState);
//
//        if(ConfigModel.instance.getmMapType() != 0){
//            mMap.getMap().setMapType(AMap.MAP_TYPE_SATELLITE);
//        }
        mPauseBtn = (TextView) mPanelWidget.findViewById(R.id.pause);
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
        ((TextView)findViewById(R.id.cal).findViewById(R.id.attr_name)).setText("卡路里");
        ((TextView)findViewById(R.id.speed).findViewById(R.id.attr_name)).setText("速度");
        ((TextView)findViewById(R.id.distance).findViewById(R.id.attr_name)).setText("距离");
        ((TextView)findViewById(R.id.time).findViewById(R.id.attr_name)).setText("时间");
        mCalValue = (TextView)findViewById(R.id.cal).findViewById(R.id.attr_value);
        mSpeedValue = (TextView)findViewById(R.id.speed).findViewById(R.id.attr_value);
        mDisValue = (TextView)findViewById(R.id.distance).findViewById(R.id.attr_value);
        mTimeValue = (TextView)findViewById(R.id.time).findViewById(R.id.attr_value);
        mCalValue.setTypeface(boldTypeFace);
        mSpeedValue.setTypeface(boldTypeFace);
        mDisValue.setTypeface(boldTypeFace);
        mTimeValue.setTypeface(boldTypeFace);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMap.onResume();
        boolean displayMode = getIntent().getBooleanExtra(DISPLAY_MODE, false);
        if (displayMode) {
            mPanelWidget.setVisibility(View.GONE);
            LatLng ll = RecordModel.instance.firstLatLng();
            if (ll != null) {
                zoomTo(ll);
            } else {
                mlocationClient.startLocation();
            }
            // mShareView.setVisibility(View.VISIBLE);
            topBar.setVisibility(View.VISIBLE);
            mBackView.setVisibility(View.GONE);
        } else {
            if (RecordModel.instance.isPause()) {
                mPauseBtn.setText("继续");
            } else if (RecordModel.instance.isRecording()) {
                mPauseBtn.setText("暂停");
            } else {
                mPanelWidget.setVisibility(View.GONE);
            }
            mlocationClient.startLocation();
            if(mTimer != null){
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
            //TODO:RecordModel
        }
        RecordModel.instance.addListener(this);
    }

    private void updateVvalue(){
        mSpeedValue.setText(""+RecordModel.instance.getSpeed() + "km/s");
        mDisValue.setText(""+RecordModel.instance.getDistance() + "km");
        mTimeValue.setText(TimeStringUtils.getTime(RecordModel.instance.getRecordTime()));
        mCalValue.setText(""+RecordModel.instance.getCal() + "cal");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMap.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMap.onPause();
        RecordModel.instance.removeListener(this);
        if(mTimer != null){
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
        List<LatLng> lls = RecordModel.instance.readCache();
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
        if(mTimer != null){
            mTimer.unsubscribe();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMap.onSaveInstanceState(outState);
    }

    @OnClick(R.id.share_btn)
    void share() {
        startActivity(new Intent(this, ShareTargetActivity.class));
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
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                SharedPreferences sp = getSharedPreferences("location", Context.MODE_PRIVATE);
                sp.edit().putString("location", amapLocation.getCountry() + '·' + amapLocation.getCity()).apply();
                mlocationClient.stopLocation();
                mMap.getMap().moveCamera(CameraUpdateFactory.zoomTo(16f));
                zoomTo(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()));
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    private void zoomTo(LatLng ll) {
        MarkerOptions mark = new MarkerOptions()
                .position(ll)
                .title("your location");

        mark.icon(
                BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),
                                R.drawable.icon_shezhi)));

        mMap.getMap().addMarker(mark);
        CameraUpdate c = CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .target(ll).zoom(mMap.getMap().getCameraPosition().zoom).build());
        mMap.getMap().moveCamera(c);
    }

    private void drawLines(List<LatLng> records) {
        if (records == null || records.size() <= 0) {
            return;
        }

        LatLng start = records.get(0);
        PolylineOptions po = new PolylineOptions();
        List<Integer> colors = new ArrayList<>();
        for (LatLng ll : records) {
            float distance = AMapUtils.calculateLineDistance(start, ll);
            if (distance > 7.2f) {
                colors.add(Color.RED);
            } else {
                colors.add(Color.GREEN);
            }
            start = ll;
        }
        po.useGradient(true);
        po.colorValues(colors);
        po.addAll(records);
        po.width(10f);
        po.geodesic(true);
        mMap.getMap().clear();
        mMap.getMap().addPolyline(po);
    }

    @Override
    public void onRecordChange(LatLng ll) {
        drawLines(RecordModel.instance.readCache());
        CameraUpdate c = CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .target(ll).zoom(mMap.getMap().getCameraPosition().zoom).build());
        mMap.getMap().moveCamera(c);
    }

}
