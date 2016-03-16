package com.tencent.tls.service;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tencent.tls.activity.ImgCodeActivity;
import com.tencent.tls.helper.Util;
import com.tencent.tls.model.ErrorCode;
import com.tencent.tls.model.LoginSession;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by dgy on 15/8/14.
 */
public class PhonePwdLoginService {

    private final static String TAG = "PhonePwdLoginService";

    private Activity mActivity;
    private TextView txt_countrycode;
    private TextView txt_phone;
    private TextView txt_pwd;

    private String countrycode;
    private String phone;
    private String password;

    private TLSService tlsService;
    public static PwdLoginListener pwdLoginListener;

    public PhonePwdLoginService(Activity activity,
                                TextView txt_countrycode,
                                TextView txt_phone,
                                TextView txt_pwd,
                                View btn_login){
        this.mActivity = activity;
        this.txt_countrycode = txt_countrycode;
        this.txt_phone = txt_phone;
        this.txt_pwd = txt_pwd;

        tlsService = TLSService.getInstance();
        pwdLoginListener = new PwdLoginListener();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countrycode = PhonePwdLoginService.this.txt_countrycode.getText().toString();
                countrycode = countrycode.substring(countrycode.indexOf('+') + 1);  // 解析国家码
                phone = PhonePwdLoginService.this.txt_phone.getText().toString();
                password = PhonePwdLoginService.this.txt_pwd.getText().toString();

                // 验证手机号和密码的有效性
                if (phone.length() == 0 || password.length() == 0) {
                    Util.showToast(PhonePwdLoginService.this.mActivity, "手机号密码不能为空");
                    return;
                }

                Log.e(TAG, Util.getWellFormatMobile(countrycode, phone));

                tlsService.TLSPwdLogin(Util.getWellFormatMobile(countrycode, phone), password, pwdLoginListener);
            }
        });
    }

    class PwdLoginListener implements TLSPwdLoginListener {
        @Override
        public void OnPwdLoginSuccess(TLSUserInfo userInfo) {
            LoginSession loginSession = new LoginSession();
            loginSession.setLoginWay(Constants.PHONE_PWD_LOGIN);
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
            intent.putExtra(Constants.EXTRA_LOGIN_WAY, Constants.PHONE_PWD_LOGIN);
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
