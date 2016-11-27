package com.runnerfun.tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.runnerfun.RunApplication;
import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.ThirdLoginBean;
import com.runnerfun.model.AccountModel;
import com.runnerfun.model.thirdpart.ThirdAccountModel;
import com.runnerfun.wxapi.WXEntryActivity;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * ThirdPartAuthManager
 * Created by andrie on 11.06.15.
 */
public class ThirdPartAuthManager {

    public interface ThirdPartActionListener {
        void onSuccess(int action, int type, boolean isFirst);

        void onFailed(int action, int type, String result);

        void onCanceled(int action, int type);
    }

    public static final int ACTION_TAG_LOGIN = 1001;
    public static final int ACTION_TAG_SHARE = 1002;

    public static final int TYPE_THIRD_WEIBO = 101;
    public static final int TYPE_THIRD_WECHAT = 102;
    public static final int TYPE_THIRD_QQ = 103;

    public static final String WEIXIN_APP_KEY = "wx69b810c173e5cb5e";
    public static final String WEIXIN_APP_SECRET = "wx69b810c173e5cb5e";
    private static final String WEIBO_APP_KEY = "3492357311";
    private static final String QQ_APP_KEY = "1105728053";

    private static final String WEIBO_REDIRECT_URL = "http://www.kanpanbao.com/";
    private static final String WEIBO_ACCESS_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "invitation_write";

    private static IWXAPI iwxapi;
    private static Tencent mTencent;

    private static ThirdPartAuthManager instance = new ThirdPartAuthManager();

    private ThirdPartAuthManager() {
        mTencent = Tencent.createInstance(QQ_APP_KEY, RunApplication.getAppContex());
        iwxapi = WXAPIFactory.createWXAPI(RunApplication.getAppContex(), WEIXIN_APP_KEY);
    }

    public static ThirdPartAuthManager instance() {
        return instance;
    }

