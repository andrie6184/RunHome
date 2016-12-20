package com.runnerfun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.UploadResult;
import com.runnerfun.beans.UserInfo;
import com.runnerfun.network.NetworkManager;
import com.runnerfun.tools.FileUtils;
import com.runnerfun.tools.RoundedTransformation;
import com.runnerfun.tools.UITools;
import com.runnerfun.widget.ImagePickerPopWindow;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

//import com.lzy.imagepicker.ui.ImageGridActivity;

/**
 * UserFragment
 * Created by andrie on 16/10/2016.
 */

public class UserFragment extends Fragment {

    private static final int REQUESTCODE_PICK = 0x1001;
    private static final int REQUESTCODE_TAKE = 0x1002;
    private static final int REQUESTCODE_CUTTING = 0x1003;

    private static final String IMAGE_FILE_NAME = "run_temp_avatar";

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

    @BindView(R.id.drawer_layout)
    View mDrawerLayout;

    private ImagePickerPopWindow menuWindow;
    private UserInfo mUserInfo;
    private String mTempAvatarUrl;
    private Uri mUriTempFile;

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

                mUserInfo = userInfo;

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

    @OnClick(R.id.user_avatar)
    void avatarClicked(final View view) {
        if (menuWindow == null) {
            menuWindow = new ImagePickerPopWindow(getActivity(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuWindow.dismiss();
                    switch (v.getId()) {
                        case R.id.takePhotoBtn:
                            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                                            IMAGE_FILE_NAME)));
                            startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                            break;
                        case R.id.pickPhotoBtn:
                            Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            startActivityForResult(pickIntent, REQUESTCODE_PICK);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        menuWindow.showAtLocation(mDrawerLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case REQUESTCODE_TAKE:
                File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                startPhotoZoom(Uri.fromFile(temp));
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        // intent.putExtra("return-data", true);
        mUriTempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriTempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    private void setPicToView(Intent picdata) {
        // Bundle extras = picdata.getExtras();
        Bitmap photo = null; //extras.getParcelable("data");
        try {
            photo = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(mUriTempFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (photo != null) {
            final String path = FileUtils.saveFile(getActivity(), "temphead.jpg", photo);
            mUserAvatar.setImageBitmap(null);
            RunApplication.getAppContex().picasso.invalidate("file://" + path);
            RunApplication.getAppContex().picasso.load("file://" + path).resize(500, 500)
                    .transform(new RoundedTransformation(360, 0)).placeholder(R.drawable.icon_avatar)
                    .error(R.drawable.icon_avatar).into(mUserAvatar);

            byte[] file = UITools.bmpToByteArray(BitmapFactory.decodeFile(path), true);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            mUserAvatar.setEnabled(false);
            mUserAvatar.setClickable(false);
            NetworkManager.instance.uploadAvatar("img", requestBody).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io()).flatMap(new Func1<ResponseBean<UploadResult>, Observable<?>>() {
                @Override
                public Observable<ResponseBean<Object>> call(ResponseBean<UploadResult> bean) {
                    mTempAvatarUrl = bean.getData().getImg();
                    return NetworkManager.instance.updateUserInfo(mUserInfo.getUser_name(),
                            Integer.valueOf(mUserInfo.getAge()), bean.getData().getImg(), mUserInfo.getRemarks(),
                            mUserInfo.getSex(), Integer.valueOf(mUserInfo.getHeight()), Integer.valueOf(mUserInfo.getWeight()))
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
                }
            }).subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    Toast.makeText(getActivity(), "头像上传成功", Toast.LENGTH_SHORT).show();

                    mUserInfo.setHeadimg(mTempAvatarUrl);
                    RunApplication.getAppContex().sharedPreferences.edit().putString(SP_KEY_USER_INFO,
                            new Gson().toJson(mUserInfo)).apply();

                    mUserAvatar.setEnabled(true);
                    mUserAvatar.setClickable(true);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    mUserAvatar.setEnabled(true);
                    mUserAvatar.setClickable(true);
                }
            });
        }
    }

    private class UserAvatarReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }

}
