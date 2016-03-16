package com.tencent.tls.service;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.tencent.tls.activity.ImgCodeActivity;
import com.tencent.tls.helper.Util;
import com.tencent.tls.model.ErrorCode;
import com.tencent.tls.model.LoginSession;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;
import tencent.tls.report.QLog;

/**
 * Created by dgy on 15/8/12.
 */
public class StrAccLoginService {

    private final static String TAG = "StrAccLoginService";

    private Activity mActivity;
    private TextView txt_username;
    private TextView txt_password;

    private String username;
    private String password;

    private TLSService tlsService;
    public  static PwdLoginListener pwdLoginListener;


    public StrAccLoginService(Activity activity,
                              TextView txt_username,
                              TextView txt_password,
                              View btn_login) {
        this.mActivity = activity;
        this.txt_username = txt_username;
        this.txt_password = txt_password;

        tlsService = TLSService.getInstance();
        pwdLoginListener = new PwdLoginListener();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = StrAccLoginService.this.txt_username.getText().toString();
                password = StrAccLoginService.this.txt_password.getText().toString();

                // 验证用户名和密码的有效性
                if (!Util.validUsername(StrAccLoginService.this.mActivity, username) ||
                        !Util.validPassword(StrAccLoginService.this.mActivity, password)) {
                    return;
                }

//                QLog.i(username + ": " + password);
                tlsService.TLSPwdLogin(username, password, pwdLoginListener);
            }
        });
    }

    class PwdLoginListener implements TLSPwdLoginListener {
        @Override
        public void OnPwdLoginSuccess(TLSUserInfo userInfo) {
            LoginSession loginSession = new LoginSession();
            loginSession.setLoginWay(Constants.STR_ACC_LOGIN);
            loginSession.setIdentifer(userInfo.identifier);
            tlsService.loginCallBack.onSuccess(loginSession);
            mActivity.finish();
        }

        @Override
        public void OnPwdLoginReaskImgcodeSuccess(byte[] picData) {
            ImgCodeActivity.fillImageview(picData);
        }

        @Override
        public void OnPwdLoginNeedImgcode(byte[] picData, TLSErrInfo errInfo) {
            Intent intent = new Intent(mActivity, ImgCodeActivity.class);
            intent.putExtra(Constants.EXTRA_IMG_CHECKCODE, picData);
            intent.putExtra(Constants.EXTRA_LOGIN_WAY, Constants.STR_ACC_LOGIN);
            mActivity.startActivity(intent);
        }

        @Override
        public void OnPwdLoginFail(TLSErrInfo errInfo) {
            tlsService.loginCallBack.onFailure(ErrorCode.OTHERERROR, errInfo.Msg);
        }

        @Override
        public void OnPwdLoginTimeout(TLSErrInfo errInfo) {
            tlsService.loginCallBack.onFailure(ErrorCode.TIMEOUT, errInfo.Msg);
        }
    }
}
