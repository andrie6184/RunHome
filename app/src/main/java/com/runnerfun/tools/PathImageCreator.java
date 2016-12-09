package com.runnerfun.tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.amap.api.maps.model.LatLng;

import java.util.List;


/**
 * Created by lixiaoyang on 06/12/2016.
 */

public class PathImageCreator {

    public static LatLng[] getBounds(List<LatLng> lls){
        if(lls == null || lls.size() <= 0){
            return null;
        }

        double left = lls.get(0).longitude;
        double top = lls.get(0).latitude;
        double right = left;
        double bottom = top;
        for(LatLng ll : lls){
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

        LatLng[] l = new LatLng[2];
        l[0] = new LatLng(bottom, left);
        l[1] = new LatLng(top, right);
        return l;
    }

    public static LatLng getCenter(List<LatLng> ll){
        LatLng[] l = getBounds(ll);
        return new LatLng((l[0].latitude + l[1].latitude)/2, (l[0].longitude + l[1].longitude)/2);
    }

    public static Bitmap createBitmap(List<LatLng> latLngs){
        Bitmap bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        if(latLngs == null || latLngs.size() <= 0){
            return bmp;
        }
        Path path = new Path();
        p.setColor(Color.BLACK);
        LatLng[] ls = getBounds(latLngs);
        double widthScale = 100 / (ls[1].longitude - ls[0].longitude);
        double heightScale = 100 / (ls[1].latitude - ls[0].latitude);

        double x1 = (latLngs.get(0).longitude - ls[0].longitude) * widthScale;
        double y1 = (ls[1].latitude - latLngs.get(0).latitude )* heightScale;
        path.moveTo((float) x1, (float) y1);
        for(LatLng l : latLngs){
            double x = (l.longitude - ls[0].longitude) * widthScale;
            double y = (ls[1].latitude - l.latitude )* heightScale;
            path.lineTo((float)x, (float)y);
        }

        c.drawPath(path, p);

        return bmp;
    }
}
