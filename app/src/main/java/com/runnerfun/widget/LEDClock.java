package com.runnerfun.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by andrie on 16/10/2016.
 */

public class LEDClock extends View {
    private Paint mPaint = new Paint();

    public LEDClock(Context context) {
        super(context);
        mPaint.setColor(Color.BLACK);
    }

    public LEDClock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LEDClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("00:00:00", 0.f, 0f, mPaint);
    }
}
