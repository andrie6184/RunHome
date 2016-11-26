package com.runnerfun.tools;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

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
}
