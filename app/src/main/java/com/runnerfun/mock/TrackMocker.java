package com.runnerfun.mock;

import com.amap.api.maps.model.LatLng;
import com.runnerfun.model.RecordModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TrackMocker
 * Created by andrie on 04/12/2016.
 */

public class TrackMocker {
    public static final TrackMocker instance = new TrackMocker();
    private Random mSeed = new Random();
    public List<LatLng> mockdata = new ArrayList<LatLng>();
    {
        mockdata.add(new LatLng(39.9086611069,116.3975273161));
        mockdata.add(new LatLng(39.9161718640,116.4148932713));
        mockdata.add(new LatLng(39.9416926475,116.4152854934));
        mockdata.add(new LatLng(39.9955216684,116.4164003901));
    };

    private Subscription listener = null;

    public void startMock2(){}
    public void startMock(){
        listener = Observable.interval(2000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                double lat = mSeed.nextDouble() / 1000 + 39.9;
                double lng = mSeed.nextDouble()  /1000 + 116.4;
                RecordModel.instance.addRecord(new LatLng(lat, lng));
            }
        });
    }

    public void stopMock(){
        if (listener != null) {
            listener.unsubscribe();
        }
    }
}
