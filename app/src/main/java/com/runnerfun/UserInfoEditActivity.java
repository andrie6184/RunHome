package com.runnerfun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.runnerfun.model.AccountModel;
import com.runnerfun.widget.WheelView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserInfoEditActivity extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        nickName.setText(AccountModel.instance.getUserName());
        signature.setText(AccountModel.instance.getUserSignature());

        mDialogLayout = View.inflate(this, R.layout.layout_edittext_dialog, null);
        mDialogLayout1 = View.inflate(this, R.layout.layout_edittext_dialog, null);
        // and so on...
    }

    @OnClick(R.id.user_avatar)
    void avatarClicked(View view) {

    }

    @OnClick(R.id.nick_name)
    void nickNameClicked(View view) {
        if (nickDialog == null) {
            nickDialog = getEditDialog("编辑昵称", mDialogLayout, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EditText et = (EditText) mDialogLayout.findViewById(R.id.dialog_input);
                    nickName.setText(et.getText().toString());
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
                }
            }, null);
        }
        dismissAllDialog();
        signatureDialog.show();
    }

    @OnClick(R.id.gender)
    void genderClicked(View view) {
        if (genderDialog == null) {
            String[] array = {"男", "女"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请选择性别");
            builder.setItems(array, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO
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
            for (int i = 0; i < 100; i++) {
                ageList.add(i + " 岁");
            }
        }
        View outerView = LayoutInflater.from(this).inflate(R.layout.layout_wheel_dialog, null);
        WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        wv.setOffset(2);
        wv.setItems(ageList);
        wv.setSeletion(3);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                age.setText(item);
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
                heightList.add(i + " cm");
            }
        }
        View outerView = LayoutInflater.from(this).inflate(R.layout.layout_wheel_dialog, null);
        WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        wv.setOffset(2);
        wv.setItems(heightList);
        wv.setSeletion(3);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                height.setText(item);
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
                weightList.add(i + " kg");
            }
        }
        View outerView = LayoutInflater.from(this).inflate(R.layout.layout_wheel_dialog, null);
        WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        wv.setOffset(2);
        wv.setItems(weightList);
        wv.setSeletion(3);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                weight.setText(item);
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
    void commitClicked(View view) {

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
