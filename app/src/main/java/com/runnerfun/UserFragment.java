package com.runnerfun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.runnerfun.beans.UserInfo;
import com.runnerfun.network.NetworkManager;
import com.runnerfun.tools.RoundedTransformation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

//import com.lzy.imagepicker.ui.ImageGridActivity;

/**
 * UserFragment
 * Created by andrie on 16/10/2016.
 */

public class UserFragment extends Fragment {

    public static final String USER_INFO_CHANGED_ACTION = "USER_INFO_CHANGED_ACTION";
    public static final String USER_INFO_RELOADED_ACTION = "USER_INFO_RELOADED_ACTION";
    public static final String SP_KEY_USER_INFO = "SP_KEY_USER_INFO";

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

    private LocalBroadcastManager mLocalManager;
    private UserAvatarReceiver mReceiver;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, v);

        mLocalManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter filter = new IntentFilter(USER_INFO_CHANGED_ACTION);
        mReceiver = new UserAvatarReceiver();
        mLocalManager.registerReceiver(mReceiver, filter);

        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/dincond.otf");
        Typeface boldTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/dincond-bold.otf");
        mUserCoin.setTypeface(typeFace);
        mUserLength.setTypeface(boldTypeFace);

        initData();

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalManager.unregisterReceiver(mReceiver);
    }

    private void initData() {
        NetworkManager.instance.getUserInfo(new Subscriber<UserInfo>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(UserInfo userInfo) {
                if (!TextUtils.isEmpty(userInfo.getHeadimg())) {
                    RunApplication.getAppContex().picasso.load(userInfo.getHeadimg())
                            .transform(new RoundedTransformation(360, 0)).placeholder(R.drawable.icon_avatar)
                            .error(R.drawable.icon_avatar).into(mUserAvatar);
                }
                mUserName.setText(userInfo.getUser_name());
                mUserSign.setText(userInfo.getRemarks());
                mUserCoin.setText(userInfo.getTotal_score());
                mUserLength.setText(userInfo.getTotal_mileage() + "Km");

                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(
                        new Intent(UserFragment.USER_INFO_RELOADED_ACTION));

                RunApplication.getAppContex().sharedPreferences.edit().putString(SP_KEY_USER_INFO,
                        new Gson().toJson(userInfo)).apply();
            }
        });
    }

    @OnClick(R.id.record)
    void onClickRecord(View view) {
        startActivity(new Intent(getActivity(), RecordDetailActivity.class));
    }

    @OnClick(R.id.detail)
    void onClickDetail(View view) {
        startActivity(new Intent(getActivity(), CoinDetailActivity.class));
    }

    @OnClick(R.id.rule)
    void onClickRule(View view) {
        String url = "https://api.paobuzhijia.com/rules";
        CommonWebActivity.openCommonWebActivity(getActivity(), url);
    }

    @OnClick(R.id.icon_setting)
    void onSettingClicked(View view) {
        startActivity(new Intent(getActivity(), UserSettingActivity.class));
    }

    private class UserAvatarReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }

}
