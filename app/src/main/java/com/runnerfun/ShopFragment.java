package com.runnerfun;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.runnerfun.tools.CookieUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        _webView.getSettings().setJavaScriptEnabled(true);
        _webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        String shopUrl = "http://mall.paobuzhijia.com/mobile?platform=android&ver=1.0.0";
        _webView.loadUrl(shopUrl);
        CookieUtils.synWebViewCookies(getActivity(), shopUrl, CookieUtils.getLocalCookies());
    }

    private void initListener() {

    }

}
