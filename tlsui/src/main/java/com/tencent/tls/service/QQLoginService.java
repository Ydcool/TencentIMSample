package com.tencent.tls.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tencent.tls.helper.Util;
import com.tencent.tls.model.ErrorCode;
import com.tencent.tls.model.LoginSession;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dgy on 15/7/23.
 */
public class QQLoginService {

    private final static String TAG = "QQLoginService";

    // 授权结果常量定义
    private static final int AUTHORIZATION_SUCC = 1;            // 授权成功
    private static final int AUTHORIZATION_FAIL = 2;            // 授权失败
    private static final int AUTHORIZATION_CANCEL = 3;          // 取消授权

    private Activity mActivity;
    private Tencent mTencent;
    private Handler handler;
    private String openid;
    private String access_token;
    private String expires;

    public QQLoginService(Activity activity, View btn_hostQQLogin) {
        this.mActivity = activity;

        handler = new Handler(callback);

        // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI
        // 其中APP_ID是分配给第三方应用的appid，类型为String
        mTencent = Tencent.createInstance(TLSConfiguration.QQ_APP_ID, activity.getApplicationContext());
        btn_hostQQLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickQQLogin();
            }
        });
    }

    private void onClickQQLogin() {
        if (!mTencent.isSessionValid()) {
            qqLogin(listener);
        } else {
            qqLogout();
            onClickQQLogin();
        }
    }

    public void qqLogin(IUiListener listener) {

        mTencent.login(mActivity, Constants.QQ_SCOPE, listener);
    }

    public void qqLogout() {
        mTencent.logout(mActivity);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResultData(requestCode, resultCode, data, listener);

        if(requestCode == com.tencent.connect.common.Constants.REQUEST_API) {
            if (resultCode == com.tencent.connect.common.Constants.RESULT_LOGIN) {
                Tencent.handleResultData(data, listener);
            }
        }
    }

    public boolean qqHasLogined() {
        return mTencent.isSessionValid();
    }

    private Callback callback = new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.arg1) {
                case AUTHORIZATION_SUCC: { // 授权成功
                    JSONObject object = (JSONObject) msg.obj;
                    try {
                        openid = object.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID).toString();
                        access_token = object.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN).toString();
                        expires = object.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);

                        if (!TextUtils.isEmpty(access_token) && !TextUtils.isEmpty(expires)
                                && !TextUtils.isEmpty(openid)) {
                            mTencent.setOpenId(openid);
                            mTencent.setAccessToken(access_token, expires);
                        }
                        LoginSession loginSession = new LoginSession();
                        loginSession.setLoginWay(Constants.QQ_LOGIN);
                        loginSession.setOpenid(openid);
                        loginSession.setAccess_token(access_token);
                        TLSService.getInstance().loginCallBack.onSuccess(loginSession);
                        mActivity.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case AUTHORIZATION_FAIL: { // 授权失败
                    TLSService.getInstance().loginCallBack.onFailure(ErrorCode.AUTHORIZATION_FAIL, "授权失败");
                    return false;
                }
                case AUTHORIZATION_CANCEL: { // 取消授权
//                    TLSService.getInstance().loginCallBack.onFailure(ErrorCode.AUTHORIZATION_CANCEL, "取消授权");
//                    Util.showToast(mActivity, "登录被取消");
                    return false;
                }
            }
            return false;
        }
    };

    private IUiListener listener = new IUiListener() {
        /** 授权失败的回调 */
        @Override
        public void onError(UiError error) {
            Message msg = new Message();
            msg.arg1 = AUTHORIZATION_FAIL;
            handler.sendMessage(msg);
        }
        /** 授权成功的回调*/
        @Override
        public void onComplete(Object response) {
            Message msg = new Message();
            msg.what = 2;
            msg.arg1 = AUTHORIZATION_SUCC;
            msg.obj = response;
            handler.sendMessage(msg);
        }
        /** 取消授权的回调*/
        @Override
        public void onCancel() {
            Message msg = new Message();
            msg.arg1 = AUTHORIZATION_CANCEL;
            handler.sendMessage(msg);
        }
    };

}
