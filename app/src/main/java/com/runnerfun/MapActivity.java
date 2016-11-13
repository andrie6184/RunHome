package com.runnerfun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapActivity extends AppCompatActivity implements AMapLocationListener {

    @BindView(R.id.map)
    MapView mMap;
    @BindView(R.id.data_list)
    RecyclerView mList;

    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        init();
        mMap.onCreate(savedInstanceState);
    }

    private void init() {
        mlocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        mlocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMap.onResume();
        mlocationClient.startLocation();
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

        LatLng ll = new LatLng(lat, lgt);
        mMap.getMap().clear();
        mMap.getMap().addMarker(mark);
        CameraUpdate newPos = CameraUpdateFactory.newLatLng(ll);
        mMap.getMap().animateCamera(newPos);
        mMap.getMap().moveCamera(CameraUpdateFactory.zoomTo(14.f));
    }

}
