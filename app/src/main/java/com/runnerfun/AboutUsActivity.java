package com.runnerfun;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends BaseActivity {

    private String content = "跑步之家APP是跑友自主研发，针对跑步人群的刚性需求，提供线上的服务平台。目前为初始版本，功能不断完善中，非常欢迎您提出宝贵意见让我们不断进步。\n" +
            "\n" +
            "邮箱：paobuzhijia@126.com\n" +
            "\n" +
            "联系跑步之家QQ：2991467476\n" +
            "\n" +
            "微信公众号：跑步之家 / paobuzhijia";

    @BindView(R.id.about_text)
    TextView aboutText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        aboutText.setText(content);
        aboutText.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @OnClick(R.id.cancel_btn)
    void onCancelClicked(View view) {
        finish();
    }

}
