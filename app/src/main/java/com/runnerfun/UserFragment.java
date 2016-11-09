package com.runnerfun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ui.ImageGridActivity;
import com.runnerfun.beans.UploadResult;
import com.runnerfun.model.AccountModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import timber.log.Timber;


/**
 * UserFragment
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

    private LocalBroadcastManager mLocalManager;
    private UserAvatarReceiver mReceiver;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, v);

        mLocalManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter filter = new IntentFilter(MainActivity.SELECTED_USER_AVATAR_ACTION);
        mReceiver = new UserAvatarReceiver();
        mLocalManager.registerReceiver(mReceiver, filter);

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalManager.unregisterReceiver(mReceiver);
    }

    @OnClick(R.id.record)
    void onClickRecord() {
        startActivity(new Intent(getActivity(), RecordDetailActivity.class));
    }

    @OnClick(R.id.detail)
    void onClickDetail() {
        startActivity(new Intent(getActivity(), CoinDetailActivity.class));
    }

    @OnClick(R.id.rule)
    void onClickRule() {
        // TODO
    }

    @OnClick(R.id.user_avatar)
    void onClickAvatar() {
        Intent intent = new Intent(getActivity(), ImageGridActivity.class);
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_IMAGE_PICKER);
    }

    protected void uploadAvatar(byte[] buffer) {
        AccountModel.instance.uploadAvatar(buffer, new Subscriber<UploadResult>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "头像上传失败,请稍后再试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(UploadResult result) {
                String rrr = result.getImg();
                Toast.makeText(getActivity(), "头像上传成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class UserAvatarReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String imagePath = intent.getStringExtra(MainActivity.INTENT_PARAMS_USER_AVATAR_PATH);
            File file = new File(imagePath);
            RunApplication.getAppContex().picasso.load(file).into(mUserAvatar);

            byte[] buffer = null;
            FileInputStream fis = null;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                fis = new FileInputStream(file);
                byte[] b = new byte[1024];
                int n;
                while ((n = fis.read(b)) != -1) {
                    bos.write(b, 0, n);
                }
                buffer = bos.toByteArray();
                uploadAvatar(buffer);
            } catch (IOException e) {
                e.printStackTrace();
                Timber.e(e, "upload avatar error");
            }
        }
    }

}
