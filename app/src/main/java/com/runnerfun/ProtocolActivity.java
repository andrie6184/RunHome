package com.runnerfun;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProtocolActivity extends BaseActivity {

    @BindView(R.id.protocol_text)
    TextView protocolText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocal);
        ButterKnife.bind(this);

        InputStream is = null;
        try {
            is = getAssets().open("protocol");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);

            String content = new String(buffer, "utf-8");
            protocolText.setText(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }

        protocolText.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @OnClick(R.id.cancel_btn)
    void onCancelClicked(View view) {
        finish();
    }

}
