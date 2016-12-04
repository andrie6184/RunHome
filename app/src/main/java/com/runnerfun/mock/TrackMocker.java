package com.runnerfun.mock;

import com.amap.api.maps2d.model.LatLng;
import com.runnerfun.model.RecordModel;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by lixiaoyang on 04/12/2016.
 */

public class TrackMocker {
    public static final TrackMocker instance = new TrackMocker();
    private Random mSeed = new Random();
    private LatLng[] mockdata = {
            new LatLng(39.9086611069,116.3975273161),
            new LatLng(39.9161718640,116.4148932713),
            new LatLng(39.9416926475,116.4152854934),
            new LatLng(39.9955216684,116.4164003901)
    };

    public void startMock(){}
    public void startMock(int i){
        Observable.interval(2000, TimeUnit.MILLISECONDS).
                subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                double lat = mSeed.nextDouble() + 39;
                double lng = mSeed.nextDouble() + 116;
                RecordModel.instance.addRecord(new LatLng(lat, lng));
            }
        });
    }
}
