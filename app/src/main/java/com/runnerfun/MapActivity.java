package com.runnerfun;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.runnerfun.beans.Record;
import com.runnerfun.model.RecordModel;
import com.runnerfun.widget.MapBtnWidget;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscriber;

public class MapActivity extends AppCompatActivity implements AMapLocationListener, RecordModel.RecordChangeListener {
    public static final String DISPLAY_MODE = "display_mode";

    @BindView(R.id.map)
    MapView mMap;
    @BindView(R.id.data_list)
    RecyclerView mList;
    @BindView(R.id.pause)
    View mPauseView;
    @BindView(R.id.stop)
    View mStopView;
    @BindView(R.id.panel)
    MapBtnWidget mPanelWidget;
    private TextView mPauseBtn;

    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient = null;

    public static void startWithDisplayMode(Context c){
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
        mMap.getMap().getUiSettings().setMyLocationButtonEnabled(true);
        mPauseBtn = (TextView)mPanelWidget.findViewById(R.id.pause);
    }

    private void init() {
        mlocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        mlocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMap.onResume();
        boolean displayMode = getIntent().getBooleanExtra(DISPLAY_MODE, false);
        if(displayMode){
            mPanelWidget.setVisibility(View.GONE);
            LatLng ll = RecordModel.instance.firstLatLng();
            if(ll != null){
                zoomTo(ll);
            }
            else{
                mlocationClient.startLocation();
            }
        }
        else{
            mlocationClient.startLocation();
            //TODO:RecordModel
        }
        RecordModel.instance.addListener(this);
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
    }

    @OnClick(R.id.back)
    void onBack(){
        finish();
    }

    private void pause(){
        //TODO: resume
        if(RecordModel.instance.isRecording()) {
            RecordService.pauseRecord(this);
            mPauseBtn.setText("继续");
        }
        else{
            RecordService.resumeRecord(this);
            mPauseBtn.setText("暂停");
        }
    }

    private void stop(){
        //TODO: start
        RecordService.stopRecord(this);
        mPanelWidget.setVisibility(View.GONE);
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mlocationClient.stopLocation();
                zoomTo(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()));
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    private void zoomTo(LatLng ll){
        MarkerOptions mark = new MarkerOptions()
                .position(ll)
                .title("your location");

        mark.icon(
                BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),
                                R.drawable.icon_shezhi)));

        mMap.getMap().clear();
        mMap.getMap().addMarker(mark);
        CameraUpdate newPos = CameraUpdateFactory.newLatLng(ll);
        mMap.getMap().animateCamera(newPos);
        mMap.getMap().moveCamera(CameraUpdateFactory.zoomTo(14.f));
    }

    private void drawLines(List<LatLng> records){
        mMap.getMap().addPolyline(new PolylineOptions().
                addAll(records).width(10).color(Color.argb(255, 1, 1, 1)));
    }

    @Override
    public void onRecordChange(LatLng ll) {
        drawLines(RecordModel.instance.readCache());
    }
}
