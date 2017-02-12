package com.runnerfun;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.runnerfun.model.thirdpart.AliPayActivity;
import com.runnerfun.tools.CookieUtils;
import com.runnerfun.tools.ThirdpartAuthManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * ShopFragment
 * Created by andrie on 16/10/2016.
 */

public class ShopFragment extends Fragment {

    @BindView(R.id.web_view)
    WebView _webView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shop, container, false);
        ButterKnife.bind(this, v);
        initData();
        initListener();
        return v;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initData() {
        _webView.getSettings().setBuiltInZoomControls(false);
        _webView.getSettings().setLoadsImagesAutomatically(true);
        _webView.getSettings().setJavaScriptEnabled(true);

        _webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (url != null && url.startsWith("http://")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            }
        });

        _webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) {
                    return false;
                }
                if (url.startsWith("paohome://")) {
                    try {
                        String host = url.substring(10, url.indexOf("?"));
                        if (host.equalsIgnoreCase("weixinpay")) {
                            String payBill = url.substring(url.indexOf("paybill=") + 8);
                            ThirdpartAuthManager.instance().payByWeixin(URLDecoder.decode(payBill, "utf8"));
                        } else if (host.equalsIgnoreCase("payinfo")) {
                            String payInfo = url.substring(url.indexOf("?") + 1);
                            AliPayActivity.openAliPayActivity(getActivity(), payInfo);
                        }
                    } catch (Exception e) {
                        Timber.e(e.getMessage(), "parse pay url error");
                        return false;
                    }
                    return true;
                } else {
                    view.loadUrl(url);
                    return true;
                }
            }
        });

        String shopUrl = "http://mall.paobuzhijia.com/mobile?platform=android&ver=1.1.0";
        _webView.loadUrl(shopUrl);
        CookieUtils.synWebViewCookies(getActivity(), shopUrl, CookieUtils.getLocalCookies());
    }

    private void initListener() {

    }

}
