package com.runnerfun;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.runnerfun.network.NetworkManager;
import com.runnerfun.widget.WheelView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

public class InitUserInfoActivity extends BaseActivity {

    private boolean mIsMale = true;

    @BindView(R.id.height)
    TextView height;
    @BindView(R.id.weight)
    TextView weight;
    @BindView(R.id.age)
    TextView age;

    @BindView(R.id.text_nan)
    TextView nan;
    @BindView(R.id.text_nv)
    TextView nv;

    private ArrayList<String> ageList;
    private ArrayList<String> heightList;
    private ArrayList<String> weightList;
    private boolean ageSet = false;
    private boolean heightSet = false;
    private boolean weightSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_user_info);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.skip_btn)
    void onSkipClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isFirstLogin", true);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.next_btn)
    void onNextClicked(final View view) {
        Toast.makeText(this, "数据提交中...", Toast.LENGTH_SHORT).show();
        view.setEnabled(false);
        view.setClickable(false);
        String gender = mIsMale ? "男" : "女";
        int ageValue = ageList.indexOf(age.getText().toString()) == -1 ? 0 : 1 + ageList.indexOf(age.getText().toString());
        int heightValue = heightList.indexOf(height.getText().toString()) == -1 ? 0 :
                50 + heightList.indexOf(height.getText().toString());
        int weightValue = weightList.indexOf(weight.getText().toString()) == -1 ? 0 :
                30 + weightList.indexOf(weight.getText().toString());
        NetworkManager.instance.updateUserInfo("name", ageValue, "", "", gender, heightValue, weightValue,
                new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        view.setEnabled(true);
                        view.setClickable(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(InitUserInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        view.setEnabled(true);
                        view.setClickable(true);
                    }

                    @Override
                    public void onNext(Object s) {
                        Toast.makeText(InitUserInfoActivity.this, "数据提交成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(InitUserInfoActivity.this, MainActivity.class);
                        intent.putExtra("isFirstLogin", true);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    @OnClick(R.id.text_nan)
    void onNanClicked(View view) {
        mIsMale = true;
        nan.setBackgroundResource(R.drawable.user_info_init_selected_bg);
        nv.setBackgroundResource(R.drawable.user_info_init_bg);
    }

    @OnClick(R.id.text_nv)
    void onNvClicked(View view) {
        mIsMale = false;
        nan.setBackgroundResource(R.drawable.user_info_init_bg);
        nv.setBackgroundResource(R.drawable.user_info_init_selected_bg);
    }

    @OnClick(R.id.age)
    void onAgeClicked(final View view) {
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
        wv.setSeletion(19);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                ageSet = true;
                age.setText(item);
                view.setBackgroundResource(R.drawable.user_info_init_selected_bg);
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("请设定年龄")
                .setView(outerView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!ageSet) {
                            age.setText(ageList.get(19));
                            view.setBackgroundResource(R.drawable.user_info_init_selected_bg);
                        }
                    }
                })
                .show();
    }

    @OnClick(R.id.height)
    void onHeightClicked(final View view) {
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
        wv.setSeletion(119);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                heightSet = true;
                height.setText(item);
                view.setBackgroundResource(R.drawable.user_info_init_selected_bg);
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("请设定身高")
                .setView(outerView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!heightSet) {
                            height.setText(heightList.get(119));
                            view.setBackgroundResource(R.drawable.user_info_init_selected_bg);
                        }
                    }
                })
                .show();
    }

    @OnClick(R.id.weight)
    void onWeightClicked(final View view) {
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
        wv.setSeletion(19);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                weightSet = true;
                weight.setText(item);
                view.setBackgroundResource(R.drawable.user_info_init_selected_bg);
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("请设定体重")
                .setView(outerView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!weightSet) {
                            weight.setText(weightList.get(19));
                            view.setBackgroundResource(R.drawable.user_info_init_selected_bg);
                        }
                    }
                })
                .show();
    }

}
