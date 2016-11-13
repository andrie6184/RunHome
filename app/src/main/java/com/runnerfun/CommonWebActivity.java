package com.runnerfun;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.runnerfun.widget.ProgressWebView;

public class CommonWebActivity extends BaseActivity {

    private TextView mTitle;
    private ProgressWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_web);

        String url = getIntent().getStringExtra("target_url");

        mWebView = (ProgressWebView) findViewById(R.id.common_webview);
        mTitle = (TextView) findViewById(R.id.web_view_title);

        findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.refresh_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.reload();
            }
        });

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (url != null && url.startsWith("http://"))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.mListener = new ProgressWebView.onReceiveTitleListener() {
            @Override
            public void onReceiveTitle(String title) {
                mTitle.setText(title);
            }
        };

        mWebView.loadUrl(url);
    }

    public static void openCommonWebActivity(Context context, String url) {
        Intent intent = new Intent(context, CommonWebActivity.class);
        intent.putExtra("target_url", url);
        context.startActivity(intent);
    }

}
