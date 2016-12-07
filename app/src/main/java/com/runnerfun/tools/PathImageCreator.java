package com.runnerfun.tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.amap.api.maps.model.LatLng;

import java.util.List;


/**
 * Created by lixiaoyang on 06/12/2016.
 */

public class PathImageCreator {
    public Bitmap createBitmap(List<LatLng> latLngs){
        Bitmap bmp = Bitmap.createBitmap(800, 800, Bitmap.Config.RGB_565);
        if(latLngs == null || latLngs.size() <= 0){
            return bmp;
        }
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas c = new Canvas(bmp);

        double left = latLngs.get(0).longitude;
        double top = latLngs.get(0).latitude;
        double right = left;
        double bottom = top;
        for(LatLng ll : latLngs){
            if(ll.longitude < left){
                left = ll.longitude;
            }
            if(ll.longitude > right){
                right = ll.longitude;
            }
            if(ll.latitude > top){
                top = ll.latitude;
            }
            if(ll.latitude < bottom){
                bottom = ll.latitude;
            }
        }




        return bmp;
    }
}
