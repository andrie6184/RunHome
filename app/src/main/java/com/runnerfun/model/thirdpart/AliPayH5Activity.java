package com.runnerfun.model.thirdpart;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;

import timber.log.Timber;

public class AliPayH5Activity extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = null;
        try {
            url = getIntent().getStringExtra("alipay_h5_url");
        } catch (Exception e) {
            finish();
            return;
        }
        if (TextUtils.isEmpty(url)) {
            // 测试H5支付，必须设置要打开的url网站
            Toast.makeText(this, "错误的URL", Toast.LENGTH_SHORT).show();
            finish();
        }
        super.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout layout = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout, params);

        mWebView = new WebView(getApplicationContext());
        params.weight = 1;
        mWebView.setVisibility(View.VISIBLE);
        layout.addView(mWebView, params);

        WebSettings settings = mWebView.getSettings();
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setSupportMultipleWindows(true);
        settings.setJavaScriptEnabled(true);
        settings.setSavePassword(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setMinimumFontSize(settings.getMinimumFontSize() + 8);
        settings.setAllowFileAccess(false);
        settings.setTextSize(WebSettings.TextSize.NORMAL);
        mWebView.setVerticalScrollbarOverlay(true);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            if (!(url.startsWith("http") || url.startsWith("https"))) {
                return true;
            }

            final PayTask task = new PayTask(AliPayH5Activity.this);
            final String ex = task.fetchOrderInfoFromH5PayUrl(url);
            if (!TextUtils.isEmpty(ex)) {
                Timber.d("paytask:::::" + url);
                new Thread(new Runnable() {
                    public void run() {
                        Timber.d("payTask:::" + ex);
                        final H5PayResultModel result = task.h5Pay(ex, true);
                        if (!TextUtils.isEmpty(result.getReturnUrl())) {
                            AliPayH5Activity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.loadUrl(result.getReturnUrl());
                                }
                            });
                        }
                    }
                }).start();
            } else {
                view.loadUrl(url);
            }
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.removeAllViews();
            try {
                mWebView.destroy();
            } catch (Throwable t) {
            }
            mWebView = null;
        }
    }

    public static void openAliPayH5Activity(Context context, String url) {
        Intent intent = new Intent(context, AliPayH5Activity.class);
        intent.putExtra("alipay_h5_url", url);
        context.startActivity(intent);
    }

}
