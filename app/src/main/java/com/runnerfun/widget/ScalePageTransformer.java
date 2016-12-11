package com.runnerfun.widget;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * ScalePageTransformer
 * Created by andrie on 16/11/9.
 */

public class ScalePageTransformer implements ViewPager.PageTransformer {

    private float MAX_SCALE = 0.9f;
    private float MIN_SCALE = 0.8f;

    public ScalePageTransformer() {
    }

    public ScalePageTransformer(float maxScale, float minScale) {
        MAX_SCALE = maxScale;
        MIN_SCALE = minScale;
    }

    @Override
    public void transformPage(View page, float position) {
        if (position < -1) {
            position = -1;
        } else if (position > 1) {
            position = 1;
        }

        float tempScale = position < 0 ? 1 + position : 1 - position;

        float slope = (MAX_SCALE - MIN_SCALE) / 1;
        float scaleValue = MIN_SCALE + tempScale * slope;
        page.setScaleX(scaleValue);
        page.setScaleY(scaleValue);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            page.getParent().requestLayout();
        }
    }

}
