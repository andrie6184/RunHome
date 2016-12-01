package com.runnerfun.widget;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ValueBar;
import com.runnerfun.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lixiaoyang on 27/11/2016.
 */

public class ColorPickerDialog extends DialogFragment {
    public static interface ColorPickerListener{
        public void onSelect(int color);
    }

    @BindView(R.id.picker)
    ColorPicker mPicker;
    private ColorPickerListener listener = null;

    public void setColorListener(ColorPickerListener listener){
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.color_picker, container, false);
        ButterKnife.bind(this, v);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPicker.setShowOldCenterColor(false);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(listener != null){
            listener.onSelect(mPicker.getColor());
        }
    }
}
