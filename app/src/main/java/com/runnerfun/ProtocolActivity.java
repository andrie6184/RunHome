package com.runnerfun;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProtocolActivity extends BaseActivity {

    @BindView(R.id.protocol_text)
    TextView protocolText;

    private String content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocal);
        ButterKnife.bind(this);
        protocolText.setText(content);
    }

    @OnClick(R.id.cancel_btn)
    void onCancelClicked(View view) {
        finish();
    }

}
