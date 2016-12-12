package com.runnerfun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareTargetActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_target);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.close_btn)
    void onCloseClicked(View view) {
        finish();
    }

    @OnClick(R.id.share_shuiyin)
    void onShuiyinClicked(View view) {
        startActivity(new Intent(this, ShareActivity.class));
    }

    @OnClick(R.id.share_pyq)
    void onSharePYQClicked(View view) {

    }

    @OnClick(R.id.share_weixin)
    void onShareWeixinClicked(View view) {

    }

    @OnClick(R.id.share_qq)
    void onShareQQClicked(View view) {

    }

    @OnClick(R.id.share_xinlang)
    void onShareSinaClicked(View view) {

    }

}
