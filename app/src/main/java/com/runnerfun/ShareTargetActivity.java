package com.runnerfun;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.runnerfun.tools.ThirdpartAuthManager;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareTargetActivity extends BaseActivity implements IWeiboHandler.Response {

    private String shareUrl = "https://m.paobuzhijia.com/share.php?rid=";
    private String shareTitle = "我用跑步之家奔跑了%s公里";
    private String shareText = "用时:%s \r\n 配速:%s \r\n 本次获得的里币:%s";

    private String distance;
    private String speed;
    private String time;

    public static void startWithShareData(Context context, String distance, String speed, String time) {
        Intent i = new Intent(context, ShareTargetActivity.class);
        i.putExtra("intent_param_distance", distance);
        i.putExtra("intent_param_speed", speed);
        i.putExtra("intent_param_time", time);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_target);
        ButterKnife.bind(this);

        distance = getIntent().getStringExtra("intent_param_distance");
        speed = getIntent().getStringExtra("intent_param_speed");
        time = getIntent().getStringExtra("intent_param_time");
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
        ThirdpartAuthManager.instance().startWeixinShare(this, true, getShareUrl(), getShareTitle(),
                getShareText());
    }

    @OnClick(R.id.share_weixin)
    void onShareWeixinClicked(View view) {
        ThirdpartAuthManager.instance().startWeixinShare(this, false, getShareUrl(), getShareTitle(),
                getShareText());
    }

    @OnClick(R.id.share_qq)
    void onShareQQClicked(View view) {
        ThirdpartAuthManager.instance().startQQShare(this, mQqShareListener, getShareUrl(),
                getShareTitle(), getShareText());
    }

    @OnClick(R.id.share_xinlang)
    void onShareSinaClicked(View view) {
        IWeiboShareAPI mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, ThirdpartAuthManager.WEIBO_APP_KEY);
        mWeiboShareAPI.registerApp();
        mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        ThirdpartAuthManager.instance().startWeiboShare(mWeiboShareAPI, this, getShareUrl(),
                getShareTitle(), getShareText());
    }

    private String getShareUrl() {
        return shareUrl + ThirdpartAuthManager.getLastRidForShare();
    }

    private String getShareTitle() {
        return String.format(Locale.getDefault(), shareTitle, distance);
    }

    private String getShareText() {
        return String.format(Locale.getDefault(), shareText, time, speed,
                ThirdpartAuthManager.getLastCoinForShare());
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case com.sina.weibo.sdk.constant.WBConstants.ErrorCode.ERR_OK:
                Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
                break;
            case com.sina.weibo.sdk.constant.WBConstants.ErrorCode.ERR_FAIL:
                Toast.makeText(this, baseResponse.errMsg + "分享失败，请关闭页面后重试", Toast.LENGTH_LONG).show();
                break;
            case com.sina.weibo.sdk.constant.WBConstants.ErrorCode.ERR_CANCEL:
                Toast.makeText(this, "取消分享", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            if (resultCode == Constants.ACTIVITY_OK) {
                Tencent.handleResultData(data, mQqShareListener);
            }
        }
    }

    private IUiListener mQqShareListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            Toast.makeText(ShareTargetActivity.this, "分享成功", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(ShareTargetActivity.this, uiError.errorMessage + "分享失败", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(ShareTargetActivity.this, "取消分享", Toast.LENGTH_LONG).show();
        }
    };

}
