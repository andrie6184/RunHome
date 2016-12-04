package com.runnerfun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.runnerfun.beans.UploadResult;
import com.runnerfun.beans.UserInfo;
import com.runnerfun.network.NetworkManager;
import com.runnerfun.tools.FileUtils;
import com.runnerfun.tools.RoundedTransformation;
import com.runnerfun.tools.UITools;
import com.runnerfun.widget.ImagePickerPopWindow;
import com.runnerfun.widget.WheelView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;

public class UserInfoEditActivity extends Activity {

    private static final int REQUESTCODE_PICK = 0x1001;
    private static final int REQUESTCODE_TAKE = 0x1002;
    private static final int REQUESTCODE_CUTTING = 0x1003;

    private static final String IMAGE_FILE_NAME = "run_temp_avatar";

    @BindView(R.id.avatar_image)
    ImageView avatar;
    @BindView(R.id.nick_name_text)
    TextView nickName;
    @BindView(R.id.signature_text)
    TextView signature;
    @BindView(R.id.gender_text)
    TextView gender;
    @BindView(R.id.age_text)
    TextView age;
    @BindView(R.id.height_text)
    TextView height;
    @BindView(R.id.weight_text)
    TextView weight;

    private AlertDialog nickDialog;
    private AlertDialog signatureDialog;
    private AlertDialog genderDialog;

    private View mDialogLayout;
    private View mDialogLayout1;

    private ArrayList<String> ageList;
    private ArrayList<String> heightList;
    private ArrayList<String> weightList;

    private ImagePickerPopWindow menuWindow;

    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            String path = FileUtils.saveFile(this, "temphead.jpg", photo);
            RunApplication.getAppContex().picasso.load("file://" + path)
                    .transform(new RoundedTransformation(360, 0)).placeholder(R.drawable.icon_avatar)
                    .error(R.drawable.icon_avatar).into(avatar);

            byte[] file = UITools.bmpToByteArray(BitmapFactory.decodeFile(path), true);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            NetworkManager.instance.uploadAvatar("img", requestBody, new Subscriber<UploadResult>() {
                @Override
                public void onCompleted() {
                    findViewById(R.id.user_avatar).setEnabled(true);
                    findViewById(R.id.user_avatar).setClickable(true);
                }

                @Override
                public void onError(Throwable e) {
                    findViewById(R.id.user_avatar).setEnabled(true);
                    findViewById(R.id.user_avatar).setClickable(true);
                    Toast.makeText(UserInfoEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(UploadResult uploadResult) {
                    userInfo.setHeadimg(uploadResult.getImg());
                }
            });
        }
    }

    private void init() {
        userInfo = new Gson().fromJson(RunApplication.getAppContex().sharedPreferences
                .getString(UserFragment.SP_KEY_USER_INFO, ""), UserInfo.class);

        if (!TextUtils.isEmpty(userInfo.getHeadimg())) {
            RunApplication.getAppContex().picasso.load(userInfo.getHeadimg())
                    .transform(new RoundedTransformation(360, 0)).placeholder(R.drawable.icon_avatar)
                    .error(R.drawable.icon_avatar).into(avatar);
        }
        nickName.setText(userInfo.getUser_name());
        signature.setText(userInfo.getRemarks());
        gender.setText(userInfo.getSex());
        age.setText(userInfo.getAge() + " 岁");
        height.setText(userInfo.getHeight() + "厘米");
        weight.setText(userInfo.getWeight() + "公斤");

        mDialogLayout = View.inflate(this, R.layout.layout_edittext_dialog, null);
        ((EditText) mDialogLayout.findViewById(R.id.dialog_input)).setText(userInfo.getUser_name());
        mDialogLayout1 = View.inflate(this, R.layout.layout_edittext_dialog, null);
        ((EditText) mDialogLayout1.findViewById(R.id.dialog_input)).setText(userInfo.getRemarks());
    }

    @OnClick(R.id.user_avatar)
    void avatarClicked(View view) {
        view.setEnabled(false);
        view.setClickable(false);
        if (menuWindow == null) {
            menuWindow = new ImagePickerPopWindow(this, new View.OnClickListener() {
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
        menuWindow.showAtLocation(findViewById(R.id.drawer_layout),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @OnClick(R.id.nick_name)
    void nickNameClicked(View view) {
        if (nickDialog == null) {
            nickDialog = getEditDialog("编辑昵称", mDialogLayout, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EditText et = (EditText) mDialogLayout.findViewById(R.id.dialog_input);
                    nickName.setText(et.getText().toString());
                    userInfo.setUser_name(et.getText().toString());
                }
            }, null);
        }
        dismissAllDialog();
        nickDialog.show();
    }

    @OnClick(R.id.personal_signature)
    void signatureClicked(View view) {
        if (signatureDialog == null) {
            signatureDialog = getEditDialog("编辑跑步宣言", mDialogLayout1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EditText et = (EditText) mDialogLayout1.findViewById(R.id.dialog_input);
                    signature.setText(et.getText().toString());
                    userInfo.setRemarks(et.getText().toString());
                }
            }, null);
        }
        dismissAllDialog();
        signatureDialog.show();
    }

    @OnClick(R.id.gender)
    void genderClicked(View view) {
        if (genderDialog == null) {
            final String[] array = {"男", "女"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请选择性别");
            builder.setItems(array, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    gender.setText(array[which]);
                    userInfo.setSex(array[which]);
                }
            });
            genderDialog = builder.create();
        }
        dismissAllDialog();
        genderDialog.show();
    }

