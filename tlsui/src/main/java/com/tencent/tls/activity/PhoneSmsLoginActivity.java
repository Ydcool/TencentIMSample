package com.tencent.tls.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.tls.helper.MResource;
import com.tencent.tls.model.ErrorCode;
import com.tencent.tls.model.LoginSession;
import com.tencent.tls.service.Constants;
import com.tencent.tls.service.SinaWeiboLoginService;
import com.tencent.tls.service.TLSService;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSSmsLoginListener;
import tencent.tls.platform.TLSUserInfo;
import tencent.tls.report.QLog;

public class PhoneSmsLoginActivity extends BaseLoginActivity {

    private final static String TAG = "PhoneSmsLoginActivity";

    private TLSService tlsService;
//    private SmsContentObserver smsContentObserver = null;
    private int login_way = Constants.PHONE_SMS_LOGIN | Constants.QQ_LOGIN | Constants.WX_LOGIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MResource.getIdByName(getApplication(), "layout", "tencent_tls_ui_activity_host_login"));

        tlsService = TLSService.getInstance();

        if ((login_way & Constants.PHONE_SMS_LOGIN) != 0) { // 短信登录
            initSmsService();
//            smsContentObserver = new SmsContentObserver(new Handler(),
//                    this,
//                    (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "checkCode_hostLogin")),
//                    Constants.SMS_LOGIN_SENDER);
            //注册短信变化监听
//            this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsContentObserver);
        }

        tlsService.initGuestLoginService(this,  findViewById(MResource.getIdByName(this, "id", "btn_guestlogin")));
        tlsService.initQQLoginService(this, findViewById(MResource.getIdByName(this, "id", "btn_qqlogin")));
        tlsService.initWXLoginService(this, findViewById(MResource.getIdByName(this, "id", "btn_wxlogin")));

   //     new SinaWeiboLoginService(this, findViewById(MResource.getIdByName(this, "id", "btn_sinawblogin")));

        SharedPreferences settings = getSharedPreferences(Constants.TLS_SETTING, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.SETTING_LOGIN_WAY, Constants.PHONE_SMS_LOGIN);
        editor.commit();
    }

    private void initSmsService() {
        tlsService.initSmsLoginService(this,
                (Button) findViewById(MResource.getIdByName(getApplication(), "id", "selectCountryCode")),
                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "phoneNumber_hostLogin")),
                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "checkCode_hostLogin")),
                (Button) findViewById(MResource.getIdByName(getApplication(), "id", "btn_requireCheckCode_hostLogin")),
                (Button) findViewById(MResource.getIdByName(getApplication(), "id", "btn_hostLogin"))
        );

        findViewById(MResource.getIdByName(getApplication(), "id", "selectCountryCode")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneSmsLoginActivity.this, SelectCountryCodeActivity.class);
                intent.putExtra(Constants.EXTRA_LOGIN_WAY, Constants.PHONE_SMS_LOGIN);
                startActivity(intent);
            }
        });

//        initTLSLogin();

        // 设置点击"注册新用户"事件
        findViewById(MResource.getIdByName(getApplication(), "id", "hostRegisterNewUser"))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PhoneSmsLoginActivity.this, PhoneSmsRegisterActivity.class);
                        startActivityForResult(intent, Constants.SMS_REG_REQUEST_CODE);
                    }
                });

        // 用户名登录
        findViewById(MResource.getIdByName(getApplication(), "id", "accountLogin"))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PhoneSmsLoginActivity.this, StrAccLoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void initTLSLogin() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TLSUserInfo userInfo = tlsService.getLastUserInfo();
                if (userInfo != null) {
                    EditText editText = (EditText) PhoneSmsLoginActivity.this
                            .findViewById(MResource.getIdByName(getApplication(), "id", "phoneNumber_hostLogin"));
                    String phoneNumber = userInfo.identifier;
                    phoneNumber = phoneNumber.substring(phoneNumber.indexOf('-') + 1);
                    editText.setText(phoneNumber);
                }
            }
        });
    }

    //应用调用Andriod_SDK接口时，使能成功接收到回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            QLog.i(TAG + " onActivityResult requestCode=" + requestCode);
            super.onActivityResult(requestCode, resultCode, data);

            // SSO 授权回调
            // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
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

        // 判断是否是从注册界面返回的
        String countryCode = intent.getStringExtra(Constants.COUNTRY_CODE);
        String phoneNumber = intent.getStringExtra(Constants.PHONE_NUMBER);

        if (countryCode != null && phoneNumber != null) {

            Log.e(TAG, "onResume" + countryCode + "-" + phoneNumber);

            tlsService.smsLogin(countryCode, phoneNumber, new TLSSmsLoginListener() {
                @Override
                public void OnSmsLoginAskCodeSuccess(int i, int i1) {}

                @Override
                public void OnSmsLoginReaskCodeSuccess(int i, int i1) {}

                @Override
                public void OnSmsLoginVerifyCodeSuccess() {}

                @Override
                public void OnSmsLoginSuccess(TLSUserInfo tlsUserInfo) {

                    LoginSession loginSession = new LoginSession();
                    loginSession.setLoginWay(Constants.PHONE_SMS_LOGIN);
                    loginSession.setIdentifer(tlsUserInfo.identifier);
                    TLSService.getInstance().loginCallBack.onSuccess(loginSession);
                    finish();
                }

                @Override
                public void OnSmsLoginFail(TLSErrInfo tlsErrInfo) {
                    TLSService.getInstance().loginCallBack.onFailure(ErrorCode.OTHERERROR, tlsErrInfo.Msg);
                }

                @Override
                public void OnSmsLoginTimeout(TLSErrInfo tlsErrInfo) {
                    TLSService.getInstance().loginCallBack.onFailure(ErrorCode.TIMEOUT, tlsErrInfo.Msg);
                }
            });
            return;
        }

        // 选择国家码界面返回
        String countryName = intent.getStringExtra(Constants.COUNTRY_NAME);
        if (countryName != null && countryCode != null) {
            ((Button) findViewById(MResource.getIdByName(getApplication(), "id", "selectCountryCode"))).setText(countryName + "+" + countryCode);
        }

    }

    protected void onDestroy() {
        super.onDestroy();

        QLog.i(getClass().getName() + " onDestroy");

//        if (smsContentObserver != null) {
//            this.getContentResolver().unregisterContentObserver(smsContentObserver);
//        }
//
//        smsContentObserver = null;
        tlsService = null;
    }

}
