package com.runnerfun.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.runnerfun.R;
import com.runnerfun.beans.LoginBean;
import com.runnerfun.beans.ResponseBean;
import com.runnerfun.model.AccountModel;
import com.runnerfun.model.thirdpart.ThirdAccountModel;
import com.runnerfun.tools.ThirdPartAuthManager;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.runnerfun.tools.ThirdPartAuthManager.ACTION_TAG_LOGIN;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    public static ThirdPartAuthManager.ThirdPartActionListener actionListener = null;
    public static int type = 0; // 0 is login, 1 is share

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_entry);
        ThirdPartAuthManager.regToWxapp(this);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        final SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
        if (resp.code != null) {
            if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                ThirdAccountModel.instance.getWeixinToken(ThirdPartAuthManager.WEIXIN_APP_KEY,
                        ThirdPartAuthManager.WEIXIN_APP_SECRET, resp.code).flatMap(new Func1<String,
                        Observable<String>>() {
                    @Override
                    public Observable<String> call(String tokenString) {
                        try {
                            JSONObject tokenInfo = new JSONObject(tokenString);
                            String token = tokenInfo.optString("access_token", "");
                            String openId = tokenInfo.optString("access_token", "");
                            return ThirdAccountModel.instance.getWeixinUserInfo(token, openId);
                        } catch (JSONException e) {
                            if (actionListener != null) {
                                actionListener.onFailed(ACTION_TAG_LOGIN,
                                        ThirdPartAuthManager.TYPE_THIRD_WECHAT, "数据解析失败");
                            }
                        }
                        return null;
                    }
                }).flatMap(new Func1<String, Observable<ResponseBean<LoginBean>>>() {
                    @Override
                    public Observable<ResponseBean<LoginBean>> call(String info) {
                        try {
                            JSONObject userInfo = new JSONObject(info);
                            String openId = userInfo.optString("openid", "");
                            String name = userInfo.optString("nickname", "");
                            String avatar = userInfo.optString("headimgurl", "");
                            return AccountModel.instance.loginWithThird(openId, "qq", name, avatar);
                        } catch (JSONException e) {
                            if (actionListener != null) {
                                actionListener.onFailed(ACTION_TAG_LOGIN,
                                        ThirdPartAuthManager.TYPE_THIRD_WECHAT, "数据解析失败");
                            }
                        }
                        return null;
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseBean<LoginBean>>() {
                            @Override
                            public void call(ResponseBean<LoginBean> bean) {
                                if (bean.getCode() == 0) {
                                    if (actionListener != null) {
                                        actionListener.onSuccess(ACTION_TAG_LOGIN, ThirdPartAuthManager.TYPE_THIRD_QQ);
                                    }
                                } else {
                                    if (actionListener != null) {
                                        actionListener.onFailed(ACTION_TAG_LOGIN, ThirdPartAuthManager.TYPE_THIRD_QQ,
                                                bean.getMsg());
                                    }
                                }
                                finish();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                if (actionListener != null) {
                                    actionListener.onFailed(ACTION_TAG_LOGIN, ThirdPartAuthManager.TYPE_THIRD_QQ,
                                            throwable.getLocalizedMessage());
                                    finish();
                                }
                            }
                        });

            } else {
                if (null != actionListener) {
                    int action = ACTION_TAG_LOGIN;
                    if (type == 1) {
                        action = ThirdPartAuthManager.ACTION_TAG_SHARE;
                    }
                    actionListener.onFailed(action, ThirdPartAuthManager.TYPE_THIRD_WECHAT, resp.state);
                    actionListener = null;
                }
                finish();
            }
        } else {
            String text = null;
            if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                if (type == 1) {
                    text = "分享成功";
                }
            } else if (resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                if (type == 1) {
                    text = "分享取消";
                } else {
                    text = "登录失败";
                }
            } else {
                if (type == 1) {
                    text = "分享失败";
                } else if (type == 0) {
                    text = "登录失败";
                }
            }
            if (text != null) {
                Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp resp) {
    }

}