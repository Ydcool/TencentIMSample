package com.tencent.tls.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.tencent.tls.activity.StrAccLoginActivity;
import com.tencent.tls.helper.Util;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by dgy on 15/8/13.
 */
public class StrAccRegisterService {

    private final static String TAG = "StrAccRegisterService";

    private Context context;
    private TextView txt_username;
    private TextView txt_password;
    private TextView txt_repassword;
    private View btn_register;

    private TLSService tlsService;
    private StrAccRegListener strAccRegListener;

    private String username;
    private String password;

    public StrAccRegisterService(final Context context,
                                 TextView txt_username,
                                 TextView txt_password,
                                 TextView txt_repassword,
                                 View btn_register) {
        this.context = context;
        this.txt_username = txt_username;
        this.txt_password = txt_password;
        this.txt_repassword = txt_repassword;
        this.btn_register = btn_register;

        tlsService = TLSService.getInstance();
        strAccRegListener = new StrAccRegListener();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = StrAccRegisterService.this.txt_username.getText().toString();
                password = StrAccRegisterService.this.txt_password.getText().toString();
                String tmp = StrAccRegisterService.this.txt_repassword.getText().toString();

                if (!Util.validUsername(StrAccRegisterService.this.context, username) ||
                        !Util.validPassword(StrAccRegisterService.this.context, password)) {
                    return;
                }

                if (!password.equals(tmp)) {
                    Util.showToast(StrAccRegisterService.this.context, "两次输入的密码不一致");
                    return;
                }

                int result = tlsService.TLSStrAccReg(username, password, strAccRegListener);
                if (result == TLSErrInfo.INPUT_INVALID) {
                    Util.showToast(context, "输入非法");
                }
            }
        });
    }

    class StrAccRegListener implements TLSStrAccRegListener {
        @Override
        public void OnStrAccRegSuccess(TLSUserInfo userInfo) {
            Util.showToast(context, "成功注册了一个字符串账号：\n" + userInfo.identifier);
            Intent intent = new Intent(context, StrAccLoginActivity.class);
            intent.putExtra(Constants.EXTRA_USRPWD_REG, Constants.USRPWD_REG_SUCCESS);
            intent.putExtra(Constants.USERNAME, username);
            intent.putExtra(Constants.PASSWORD, password);
            context.startActivity(intent);
            ((Activity) context).finish();
        }

        @Override
        public void OnStrAccRegFail(TLSErrInfo errInfo) {
            Util.notOK(context, errInfo);
        }

        @Override
        public void OnStrAccRegTimeout(TLSErrInfo errInfo) {
            Util.notOK(context, errInfo);
        }
    }
}
