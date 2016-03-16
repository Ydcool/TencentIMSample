package com.tencent.tls.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.tls.helper.MResource;
import com.tencent.tls.helper.Util;
import com.tencent.tls.model.ErrorCode;
import com.tencent.tls.model.LoginSession;
import com.tencent.tls.service.Constants;
import com.tencent.tls.service.SinaWeiboLoginService;
import com.tencent.tls.service.TLSService;

import java.util.Timer;
import java.util.TimerTask;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdRegListener;
import tencent.tls.platform.TLSPwdResetListener;
import tencent.tls.platform.TLSUserInfo;
import tencent.tls.report.QLog;

public class PhonePwdLoginActivity extends BaseLoginActivity {

    public final static String TAG = "PhonePwdLoginActivity";

    private TLSService tlsService;
    private int login_way = Constants.PHONE_PWD_LOGIN | Constants.QQ_LOGIN | Constants.WX_LOGIN;
    private String countryCode;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MResource.getIdByName(getApplication(), "layout", "tencent_tls_ui_activity_phone_pwd_login"));

        tlsService = TLSService.getInstance();

        if ((login_way & Constants.PHONE_PWD_LOGIN) != 0) { // 手机号密码登录
            initPhonePwdService();
        }

        tlsService.initGuestLoginService(this, findViewById(MResource.getIdByName(this, "id", "btn_guestlogin")));
        tlsService.initQQLoginService(this, findViewById(MResource.getIdByName(this, "id", "btn_qqlogin")));
        tlsService.initWXLoginService(this, findViewById(MResource.getIdByName(this, "id", "btn_wxlogin")));
        //new SinaWeiboLoginService(this, findViewById(MResource.getIdByName(this, "id", "btn_sinawblogin")));

        SharedPreferences settings = getSharedPreferences(Constants.TLS_SETTING, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.SETTING_LOGIN_WAY, Constants.PHONE_PWD_LOGIN);
        editor.commit();
    }

    private void initPhonePwdService() {
        tlsService.initPhonePwdLoginService(this,
                (Button) findViewById(MResource.getIdByName(getApplication(), "id", "selectCountryCode")),
                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "phone")),
                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "password")),
                (Button) findViewById(MResource.getIdByName(getApplication(), "id", "btn_login"))
        );

        findViewById(MResource.getIdByName(getApplication(), "id", "selectCountryCode")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhonePwdLoginActivity.this, SelectCountryCodeActivity.class);
                intent.putExtra(Constants.EXTRA_LOGIN_WAY, Constants.PHONE_PWD_LOGIN);
                startActivity(intent);
            }
        });

        // 设置点击"注册新用户"事件
        findViewById(MResource.getIdByName(getApplication(), "id", "registerNewUser"))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PhonePwdLoginActivity.this, PhonePwdRegisterActivity.class);
                        startActivityForResult(intent, Constants.PHONEPWD_REG_REQUEST_CODE);
                    }
                });

        // 设置点击"重置密码"事件
        findViewById(MResource.getIdByName(getApplication(), "id", "resetPassword"))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PhonePwdLoginActivity.this, ResetPhonePwdActivity.class);
                        startActivityForResult(intent, Constants.PHONEPWD_RESET_REQUEST_CODE);
                    }
                });
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

        // 判断是从注册界面还是重置密码界面返回
        int where = intent.getIntExtra(Constants.EXTRA_PHONEPWD_REG_RST, Constants.PHONEPWD_NON);
        countryCode= intent.getStringExtra(Constants.COUNTRY_CODE);
        phoneNumber = intent.getStringExtra(Constants.PHONE_NUMBER);
        intent.putExtra(Constants.EXTRA_PHONEPWD_REG_RST, Constants.PHONEPWD_NON);
        intent.putExtra(Constants.COUNTRY_CODE, "");
        intent.putExtra(Constants.PHONE_NUMBER, "");
        if (where == Constants.PHONEPWD_REGISTER) { // 从注册界面返回

            if (countryCode != null && phoneNumber != null && countryCode.length() > 0 && phoneNumber.length() > 0) {
                setPassword(1);      // 弹出填写密码的对话框
            }

            return;

        } else if (where == Constants.PHONEPWD_RESET) { // 从重置密码界面返回

            if (countryCode != null && phoneNumber != null && countryCode.length() > 0 && phoneNumber.length() > 0) {
                setPassword(2);      // 弹出填写密码的对话框
            }

            return;
        }

        // 选择国家码界面返回
        String countryName = intent.getStringExtra(Constants.COUNTRY_NAME);
        if (countryName != null && countryCode != null) {
            ((Button) findViewById(MResource.getIdByName(getApplication(), "id", "selectCountryCode"))).setText(countryName + "+" + countryCode);
        }
    }

    public void setPassword(final int type) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(MResource.getIdByName(getApplication(), "layout", "tencent_tls_ui_dialog"), null);

        final EditText editText = (EditText)view.findViewById(MResource.getIdByName(getApplication(), "id", "password"));
        Button btn_confirm = (Button)view.findViewById(MResource.getIdByName(getApplication(), "id", "btn_confirm"));
        Button btn_cancel = (Button)view.findViewById(MResource.getIdByName(getApplication(), "id", "btn_cancel"));

        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).setCancelable(false).create();

        dialog.show();

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String regPassword = editText.getText().toString();
                if (regPassword.length() == 0) {
                    Util.showToast(PhonePwdLoginActivity.this, "密码不能为空");
                    return;
                }

                if (type == 1) { // 设置密码
                    tlsService.TLSPwdRegCommit(regPassword, new TLSPwdRegListener() {
                        @Override
                        public void OnPwdRegAskCodeSuccess(int reaskDuration, int expireDuration) {}

                        @Override
                        public void OnPwdRegReaskCodeSuccess(int reaskDuration, int expireDuration) {}

                        @Override
                        public void OnPwdRegVerifyCodeSuccess() {}

                        @Override
                        public void OnPwdRegCommitSuccess(TLSUserInfo userInfo) {
                            Util.showToast(PhonePwdLoginActivity.this, "注册成功");
                            ((Button) findViewById(MResource.getIdByName(getApplication(), "id", "selectCountryCode"))).setText(countryCode);
                            ((EditText) findViewById(MResource.getIdByName(getApplication(), "id", "phone"))).setText(phoneNumber);
                            ((EditText) findViewById(MResource.getIdByName(getApplication(), "id", "password"))).setText(regPassword);

                            findViewById(MResource.getIdByName(getApplication(), "id", "btn_login")).performClick();
                        }

                        @Override
                        public void OnPwdRegFail(TLSErrInfo errInfo) {
                            Util.notOK(PhonePwdLoginActivity.this, errInfo);
                        }

                        @Override
                        public void OnPwdRegTimeout(TLSErrInfo errInfo) {
                            Util.notOK(PhonePwdLoginActivity.this, errInfo);
                        }
                    });
                }

                if (type == 2) { // 重置密码
                    tlsService.TLSPwdResetCommit(regPassword, new TLSPwdResetListener() {
                        @Override
                        public void OnPwdResetAskCodeSuccess(int reaskDuration, int expireDuration) {}

                        @Override
                        public void OnPwdResetReaskCodeSuccess(int reaskDuration, int expireDuration) {}

                        @Override
                        public void OnPwdResetVerifyCodeSuccess() {}

                        @Override
                        public void OnPwdResetCommitSuccess(TLSUserInfo userInfo) {
                            Util.showToast(PhonePwdLoginActivity.this, "重置密码成功");
                            ((Button) findViewById(MResource.getIdByName(getApplication(), "id", "selectCountryCode"))).setText(countryCode);
                            ((EditText) findViewById(MResource.getIdByName(getApplication(), "id", "phone"))).setText(phoneNumber);
                            ((EditText) findViewById(MResource.getIdByName(getApplication(), "id", "password"))).setText(regPassword);

                            findViewById(MResource.getIdByName(getApplication(), "id", "btn_login")).performClick();
                        }

                        @Override
                        public void OnPwdResetFail(TLSErrInfo errInfo) {
                            Util.notOK(PhonePwdLoginActivity.this, errInfo);
                        }

                        @Override
                        public void OnPwdResetTimeout(TLSErrInfo errInfo) {
                            Util.notOK(PhonePwdLoginActivity.this, errInfo);
                        }
                    });
                }

                dialog.dismiss();
            }
        });

        showSoftInput(getApplicationContext());
    }

    private static void showSoftInput(final Context ctx) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        }, 200);
    }

    protected void onDestroy() {
        super.onDestroy();

        tlsService = null;
        phoneNumber = null;
        countryCode = null;

        QLog.i(getClass().getName() + " onDestroy");
    }
}
