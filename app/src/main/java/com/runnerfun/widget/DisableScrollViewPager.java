package com.runnerfun.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by lixiaoyang on 24/10/2016.
 */

public class DisableScrollViewPager extends ViewPager {
    private boolean mEnableScroll = false;

    public DisableScrollViewPager(Context context) {
        super(context);
    }

    public DisableScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mEnableScroll && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mEnableScroll && super.onInterceptTouchEvent(event);
    }

    public void setScrollEnabled(boolean b) {
        mEnableScroll = b;
    }
}
