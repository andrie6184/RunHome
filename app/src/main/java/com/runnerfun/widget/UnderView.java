package com.runnerfun.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.runnerfun.LockScreenActivity;
import com.runnerfun.tools.ApplicationUtils;

/**
 * UnderView
 * Created by andrie on 16/12/26.
 */

public class UnderView extends View {

    private float mStartX;
    private float mWidth;
    public View mMoveView;
    public Handler mHandler;
    public LockScreenActivity mActivity;

    public UnderView(Context context) {
        super(context);
        init();
    }

    public UnderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UnderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public UnderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mWidth = ApplicationUtils.getDeviceWidth();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float nx = event.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = nx;
                onAnimationEnd();
            case MotionEvent.ACTION_MOVE:
                handleMoveView(nx);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                doTriggerEvent(nx);
                break;
        }
        return true;
    }

    private void handleMoveView(float x) {
        float movex = x - mStartX;
        if (movex < 0)
            movex = 0;
        if (mMoveView != null) {
            mMoveView.setTranslationX(movex);
        }

        float mWidthFloat = (float) mWidth;//屏幕显示宽度
        if (getBackground() != null && mMoveView != null) {
            getBackground().setAlpha((int) ((mWidthFloat - mMoveView.getTranslationX()) / mWidthFloat * 200));//初始透明度的值为200
        }
    }

    private void doTriggerEvent(float x) {
        float movex = x - mStartX;
        if (mMoveView != null) {
            if (movex > (mWidth * 0.4)) {
                moveMoveView(mWidth - mMoveView.getLeft(), true);//自动移动到屏幕右边界之外，并finish掉

            } else {
                moveMoveView(-mMoveView.getLeft(), false);//自动移动回初始位置，重新覆盖
            }
        }
    }

    private void moveMoveView(float to, boolean exit) {
        if (mMoveView == null) {
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(mMoveView, "translationX", to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (getBackground() != null) {
                    getBackground().setAlpha((int) (((float) mWidth - mMoveView.getTranslationX()) / (float) mWidth * 200));
                }
            }
        });//随移动动画更新背景透明度
        animator.setDuration(250).start();

        if (exit) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (mHandler != null && mActivity != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.finish();
                            }
                        });
                    }
                }
            });
        }//监听动画结束，利用Handler通知Activity退出
    }

}
