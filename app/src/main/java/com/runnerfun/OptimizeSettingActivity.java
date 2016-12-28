package com.runnerfun;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.runnerfun.model.OptimizeConfigModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OptimizeSettingActivity extends BaseActivity {

    @BindView(R.id.gps_state)
    TextView gpsStatus;
    private boolean checkState;

    @BindView(R.id.type_wifi)
    TextView wifiType;
    @BindView(R.id.type_cell)
    TextView cellType;
    @BindView(R.id.type_amap)
    TextView amapType;
    private List<String> sourceTypes;

    @BindView(R.id.accuracy)
    EditText accuracy;
    private float gpsAccValue;
    @BindView(R.id.length)
    EditText length;
    private float lengthValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimaze_setting);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        checkState = OptimizeConfigModel.instance.isCheckGPSState();
        if (checkState) {
            gpsStatus.setTextColor(Color.parseColor("#E60012"));
        } else {
            gpsStatus.setTextColor(Color.parseColor("#000000"));
        }

        sourceTypes = OptimizeConfigModel.instance.getSourceType();
        if (sourceTypes.contains(String.valueOf(AMapLocation.LOCATION_TYPE_WIFI))) {
            wifiType.setTextColor(Color.parseColor("#E60012"));
        } else {
            wifiType.setTextColor(Color.parseColor("#000000"));
        }
        if (sourceTypes.contains(String.valueOf(AMapLocation.LOCATION_TYPE_CELL))) {
            cellType.setTextColor(Color.parseColor("#E60012"));
        } else {
            cellType.setTextColor(Color.parseColor("#000000"));
        }
        if (sourceTypes.contains(String.valueOf(AMapLocation.LOCATION_TYPE_AMAP))) {
            amapType.setTextColor(Color.parseColor("#E60012"));
        } else {
            amapType.setTextColor(Color.parseColor("#000000"));
        }

        gpsAccValue = OptimizeConfigModel.instance.getGPSAccuracy();
        accuracy.setText(String.valueOf(gpsAccValue));

        lengthValue = OptimizeConfigModel.instance.getLengthPercent();
        length.setText(String.valueOf(lengthValue * 100));
    }

    @OnClick(R.id.cancel_btn)
    void onCancelClicked(View view) {
        finish();
    }

    @OnClick(R.id.save_btn)
    void onSaveClicked(View view) {
        OptimizeConfigModel.instance.setCheckGPSState(checkState);
        OptimizeConfigModel.instance.setSourceType(sourceTypes);

        gpsAccValue = Float.valueOf(accuracy.getText().toString());
        if (gpsAccValue > 0f && gpsAccValue <= 100f) {
            OptimizeConfigModel.instance.setGPSAccuracy(gpsAccValue);
        } else {
            accuracy.setError("输入不合法");
            return;
        }

        lengthValue = Float.valueOf(length.getText().toString());
        if (lengthValue > 1f && lengthValue <= 200f) {
            OptimizeConfigModel.instance.setLengthPercent(lengthValue / 100);
        } else {
            length.setError("输入不合法");
            return;
        }

        OptimizeConfigModel.instance.save(this);
        finish();
    }

    @OnClick(R.id.gps_state)
    void onGPSStateClicked(View view) {
        checkState = !checkState;
        if (checkState) {
            gpsStatus.setTextColor(Color.parseColor("#E60012"));
        } else {
            gpsStatus.setTextColor(Color.parseColor("#000000"));
        }
    }

    @OnClick(R.id.type_wifi)
    void onWifiClicked(View view) {
        if (sourceTypes.contains(String.valueOf(AMapLocation.LOCATION_TYPE_WIFI))) {
            sourceTypes.remove(String.valueOf(AMapLocation.LOCATION_TYPE_WIFI));
        } else {
            sourceTypes.add(String.valueOf(AMapLocation.LOCATION_TYPE_WIFI));
        }
        if (sourceTypes.contains(String.valueOf(AMapLocation.LOCATION_TYPE_WIFI))) {
            wifiType.setTextColor(Color.parseColor("#E60012"));
        } else {
            wifiType.setTextColor(Color.parseColor("#000000"));
        }
    }

    @OnClick(R.id.type_cell)
    void onCellClicked(View view) {
        if (sourceTypes.contains(String.valueOf(AMapLocation.LOCATION_TYPE_CELL))) {
            sourceTypes.remove(String.valueOf(AMapLocation.LOCATION_TYPE_CELL));
        } else {
            sourceTypes.add(String.valueOf(AMapLocation.LOCATION_TYPE_CELL));
        }
        if (sourceTypes.contains(String.valueOf(AMapLocation.LOCATION_TYPE_CELL))) {
            cellType.setTextColor(Color.parseColor("#E60012"));
        } else {
            cellType.setTextColor(Color.parseColor("#000000"));
        }
    }

    @OnClick(R.id.type_amap)
    void onAmapClicked(View view) {
        if (sourceTypes.contains(String.valueOf(AMapLocation.LOCATION_TYPE_AMAP))) {
            sourceTypes.remove(String.valueOf(AMapLocation.LOCATION_TYPE_AMAP));
        } else {
            sourceTypes.add(String.valueOf(AMapLocation.LOCATION_TYPE_AMAP));
        }
        if (sourceTypes.contains(String.valueOf(AMapLocation.LOCATION_TYPE_AMAP))) {
            amapType.setTextColor(Color.parseColor("#E60012"));
        } else {
            amapType.setTextColor(Color.parseColor("#000000"));
        }
    }

}
