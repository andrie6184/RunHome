package com.runnerfun.tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.runnerfun.R;
import com.runnerfun.RunApplication;
import com.runnerfun.beans.ResponseBean;
import com.runnerfun.beans.ThirdLoginBean;
import com.runnerfun.model.thirdpart.ThirdAccountModel;
import com.runnerfun.model.thirdpart.WeiboInfoBean;
import com.runnerfun.network.NetworkManager;
import com.runnerfun.wxapi.WXEntryActivity;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.UserInfo;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

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
public class ThirdpartAuthManager {

    public interface ThirdPartActionListener {
        void onSuccess(int action, int type, boolean isFirst);

        void onFailed(int action, int type, String result);

        void onCanceled(int action, int type);
    }

    public static final String SP_KEY_LAST_RID = "SP_KEY_LAST_RID";
    public static final String SP_KEY_LAST_COIN = "SP_KEY_LAST_COIN";

    public static final int ACTION_TAG_LOGIN = 1001;
    public static final int ACTION_TAG_SHARE = 1002;

    public static final int TYPE_THIRD_WEIBO = 101;
    public static final int TYPE_THIRD_WECHAT = 102;
    public static final int TYPE_THIRD_QQ = 103;

    public static final String WEIXIN_APP_KEY = "wx8d08c221f73a378e";
    public static final String WEIXIN_APP_SECRET = "544e1939b3779abca4f9c7302de0c046";
    public static final String WEIBO_APP_KEY = "2756294164";
    private static final String QQ_APP_KEY = "1105835637";

    private static final String WEIBO_REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
    private static final String WEIBO_ACCESS_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "invitation_write";

    private static final String LOGO_WEB_URL = "https://img.yzcdn.cn/upload_files/2016/07/14/" +
            "691d8126a24c027b3f12e59b782fe07a.jpeg?imageView2/2/w/120/h/120/q/75/format/jpeg";

    private static IWXAPI iwxapi;
    private static Tencent mTencent;

    public static IUiListener mTencentListener;
    public static SsoHandler ssoHandler;

    private static ThirdpartAuthManager instance = new ThirdpartAuthManager();

    private ThirdpartAuthManager() {
        mTencent = Tencent.createInstance(QQ_APP_KEY, RunApplication.getAppContex());
        iwxapi = WXAPIFactory.createWXAPI(RunApplication.getAppContex(), WEIXIN_APP_KEY);
        iwxapi.registerApp(WEIXIN_APP_KEY);
    }

    public static ThirdpartAuthManager instance() {
        return instance;
    }

    public void startQQLogin(final Activity activity, final ThirdPartActionListener listener) {
        mTencent = Tencent.createInstance(QQ_APP_KEY, activity.getApplicationContext());
        mTencentListener = new IUiListener() {
            @Override
            public void onComplete(final Object response) {
                final String id = ((JSONObject) response).optString("openid", "");
                String accessToken = ((JSONObject) response).optString("access_token", "");
                String expires = ((JSONObject) response).optString("expires_in", "");
                mTencent.setOpenId(id);
                mTencent.setAccessToken(accessToken, expires);

                final UserInfo mInfo = new UserInfo(activity, mTencent.getQQToken());
                mInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        JSONObject info = (JSONObject) response;
                        NetworkManager.instance.loginWithThird(id, "qq", info.optString("nickname", ""),
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
                                        NetworkManager.instance.setLoginInfo();
                                        if (listener != null) {
                                            listener.onSuccess(ACTION_TAG_LOGIN, TYPE_THIRD_QQ,
                                                    loginBean.getFirstlogin() == 1);
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
            mTencent.login(activity, "all", mTencentListener);
        }
    }

    public void startQQShare(Activity context, IUiListener mListener, String imageUrl) {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, ApplicationUtils.getAppName());
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        mTencent.shareToQQ(context, params, mListener);
    }

    public void startQQShare(Activity context, IUiListener mListener, String url, String title, String content) {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, LOGO_WEB_URL);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, ApplicationUtils.getAppName());
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        mTencent.shareToQQ(context, params, mListener);
    }

