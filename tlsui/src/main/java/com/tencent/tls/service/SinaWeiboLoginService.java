package com.tencent.tls.service;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.tls.helper.Util;
import com.tencent.tls.model.ErrorCode;
import com.tencent.tls.model.LoginSession;

/**
 * Created by markding on 2015/11/12.
 */
public class SinaWeiboLoginService {

    private static final String TAG = "SinaWeiboLoginService";

    private Activity mActivity;
    private AuthInfo mAuthInfo;

    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    public static SsoHandler mSsoHandler = null;

    public SinaWeiboLoginService(Activity activity, View btn_login) {
        this.mActivity = activity;

        mAuthInfo = new AuthInfo(mActivity, TLSConfiguration.SINA_WEIBO_APP_ID, Constants.REDIRECT_URL, Constants.SINA_WEIBO_SCOPE);
        mSsoHandler = new SsoHandler(mActivity, mAuthInfo);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSsoHandler.authorize(new WeiboAuthListener() {
                    @Override
                    public void onComplete(Bundle bundle) {
                        // 从 Bundle 中解析 Token
                        mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
                        //从这里获取用户输入的 电话号码信息
                        String phoneNum = mAccessToken.getPhoneNum();
                        if (mAccessToken.isSessionValid()) {
                            LoginSession loginSession = new LoginSession();
                            loginSession.setLoginWay(Constants.SINA_WEIBO_LOGIN);
                            loginSession.setOpenid(mAccessToken.getUid());
                            loginSession.setAccess_token(mAccessToken.getToken());
                            TLSService.getInstance().loginCallBack.onSuccess(loginSession);
                            mActivity.finish();
                        } else {
                            // 以下几种情况，您会收到 Code：
                            // 1. 当您未在平台上注册的应用程序的包名与签名时；
                            // 2. 当您注册的应用程序包名与签名不正确时；
                            // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
//                            String code = bundle.getString("code");
//                            String message = "授权失败";
//                            if (!TextUtils.isEmpty(code)) {
//                                message = message + "\nObtained the code: " + code;
//                            }
//                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                            TLSService.getInstance().loginCallBack.onFailure(ErrorCode.AUTHORIZATION_FAIL, "授权失败");
                        }
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        TLSService.getInstance().loginCallBack.onFailure(ErrorCode.EXCEPTION, "发生异常");
                    }

                    @Override
                    public void onCancel() {
//                        TLSService.getInstance().loginCallBack.onFailure(ErrorCode.AUTHORIZATION_CANCEL, "取消授权");
                        Util.showToast(mActivity, "登录被取消");
                    }
                });
            }
        });
    }
}
