package com.runnerfun.xyzrunpackage;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.runnerfun.BaseActivity;
import com.runnerfun.R;
import com.runnerfun.ShareTargetActivity;
import com.runnerfun.model.TimeLatLng;
import com.runnerfun.tools.TimeStringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrackMapActivity extends BaseActivity {

    public static final String PARAM_TRACK_RUN_ID = "TRACK_RUN_ID";

    @BindView(R.id.map)
    MapView mMap;

    private BitmapDescriptor stop = null;
    private BitmapDescriptor start = null;

    private String rid;
    private DecimalFormat decimalFormat = new DecimalFormat("0.000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_map);
        ButterKnife.bind(this);
        rid = getIntent().getStringExtra(PARAM_TRACK_RUN_ID);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMap.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMap.onDestroy();
    }

    private void init() {
        stop = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.zhong));
        start = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.shi));

        Typeface boldTypeFace = Typeface.createFromAsset(getAssets(), "fonts/dincond-bold.otf");
        ((TextView) findViewById(R.id.cal).findViewById(R.id.attr_name)).setText("卡路里");
        ((TextView) findViewById(R.id.speed).findViewById(R.id.attr_name)).setText("配速");
        ((TextView) findViewById(R.id.distance).findViewById(R.id.attr_name)).setText("距离");
        ((TextView) findViewById(R.id.time).findViewById(R.id.attr_name)).setText("时间");

        TextView mCalValue = (TextView) findViewById(R.id.cal).findViewById(R.id.attr_value);
        TextView mSpeedValue = (TextView) findViewById(R.id.speed).findViewById(R.id.attr_value);
        TextView mDisValue = (TextView) findViewById(R.id.distance).findViewById(R.id.attr_value);
        TextView mTimeValue = (TextView) findViewById(R.id.time).findViewById(R.id.attr_value);

        mCalValue.setTypeface(boldTypeFace);
        mSpeedValue.setTypeface(boldTypeFace);
        mDisValue.setTypeface(boldTypeFace);
        mTimeValue.setTypeface(boldTypeFace);

        if (RunModel.instance.getState() == RunModel.RUN_STATE_STOP) {
            showRecord(RunModel.instance.getRecord().tracks);

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
    }

    @OnClick(R.id.cancel_btn)
    void onCancelClicked(View view) {
        finish();
    }

    @OnClick(R.id.share_btn)
    void onShareClicked(View view) {
        if (TextUtils.isEmpty(rid) || rid.equals("-1")) {
            Toast.makeText(this, "本次跑步记录尚未上传到服务器,暂时不能分享", Toast.LENGTH_SHORT).show();
        } else if (RunModel.instance.getState() != RunModel.RUN_STATE_RUNNING) {
            Toast.makeText(this, "本次跑步尚未结束,暂时不能分享", Toast.LENGTH_SHORT).show();
        } else {
            ShareTargetActivity.startWithShareData(this, String.valueOf(RunModel.instance.getDistance()),
                    String.valueOf(RunModel.instance.getSpeed()), String.valueOf(RunModel.instance.getRecordTime()));
        }
    }

    private void showRecord(List<TimeLatLng> records) {
        if (records == null || records.size() <= 0) {
            return;
        }

        PolylineOptions po = new PolylineOptions();
        List<Integer> colors = new ArrayList<>();
        for (TimeLatLng ll : records) {
            if (ll.getSpeed() > (12.2f / 1000f)) { // TODO maybe 7.2f
                colors.add(Color.RED);
            } else {
                colors.add(Color.GREEN);
            }
        }
        po.colorValues(colors);
        po.addAll(TimeLatLng.toLatLngList(records));
        po.width(10f);
        mMap.getMap().clear();
        mMap.getMap().addPolyline(po);

        drawStart();
        drawEnd();
        zoomToBound(TimeLatLng.toLatLngList(records));
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

    private void zoomToBound(List<LatLng> records) {
        LatLngBounds.Builder llb = LatLngBounds.builder();
        for (LatLng a : records) {
            llb.include(a);
        }
        CameraUpdate c = CameraUpdateFactory.newLatLngBounds(llb.build(), 10);
        mMap.getMap().moveCamera(c);
    }

}
