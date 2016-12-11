package com.runnerfun.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.runnerfun.R;
import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.ThirdLoginBean;
import com.runnerfun.network.NetworkManager;
import com.runnerfun.model.thirdpart.ThirdAccountModel;
import com.runnerfun.tools.ThirdpartAuthManager;
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

import static com.runnerfun.tools.ThirdpartAuthManager.ACTION_TAG_LOGIN;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    public static ThirdpartAuthManager.ThirdPartActionListener actionListener = null;
    public static int type = 0; // 0 is login, 1 is share

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_entry);
        ThirdpartAuthManager.instance().regToWxapp(this);
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
                ThirdAccountModel.instance.getWeixinToken(ThirdpartAuthManager.WEIXIN_APP_KEY,
                        ThirdpartAuthManager.WEIXIN_APP_SECRET, resp.code).flatMap(new Func1<String,
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
                                        ThirdpartAuthManager.TYPE_THIRD_WECHAT, "数据解析失败");
                            }
                        }
                        return null;
                    }
                }).flatMap(new Func1<String, Observable<ResponseBean<ThirdLoginBean>>>() {
                    @Override
                    public Observable<ResponseBean<ThirdLoginBean>> call(String info) {
                        try {
                            JSONObject userInfo = new JSONObject(info);
                            String openId = userInfo.optString("openid", "");
                            String name = userInfo.optString("nickname", "");
                            String avatar = userInfo.optString("headimgurl", "");
                            return NetworkManager.instance.loginWithThird(openId, "icon_qq", name, avatar);
                        } catch (JSONException e) {
                            if (actionListener != null) {
                                actionListener.onFailed(ACTION_TAG_LOGIN,
                                        ThirdpartAuthManager.TYPE_THIRD_WECHAT, "数据解析失败");
                            }
                        }
                        return null;
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseBean<ThirdLoginBean>>() {
                            @Override
                            public void call(ResponseBean<ThirdLoginBean> bean) {
                                if (bean.getCode() == 0) {
                                    NetworkManager.instance.setLoginInfo();
                                    if (actionListener != null) {
                                        actionListener.onSuccess(ACTION_TAG_LOGIN,
                                                ThirdpartAuthManager.TYPE_THIRD_QQ,
                                                bean.getData().isFirstlogin());
                                    }
                                } else {
                                    if (actionListener != null) {
                                        actionListener.onFailed(ACTION_TAG_LOGIN,
                                                ThirdpartAuthManager.TYPE_THIRD_QQ, bean.getMsg());
                                    }
                                }
                                finish();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                if (actionListener != null) {
                                    actionListener.onFailed(ACTION_TAG_LOGIN, ThirdpartAuthManager.TYPE_THIRD_QQ,
                                            throwable.getLocalizedMessage());
                                    finish();
                                }
                            }
                        });

            } else {
                if (null != actionListener) {
                    int action = ACTION_TAG_LOGIN;
                    if (type == 1) {
                        action = ThirdpartAuthManager.ACTION_TAG_SHARE;
                    }
                    actionListener.onFailed(action, ThirdpartAuthManager.TYPE_THIRD_WECHAT, resp.state);
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