package com.runnerfun.tools;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

/**
 * UITools
 * Created by andrie on 19/10/2016.
 */

public class UITools {

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String numberFormat(String num) {
        float value = Float.valueOf(num);
        return numberFormat(value);
    }

    public static String numberFormat(float num) {
        String value = String.valueOf(num);

        DecimalFormat format = new DecimalFormat("0.00");
        try {
            value = format.format(num);
        } catch (Exception e) {
        }
        return value;
    }

}
