package com.runnerfun.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by andrie on 19/10/2016.
 */

public class MapBtnWidget extends LinearLayout {
    public static interface OnButtonClick{
        void onLeftClick();
        void onRightClick();
    }

    private Paint mRedPaint;
    private Paint mBlackPaint;
    private Path mPath = new Path();
    private OnButtonClick mListener;

    public MapBtnWidget(Context context) {
        super(context);
        init();
    }

    public MapBtnWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MapBtnWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOnClickListener(OnButtonClick listener){
        mListener = listener;
    }

    private void init(){
        setWillNotDraw(false);
        mRedPaint = new Paint();
        mRedPaint.setColor(Color.RED);
        mBlackPaint = new Paint();
        mBlackPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas c){
        c.drawRect(0.f, 0.f, getWidth(), getHeight(), mRedPaint);
        mPath.reset();
        mPath.moveTo(0, 0);
        mPath.lineTo(getWidth()/2 + (int)(getWidth() * 0.05) , 0);
        mPath.lineTo(getWidth()/2 - (int)(getWidth() * 0.05), getHeight());
        mPath.lineTo(0, getHeight());
        mPath.close();
        c.drawPath(mPath, mBlackPaint);
        super.onDraw(c);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mListener == null){
            return false;
        }
        float x = ev.getX();
        float y = ev.getY();
        float xRange =  (float)(getWidth()/2 * 1.1 - (y * (0.1 * getWidth()) / getHeight()));
        if(x < xRange){
            mListener.onLeftClick();
        }
        else {
            mListener.onRightClick();
        }
        return true;
    }
}
