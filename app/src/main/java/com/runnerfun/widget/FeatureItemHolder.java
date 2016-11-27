package com.runnerfun.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.runnerfun.R;


/**
 * Created by lixiaoyang on 27/11/2016.
 */

public class FeatureItemHolder extends RecyclerView.ViewHolder {
    private CheckBox mImageView;
    private TextView mTextView;
    private CheckBox mCheckbox;

    public FeatureItemHolder(View itemView) {
        super(itemView);
       // mCheckbox = (CheckBox)itemView;
        mImageView = (CheckBox)itemView.findViewById(R.id.image);
        mTextView = (TextView)itemView.findViewById(R.id.text);
    }

    public void setTitle(String title){
        mTextView.setText(title);
      //  mCheckbox.setText(title);
    }

    public void setIcon(int iconId){
        mImageView.setButtonDrawable(iconId);
//        mCheckbox.setButtonDrawable(iconId);
    }

}