    public void startWeiboLogin(final Activity activity, final ThirdPartActionListener listener) {
        AuthInfo authInfo = new AuthInfo(activity, WEIBO_APP_KEY,
                WEIBO_REDIRECT_URL, WEIBO_ACCESS_SCOPE);
        ssoHandler = new SsoHandler(activity, authInfo);
        ssoHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                Timber.d("ThirdPartAuthManager", "weibo done");
                final Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeAccessToken(activity, mAccessToken);
                if (mAccessToken.isSessionValid()) {
                    AccessTokenKeeper.writeAccessToken(activity, mAccessToken);
                    ThirdAccountModel.instance.getWeiboUserInfo(mAccessToken.getToken(),
                            mAccessToken.getUid()).observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io()).flatMap(new Func1<WeiboInfoBean,
                            Observable<ResponseBean<ThirdLoginBean>>>() {
                        @Override
                        public Observable<ResponseBean<ThirdLoginBean>> call(WeiboInfoBean info) {
                            try {
                                String name = info.getName();
                                String image = info.getProfile_image_url();
                                return NetworkManager.instance.loginWithThird(mAccessToken.getUid(),
                                        "weibo", name, image).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread());
                            } catch (Throwable e) {
                                if (listener != null) {
                                    listener.onFailed(ACTION_TAG_LOGIN, TYPE_THIRD_QQ, "数据解析失败");
                                }
                            }
                            return null;
                        }
                    }).subscribe(new Action1<ResponseBean<ThirdLoginBean>>() {
                        @Override
                        public void call(ResponseBean<ThirdLoginBean> bean) {
                            if (bean.getCode() == 0) {
                                NetworkManager.instance.setLoginInfo();
                                if (listener != null) {
                                    listener.onSuccess(ACTION_TAG_LOGIN, TYPE_THIRD_QQ,
                                            bean.getData().getFirstlogin() == 1);
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

    public void startWeiboShare(IWeiboShareAPI mWeiboShareAPI, final Activity context, String url,
                                String title, String content) {
        // 获取微博客户端相关信息，如是否安装、支持 SDK 的版本
        boolean isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();

        if (isInstalledWeibo) {
            WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
            weiboMessage.textObject = getTextObj(content);
            weiboMessage.mediaObject = getWebpageObj(context, url, title, content);

            SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
            // 用transaction唯一标识一个请求
            request.transaction = String.valueOf(System.currentTimeMillis());
            request.multiMessage = weiboMessage;

            AuthInfo authInfo = new AuthInfo(context, WEIBO_APP_KEY, WEIBO_REDIRECT_URL, WEIBO_ACCESS_SCOPE);
            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(context);
            String token = "";
            if (accessToken != null) {
                token = accessToken.getToken();
            }

            boolean result = mWeiboShareAPI.sendRequest(context, request);
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
            req.state = "paobuzhijia_social_login";
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

    public void startWeixinShare(Activity context, boolean isTimeLine, String url, String title, String content) {
        if (iwxapi.isWXAppInstalled() && iwxapi.isWXAppInstalled()) {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = url;
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = title;
            msg.description = content;
            Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            msg.thumbData = bmpToByteArray(thumb, true);

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("webpage");
            req.message = msg;
            req.scene = isTimeLine ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
            WXEntryActivity.type = 1;
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
                req.packageValue = "Sign=WXPay"; //json.getString("package");
                // req.timeStamp = json.getString("timestamp");
                // req.sign = json.getString("sign").toUpperCase();
                req.timeStamp = String.valueOf(genTimeStamp());

                List<NameValuePair> signParams = new LinkedList<NameValuePair>();
                signParams.add(new BasicNameValuePair("appid", req.appId));
                signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
                signParams.add(new BasicNameValuePair("package", req.packageValue));
                signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
                signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
                signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
                req.sign = genAppSign(signParams);

                // req.extData = "app data";
                iwxapi.sendReq(req);
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            return false;
        }
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append("RcxFyx3QXv2IbpHtJ4g9y8Y98wamkoM7");

        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        Log.e("orion", appSign);
        return appSign;
    }

    public static void setLastRidForShare(String rid) {
        if (!TextUtils.isEmpty(rid)) {
            RunApplication.getAppContex().sharedPreferences.edit().putString(SP_KEY_LAST_RID, rid).apply();
        }
    }

    public static String getLastRidForShare() {
        return RunApplication.getAppContex().sharedPreferences.getString(SP_KEY_LAST_RID, "0001");
    }

    public static void setLastCoinForShare(String coin) {
        if (!TextUtils.isEmpty(coin)) {
            RunApplication.getAppContex().sharedPreferences.edit().putString(SP_KEY_LAST_COIN, coin).apply();
        }
    }

    public static String getLastCoinForShare() {
        return RunApplication.getAppContex().sharedPreferences.getString(SP_KEY_LAST_COIN, "100");
    }

    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    private static TextObject getTextObj(String text) {
        TextObject textObject = new TextObject();
        textObject.text = text;
        return textObject;
    }

    private static ImageObject getImageObj(Activity context) {
        ImageObject imageObject = new ImageObject();
        Drawable logo = context.getResources().getDrawable(R.mipmap.ic_launcher);
        imageObject.setImageObject(drawableToBitmap(logo));
        return imageObject;
    }

    private static WebpageObject getWebpageObj(Activity context, String url, String shareName, String shareDesc) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = shareName;
        mediaObject.description = shareDesc;

        // 设置 Bitmap 类型的图片到视频对象里
        Drawable logo = context.getResources().getDrawable(R.mipmap.ic_launcher);
        mediaObject.setThumbImage(drawableToBitmap(logo));
        mediaObject.actionUrl = url;
        mediaObject.defaultText = mediaObject.title;
        return mediaObject;
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE
                ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

}
