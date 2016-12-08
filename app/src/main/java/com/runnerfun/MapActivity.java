package com.runnerfun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.LatLngBoundsCreator;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.runnerfun.beans.Record;
import com.runnerfun.model.ConfigModel;
import com.runnerfun.model.RecordModel;
import com.runnerfun.tools.PathImageCreator;
import com.runnerfun.tools.TimeStringUtils;
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
import rx.Subscription;


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
        List<LatLng> lls = RecordModel.instance.readCache();
//        LatLng l = PathImageCreator.getCenter(lls);

//        CameraUpdate c =CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
//                .target(l).zoom(mMap.getMap().getCameraPosition().zoom).build());
//        mMap.getMap().moveCamera(c);

        LatLngBounds.Builder llb = LatLngBounds.builder();
        for(LatLng a : lls){
            llb.include(a);
        }
        CameraUpdate c = CameraUpdateFactory.newLatLngBounds(llb.build(), 10);
        mMap.getMap().moveCamera(c);
    }

    private void adjustCamera(LatLng centerLatLng,int range) {
        //http://www.eoeandroid.com/blog-1107295-47621.html
        //当前缩放级别下的比例尺
        //"每像素代表" + scale + "米"
        float scale = mMap.getMap().getScalePerPixel();
        //代表range（米）的像素数量
        int pixel = Math.round(range / scale);
        //小范围，小缩放级别（比例尺较大），有精度损失
        Projection projection = mMap.getMap().getProjection();
        //将地图的中心点，转换为屏幕上的点
        Point center = projection.toScreenLocation(centerLatLng);
        //获取距离中心点为pixel像素的左、右两点（屏幕上的点
        Point right = new Point(center.x + pixel, center.y);
        Point left = new Point(center.x - pixel, center.y);

        //将屏幕上的点转换为地图上的点
        LatLng rightLatlng = projection.fromScreenLocation(right);
        LatLng LeftLatlng = projection.fromScreenLocation(left);

        LatLngBounds bounds = LatLngBounds.builder().include(rightLatlng).include(LeftLatlng).build();
        //bounds.contains();

        mMap.getMap().getMapScreenMarkers();

        //调整可视范围
        //aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds.builder().include(rightLatlng).include(LeftLatlng).build(), 10)); }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMap.onSaveInstanceState(outState);
    }

    @OnClick(R.id.share)
    void share(){
        startActivity(new Intent(this, ShareTargetActivity.class));
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                SharedPreferences sp = getSharedPreferences("location", Context.MODE_PRIVATE);
                sp.edit().putString("location", amapLocation.getCountry() + '·' +amapLocation.getCity()).apply();
                mlocationClient.stopLocation();
                mMap.getMap().moveCamera(CameraUpdateFactory.zoomTo(16f));
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
        CameraUpdate c =CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .target(ll).zoom( mMap.getMap().getCameraPosition().zoom).build());
        mMap.getMap().moveCamera(c);

        ((RecordAdapter)viewPager.getAdapter()).update();
        ((RecordAdapter)viewPager.getAdapter()).notifyDataSetChanged();
    }

    private void setUpMap(LatLng oldData,LatLng newData) {
        // 绘制一个大地曲线
        mMap.getMap().addPolyline((new PolylineOptions())
                .add(oldData, newData)
                .geodesic(true).color(Color.GREEN));
    }

    private class RecordAdapter extends RecyclingPagerAdapter {
        private Map<String, Updater> runInfo;
        private final Context mContext;

        public void update(){
            for(Updater u : runInfo.values()){
                u.onUpdate();
            }
        }

        RecordAdapter(Context context) {
            mContext = context;

            //// for test !!!!!!
            runInfo = new HashMap<>();
            runInfo.put("总消耗", new CalUpdater());
            runInfo.put("平均配速", new SpeedUpdater());
            runInfo.put("总里程", new DisUpdater());
            runInfo.put("总用时", new TimeUpdater());
        }

        InfoValue getItem(int position) {
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
                viewHolder.updater = item.value;
                viewHolder.updater.bind(viewHolder.attrValue);
//                viewHolder.attrValue.setText(item.value);
            }
            return convertView;
        }

        public  class ViewHolder {
            TextView attrName;
            TextView attrValue;
            Updater updater;
        }

        private class InfoValue {

            public InfoValue(String s, Updater v) {
                key = s;
                value = v;
            }

            public String key;
            public Updater value;
        }

        public abstract class Updater {
            private TextView v = null;

            public void bind(TextView v){
                this.v = v;
            }
            public void onUpdate(){
                if(v != null) {
                    v.setText(getValue());
                }
            }

            abstract protected String getValue();
        }

        private class SpeedUpdater extends Updater{
            @Override
            protected String getValue() {
                return RecordModel.instance.getSpeed() + "km/s";
            }
        }

        private class CalUpdater extends Updater{

            @Override
            protected String getValue() {
                return RecordModel.instance.getCal() + "kcal";
            }
        }

        private class DisUpdater extends Updater{

            @Override
            protected String getValue() {
                return RecordModel.instance.getDistance() + "km";
            }
        }

        private class TimeUpdater extends Updater{

            @Override
            protected String getValue() {
                return TimeStringUtils.getTime(RecordModel.instance.getRecordTime());
            }
        }

    }
}
