package com.tencent.tls.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.tls.helper.MResource;
import com.tencent.tls.model.ErrorCode;
import com.tencent.tls.model.LoginSession;
import com.tencent.tls.service.Constants;
import com.tencent.tls.service.SinaWeiboLoginService;
import com.tencent.tls.service.TLSService;

import tencent.tls.platform.TLSUserInfo;
import tencent.tls.report.QLog;

public class StrAccLoginActivity extends BaseLoginActivity {

    private final static String TAG = "StrAccLoginActivity";

    private TLSService tlsService;
    private int login_way = Constants.STR_ACC_LOGIN | Constants.QQ_LOGIN | Constants.WX_LOGIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MResource.getIdByName(getApplication(), "layout", "tencent_tls_ui_activity_independent_login"));

        tlsService = TLSService.getInstance();

        if ((login_way & Constants.STR_ACC_LOGIN) != 0) { // 账号密码登录
            initAccountLoginService();
        }

        tlsService.initGuestLoginService(this, findViewById(MResource.getIdByName(this, "id", "btn_guestlogin")));
        tlsService.initQQLoginService(this, findViewById(MResource.getIdByName(this, "id", "btn_qqlogin")));
        tlsService.initWXLoginService(this, findViewById(MResource.getIdByName(this, "id", "btn_wxlogin")));
     //   new SinaWeiboLoginService(this, findViewById(MResource.getIdByName(this, "id", "btn_sinawblogin")));

        SharedPreferences settings = getSharedPreferences(Constants.TLS_SETTING, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.SETTING_LOGIN_WAY, Constants.STR_ACC_LOGIN);
        editor.commit();

    }

    private void initTLSLogin() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TLSUserInfo userInfo = tlsService.getLastUserInfo();
                if (userInfo != null) {
                    EditText editText = (EditText) StrAccLoginActivity.this
                            .findViewById(MResource.getIdByName(getApplication(), "id", "username"));
                    editText.setText(userInfo.identifier);
                }
            }
        });
    }

    private void initAccountLoginService() {
        tlsService.initAccountLoginService(this,
                (TextView) findViewById(MResource.getIdByName(getApplication(), "id", "username")),
                (TextView) findViewById(MResource.getIdByName(getApplication(), "id", "password")),
                findViewById(MResource.getIdByName(getApplication(), "id", "btn_login")));

        // 设置点击"注册新用户"事件
        findViewById(MResource.getIdByName(getApplication(), "id", "registerNewUser"))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(StrAccLoginActivity.this, StrAccRegisterActivity.class);
                        startActivityForResult(intent, Constants.USRPWD_REG_REQUEST_CODE);
                    }
                });

        // 手机短信登录
        findViewById(MResource.getIdByName(getApplication(), "id", "hostLogin"))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(StrAccLoginActivity.this, PhoneSmsLoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

//        initTLSLogin();
    }

    //应用调用Andriod_SDK接口时，使能成功接收到回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            QLog.i(TAG + " onActivityResult");
            super.onActivityResult(requestCode, resultCode, data);

            // SSO 授权回调
            // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
            if (SinaWeiboLoginService.mSsoHandler != null) {
                SinaWeiboLoginService.mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            }

            tlsService.onActivityResultForQQLogin(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();



        Intent intent = getIntent();
        if (intent == null)     return;

        // 判断是否是从微信登录界面返回的
        int wx_login = intent.getIntExtra(Constants.EXTRA_WX_LOGIN, Constants.WX_LOGIN_NON);
        if (wx_login != Constants.WX_LOGIN_NON) {
            if (wx_login == Constants.WX_LOGIN_SUCCESS) {

                LoginSession loginSession = new LoginSession();
                loginSession.setLoginWay(Constants.WX_LOGIN);
                loginSession.setOpenid(intent.getStringExtra(Constants.EXTRA_WX_OPENID));
                loginSession.setAccess_token(intent.getStringExtra(Constants.EXTRA_WX_ACCESS_TOKEN));
                TLSService.getInstance().loginCallBack.onSuccess(loginSession);
                finish();
            } else {
                TLSService.getInstance().loginCallBack.onFailure(ErrorCode.AUTHORIZATION_FAIL, "微信登录失败");
            }
            return;
        }

        // 判断是否是从注册界面返回
        String username = intent.getStringExtra(Constants.USERNAME);
        String password = intent.getStringExtra(Constants.PASSWORD);

        if (username != null && password != null && username.length() > 0 && password.length() > 0) {
            intent.putExtra(Constants.USERNAME, "");
            intent.putExtra(Constants.PASSWORD, "");
            ((EditText) findViewById(MResource.getIdByName(getApplication(), "id", "username"))).setText(username);
            ((EditText) findViewById(MResource.getIdByName(getApplication(), "id", "password"))).setText(password);

            findViewById(MResource.getIdByName(getApplication(), "id", "btn_login")).performClick();

            return;
        }

    }

    protected void onDestroy() {
        super.onDestroy();

        tlsService = null;

        QLog.i(getClass().getName() + " onDestroy");
    }
}
