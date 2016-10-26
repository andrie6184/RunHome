package com.runnerfun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.runnerfun.widget.HorizontalListView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.amap.api.maps2d.CameraUpdateFactory.newLatLngZoom;

/**
 * Created by lixiaoyang on 16/10/2016.
 */

public class MapFragment extends Fragment implements AMapLocationListener {
    @BindView(R.id.map)
    MapView mMap;
    @BindView(R.id.data_list)
    HorizontalListView mList;

    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMap.onCreate(savedInstanceState);
        mlocationClient = new AMapLocationClient(getActivity());
        mLocationOption = new AMapLocationClientOption();
        mlocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();

        mMap.getMap().getUiSettings().setZoomControlsEnabled(false);


        mList.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 100;
            }

            @Override
            public Object getItem(int position) {
                return position  % 5;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = new TextView(getActivity());
                }
                ((TextView)convertView).setText(String.valueOf(position));
                return convertView;
            }
        });
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mMap.onResume();
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
        Fragment f = getParentFragment();
        if(f instanceof MainFragment){
            ((MainFragment)f).switchToData();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                setLocationMark(amapLocation.getLatitude(), amapLocation.getLongitude());
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    private void setLocationMark(double lat, double lgt){
        Bitmap pin = BitmapFactory.decodeResource(getResources(), R.drawable.icon_shezhi);
        MarkerOptions mark = new MarkerOptions()
                .position(new LatLng(lat, lgt))
                .title("your location");
        mMap.getMap().clear();
        mMap.getMap().addMarker(mark);
        CameraUpdate newPos = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lgt), 14.f);
        mMap.getMap().animateCamera(newPos);
    }

}