    public void startQQLogin(final Activity activity, final ThirdPartActionListener listener) {
        mTencent = Tencent.createInstance(QQ_APP_KEY, activity.getApplicationContext());
        IUiListener mTencentListener = new IUiListener() {
            @Override
            public void onComplete(final Object response) {
                final String id = ((JSONObject) response).optString("openid", "");
                final UserInfo mInfo = new UserInfo(activity, mTencent.getQQToken());
                mInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        JSONObject info = (JSONObject) response;
                        AccountModel.instance.loginWithThird(id, "qq", info.optString("nickname", ""),
                                info.optString("figureurl_qq_2", ""), new Subscriber<ThirdLoginBean>() {
                                    @Override
                                    public void onCompleted() {
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        if (listener != null) {
                                            listener.onFailed(ACTION_TAG_LOGIN, TYPE_THIRD_QQ,
                                                    e.getLocalizedMessage());
                                        }
                                    }

                                    @Override
                                    public void onNext(ThirdLoginBean loginBean) {
                                        if (listener != null) {
                                            listener.onSuccess(ACTION_TAG_LOGIN, TYPE_THIRD_QQ,
                                                    loginBean.isFirstlogin());
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onError(UiError uiError) {
                        if (listener != null) {
                            listener.onFailed(ACTION_TAG_LOGIN, TYPE_THIRD_QQ, uiError.errorMessage);
                        }
                    }

                    @Override
                    public void onCancel() {
                        if (listener != null) {
                            listener.onCanceled(ACTION_TAG_LOGIN, TYPE_THIRD_QQ);
                        }
                    }
                });
            }

            @Override
            public void onError(UiError uiError) {
                if (listener != null) {
                    listener.onFailed(ACTION_TAG_LOGIN, TYPE_THIRD_QQ, uiError.errorMessage);
                }
            }

            @Override
            public void onCancel() {
                if (listener != null) {
                    listener.onCanceled(ACTION_TAG_LOGIN, TYPE_THIRD_QQ);
                }
            }
        };
        if (!mTencent.isSessionValid()) {
            mTencent.login(activity, Constants.LOGIN_INFO, mTencentListener);
        }
    }

    public void startQQShare(Activity context, Tencent mTencent, IUiListener mListener, String imageUrl) {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, ApplicationUtils.getAppName());
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        mTencent.shareToQQ(context, params, mListener);
    }

    public void startWeiboLogin(final Activity activity, final ThirdPartActionListener listener) {
        AuthInfo authInfo = new AuthInfo(activity, WEIBO_APP_KEY,
                WEIBO_REDIRECT_URL, WEIBO_ACCESS_SCOPE);
        SsoHandler ssoHandler = new SsoHandler(activity, authInfo);
        ssoHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                Timber.d("ThirdPartAuthManager", "weibo done");
                final Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeAccessToken(activity, mAccessToken);
                if (mAccessToken.isSessionValid()) {
                    AccessTokenKeeper.writeAccessToken(activity, mAccessToken);
                    ThirdAccountModel.instance.getWeiboUserInfo(mAccessToken.getToken(),
                            mAccessToken.getUid()).flatMap(new Func1<String, Observable<ResponseBean<ThirdLoginBean>>>() {
                        @Override
                        public Observable<ResponseBean<ThirdLoginBean>> call(String info) {
                            try {
                                JSONObject userInfo = new JSONObject(info);
                                String name = userInfo.optString("name", "");
                                String image = userInfo.optString("profile_image_url", "");
                                return AccountModel.instance.loginWithThird(mAccessToken.getUid(), "qq", name, image);
                            } catch (JSONException e) {
                                if (listener != null) {
                                    listener.onFailed(ACTION_TAG_LOGIN, TYPE_THIRD_QQ, "数据解析失败");
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
                                        if (listener != null) {
                                            listener.onSuccess(ACTION_TAG_LOGIN, TYPE_THIRD_QQ,
                                                    bean.getData().isFirstlogin());
                                        }
                                    } else {
                                        if (listener != null) {
                                            listener.onFailed(ACTION_TAG_LOGIN, TYPE_THIRD_QQ,
                                                    bean.getMsg());
                                        }
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    if (listener != null) {
                                        listener.onFailed(ACTION_TAG_LOGIN, TYPE_THIRD_QQ,
                                                throwable.getLocalizedMessage());
                                    }
                                }
                            });
                } else {
                    if (null != listener) {
                        listener.onFailed(ACTION_TAG_LOGIN, TYPE_THIRD_WEIBO, "Token 验证失败, 登录失败");
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                Timber.e("ThirdPartAuthManager", "weibo failed");
                if (null != listener) {
                    listener.onFailed(ACTION_TAG_LOGIN, TYPE_THIRD_WEIBO, e.getLocalizedMessage());
                }
            }

            @Override
            public void onCancel() {
                Timber.d("ThirdPartAuthManager", "weibo cancel");
                if (null != listener) {
                    listener.onCanceled(ACTION_TAG_LOGIN, TYPE_THIRD_WEIBO);
                }
            }
        });
    }

    public void startWeiboShare(IWeiboShareAPI mWeiboShareAPI, final Activity context, Bitmap image) {
        // 获取微博客户端相关信息，如是否安装、支持 SDK 的版本
        boolean isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();

        if (isInstalledWeibo) {
            WeiboMessage weiboMessage = new WeiboMessage();
            ImageObject imageObject = new ImageObject();
            imageObject.setImageObject(image);
            weiboMessage.mediaObject = imageObject;

            SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
            request.transaction = String.valueOf(System.currentTimeMillis());
            request.message = weiboMessage;

            mWeiboShareAPI.sendRequest(context, request);
        } else {
            Toast.makeText(context, "分享请先安装微博客户端", Toast.LENGTH_SHORT).show();
        }
    }

    public void startWeixinLogin(Activity activity, ThirdPartActionListener listener) {
        if (!iwxapi.isWXAppInstalled()) {
            Toast.makeText(activity, "分享请先安装微信客户端", Toast.LENGTH_SHORT).show();
        } else {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "kanpanbao_social_login";
            WXEntryActivity.actionListener = listener;
            WXEntryActivity.type = 0;
            iwxapi.sendReq(req);
        }
    }

    public void startWeixinShare(Activity context, boolean isTimeLine, String imagePath) {
        if (iwxapi.isWXAppInstalled() && iwxapi.isWXAppInstalled()) {
            WXImageObject imgObj = new WXImageObject();
            imgObj.setImagePath(imagePath);

            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imgObj;
            // msg.description="图片描述";

            Bitmap bmp = BitmapFactory.decodeFile(imagePath);
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
            bmp.recycle();
            msg.thumbData = UITools.bmpToByteArray(thumbBmp, true);
            // msg.title="run-title";
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = "img" + String.valueOf(System.currentTimeMillis());
            req.message = msg;
            req.scene = isTimeLine ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
            iwxapi.sendReq(req);

        } else if (!iwxapi.isWXAppInstalled()) {
            Toast.makeText(context, "微信分享需要安装微信客户端", Toast.LENGTH_LONG).show();
        } else if (!iwxapi.isWXAppInstalled()) {
            Toast.makeText(context, "您的微信客户端版本过低，请升级后重试", Toast.LENGTH_LONG).show();
        }
    }

    public IWXAPI regToWxapp(Activity activity) {
        iwxapi = WXAPIFactory.createWXAPI(activity, WEIXIN_APP_KEY, true);
        iwxapi.registerApp(WEIXIN_APP_KEY);
        return iwxapi;
    }

    public boolean payByWeixin(String payBill) {
        if (TextUtils.isEmpty(payBill)) {
            return false;
        }
        try {
            JSONObject json = new JSONObject(payBill);
            if (!json.has("retcode")) {
                PayReq req = new PayReq();
                req.appId = json.getString("appid");
                req.partnerId = json.getString("partnerid");
                req.prepayId = json.getString("prepayid");
                req.nonceStr = json.getString("noncestr");
                req.timeStamp = json.getString("timestamp");
                req.packageValue = json.getString("package");
                req.sign = json.getString("sign");
                req.extData = "app data";
                iwxapi.sendReq(req);
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            return false;
        }
    }

}
