package com.runnerfun;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.runnerfun.model.AccountModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

public class InitUserInfoActivity extends Activity {

    private boolean mIsMale = true;

    @BindView(R.id.height)
    EditText height;
    @BindView(R.id.weight)
    EditText weight;
    @BindView(R.id.age)
    EditText age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_user_info);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.skip_btn)
    void onSkipClicked(View view) {
        // TODO ???
    }

    @OnClick(R.id.next_btn)
    void onNextClicked(View view) {
        AccountModel.instance.updateUserInfo("name", Integer.parseInt(age.getText().toString()),
                "headimage", "remarks", "sexy", Integer.parseInt(height.getText().toString()),
                new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        // TODO goto what ?
                    }
                });
    }

    @OnClick(R.id.text_nan)
    void onNanClicked(View view) {
        mIsMale = true;
    }

    @OnClick(R.id.text_nv)
    void onNvClicked(View view) {
        mIsMale = false;
    }

}