    @OnClick(R.id.age)
    void ageClicked(View view) {
        if (ageList == null) {
            ageList = new ArrayList<>();
            for (int i = 1; i < 100; i++) {
                ageList.add(i + " 岁");
            }
        }
        View outerView = LayoutInflater.from(this).inflate(R.layout.layout_wheel_dialog, null);
        WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        wv.setOffset(2);
        wv.setItems(ageList);
        int selection = ageList.indexOf(userInfo.getAge() + " 岁");
        wv.setSeletion(selection);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                age.setText(item);
                userInfo.setAge(String.valueOf(ageList.indexOf(item) + 1));
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("请设定年龄")
                .setView(outerView)
                .setPositiveButton("确定", null)
                .show();
    }

    @OnClick(R.id.height)
    void heightClicked(View view) {
        if (heightList == null) {
            heightList = new ArrayList<>();
            for (int i = 50; i < 251; i++) {
                heightList.add(i + " 厘米");
            }
        }
        View outerView = LayoutInflater.from(this).inflate(R.layout.layout_wheel_dialog, null);
        WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        wv.setOffset(2);
        wv.setItems(heightList);
        int selection = heightList.indexOf(userInfo.getHeight() + " 厘米");
        wv.setSeletion(selection);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                height.setText(item);
                userInfo.setHeight(String.valueOf(heightList.indexOf(item) + 50));
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("请设定身高")
                .setView(outerView)
                .setPositiveButton("确定", null)
                .show();
    }

    @OnClick(R.id.weight)
    void weightClicked(View view) {
        if (weightList == null) {
            weightList = new ArrayList<>();
            for (int i = 30; i < 200; i++) {
                weightList.add(i + " 公斤");
            }
        }
        View outerView = LayoutInflater.from(this).inflate(R.layout.layout_wheel_dialog, null);
        WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        wv.setOffset(2);
        wv.setItems(weightList);
        int selection = weightList.indexOf(userInfo.getWeight() + " 公斤");
        wv.setSeletion(selection);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                weight.setText(item);
                userInfo.setWeight(String.valueOf(weightList.indexOf(item) + 30));
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("请设定体重")
                .setView(outerView)
                .setPositiveButton("确定", null)
                .show();
    }

    @OnClick(R.id.cancel_btn)
    void onReturnClicked(View view) {
        finish();
    }

    @OnClick(R.id.save_btn)
    void commitClicked(final View view) {
        view.setEnabled(false);
        view.setClickable(false);
        Toast.makeText(UserInfoEditActivity.this, "提交修改中...", Toast.LENGTH_SHORT).show();
        NetworkManager.instance.updateUserInfo(userInfo.getUser_name(), Integer.parseInt(userInfo.getAge()),
                userInfo.getHeadimg(), userInfo.getRemarks(), userInfo.getSex(),
                Integer.parseInt(userInfo.getHeight()), Integer.parseInt(userInfo.getWeight()),
                new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        view.setEnabled(true);
                        view.setClickable(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.setEnabled(true);
                        view.setClickable(true);
                        Toast.makeText(UserInfoEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Object s) {
                        Toast.makeText(UserInfoEditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        LocalBroadcastManager.getInstance(UserInfoEditActivity.this)
                                .sendBroadcast(new Intent(UserFragment.USER_INFO_CHANGED_ACTION));
                    }
                });
    }

    private AlertDialog getEditDialog(String title, View view, DialogInterface.OnClickListener okListener,
                                      DialogInterface.OnClickListener cancelListener) {
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", cancelListener).create();
    }

    private void dismissAllDialog() {
        if (nickDialog != null && nickDialog.isShowing()) {
            nickDialog.dismiss();
        }
        if (signatureDialog != null && signatureDialog.isShowing()) {
            signatureDialog.dismiss();
        }
        if (genderDialog != null && genderDialog.isShowing()) {
            genderDialog.dismiss();
        }
    }

}
