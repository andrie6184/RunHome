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

public class MapActivity extends AppCompatActivity implements AMapLocationListener {
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
        }
        else{
            mlocationClient.startLocation();
            //TODO:RecordModel
        }
//        initCycleList();
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
    }

    @OnClick(R.id.back)
    void onBack(){
        finish();
    }

    private void pause(){
        //TODO: resume
        RecordService.pauseRecord(this);
    }

    private void stop(){
        //TODO: start
        RecordService.stopRecord(this);
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                setLocationMark(amapLocation.getLatitude(), amapLocation.getLongitude());
                mlocationClient.stopLocation();
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    private void setLocationMark(double lat, double lgt){
        MarkerOptions mark = new MarkerOptions()
                .position(new LatLng(lat, lgt))
                .title("your location");

        mark.icon(
                BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),
                                R.drawable.icon_shezhi)));

        LatLng ll = new LatLng(lat, lgt);
        mMap.getMap().clear();
        mMap.getMap().addMarker(mark);
        CameraUpdate newPos = CameraUpdateFactory.newLatLng(ll);
        mMap.getMap().animateCamera(newPos);
        mMap.getMap().moveCamera(CameraUpdateFactory.zoomTo(14.f));
    }

    private void drawLines(List<LatLng> records){
        List<LatLng> latLngs = new ArrayList<LatLng>();
        latLngs.add(new LatLng(39.9086611069,116.3975273161));
        latLngs.add(new LatLng(39.9161718640,116.4148932713));
        latLngs.add(new LatLng(39.9416926475,116.4152854934));
        latLngs.add(new LatLng(39.9955216684,116.4164003901));
        mMap.getMap().addPolyline(new PolylineOptions().
                addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));
    }

}
