package com.runnerfun;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by lixiaoyang on 16/10/2016.
 */

public class UserFragment extends Fragment {

    @BindView(R.id.user_avatar)
    ImageView mUserAvatar;
    @BindView(R.id.user_name)
    TextView mUserName;
    @BindView(R.id.user_sign)
    TextView mUserSign;

    @BindView(R.id.user_record)
    TextView mUserCoin;
    @BindView(R.id.user_length)
    TextView mUserLength;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.record)
    void onClickRecord() {
        // TODO
    }

    @OnClick(R.id.detail)
    void onClickDetail() {
        // TODO
    }

    @OnClick(R.id.rule)
    void onClickRule() {
        // TODO
    }

}
