package com.runnerfun;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.runnerfun.model.ConfigModel;
import com.runnerfun.model.RecordModel;
import com.runnerfun.widget.MapBtnWidget;
import com.runnerfun.widget.RecyclingPagerAdapter;
import com.runnerfun.widget.ScalePageTransformer;
import com.runnerfun.widget.TransformViewPager;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


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
    @BindView(R.id.info_viewpager)
    TransformViewPager viewPager;
    @BindView(R.id.share)
    ImageView mShareView;

    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient = null;
    private Typeface boldTypeFace;

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
//
//        if(ConfigModel.instance.getmMapType() != 0){
//            mMap.getMap().setMapType(AMap.MAP_TYPE_SATELLITE);
//        }
        mPauseBtn = (TextView)mPanelWidget.findViewById(R.id.pause);
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

        viewPager.setPageTransformer(true, new ScalePageTransformer(1.2f, 0.8f));
        viewPager.setAdapter(new RecordAdapter(this));
        viewPager.setOffscreenPageLimit(7);
        viewPager.setCurrentItem(5000);
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
            mShareView.setVisibility(View.VISIBLE);
        }
        else{
            if(RecordModel.instance.isPause()) {
                mPauseBtn.setText("继续");
            }
            else if(RecordModel.instance.isRecording()){
                mPauseBtn.setText("暂停");
            }
            else{
                mPanelWidget.setVisibility(View.GONE);
            }
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
        mShareView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.share)
    void share(){
        startActivity(new Intent(this, ShareTargetActivity.class));
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mlocationClient.stopLocation();
                mMap.getMap().moveCamera(CameraUpdateFactory.zoomTo(14));
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

        mMap.getMap().addMarker(mark);
        CameraUpdate c =CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .target(ll).zoom( mMap.getMap().getCameraPosition().zoom).build());
        mMap.getMap().moveCamera(c);
//        CameraUpdate newPos = CameraUpdateFactory.newLatLng(ll);
//        mMap.getMap().animateCamera(newPos);
//        mMap.getMap().moveCamera(CameraUpdateFactory.zoomTo(14.f));
    }

    private void drawLines(List<LatLng> records){
        if(records == null || records.size() <= 0){
            return;
        }

        LatLng start = records.get(0);
        PolylineOptions po = new PolylineOptions();
        List<Integer> colors = new ArrayList<>();
        for(LatLng ll : records){
            float distance = AMapUtils.calculateLineDistance(start, ll);
            if(distance > 7.2f){
                colors.add(Color.RED);
            }
            else{
                colors.add(Color.GREEN);
            }
        }
        po.colorValues(colors);
        po.addAll(records);
        po.width(10f);
        mMap.getMap().clear();
        mMap.getMap().addPolyline(po);
    }

    @Override
    public void onRecordChange(LatLng ll) {
        drawLines(RecordModel.instance.readCache());
//        zoomTo(ll);
        CameraUpdate c =CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .target(ll).zoom( mMap.getMap().getCameraPosition().zoom).build());
        mMap.getMap().moveCamera(c);
    }


    public class RecordAdapter extends RecyclingPagerAdapter {
        private Map<String, String> runInfo;
        private final Context mContext;

        public RecordAdapter(Context context) {
            mContext = context;

            //// for test !!!!!!
            runInfo = new HashMap<>();
            runInfo.put("总消耗", "45kcal");
            runInfo.put("平均配速", "12km/s");
            runInfo.put("总里程", "13km");
            runInfo.put("总用时", "20:12");
        }

        public InfoValue getItem(int position) {
            int offset = position % 4;
            String[] key = new String[4];
            key = runInfo.keySet().toArray(key);
            return new InfoValue(key[offset], runInfo.get(key[offset]));
        }

        @Override
        public int getCount() {
            return 10001;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            final ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                LayoutInflater mInflater = LayoutInflater.from(mContext);
                convertView = mInflater.inflate(R.layout.layout_run_record_item, null);

                viewHolder.attrName = (TextView) convertView.findViewById(R.id.attr_name);
                viewHolder.attrValue = (TextView) convertView.findViewById(R.id.attr_value);
                viewHolder.attrValue.setTypeface(boldTypeFace);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final InfoValue item = getItem(position);
            if (item != null) {
                viewHolder.attrName.setText(item.key);
                viewHolder.attrValue.setText(item.value);
            }
            return convertView;
        }

        private class ViewHolder {
            TextView attrName;
            TextView attrValue;
        }

        private class InfoValue {

            public InfoValue(String s, String v) {
                key = s;
                value = v;
            }

            public String key;
            public String value;
        }

    }
}
