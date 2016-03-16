package com.tencent.tls.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.qalsdk.QALSDKManager;
import com.tencent.tls.activity.PhonePwdLoginActivity;
import com.tencent.tls.activity.PhoneSmsLoginActivity;
import com.tencent.tls.activity.StrAccLoginActivity;
import com.tencent.tls.callback.LoginCallBack;
import com.tencent.tls.helper.Util;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSGuestLoginListener;
import tencent.tls.platform.TLSHelper;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSPwdRegListener;
import tencent.tls.platform.TLSPwdResetListener;
import tencent.tls.platform.TLSRefreshUserSigListener;
import tencent.tls.platform.TLSSmsLoginListener;
import tencent.tls.platform.TLSSmsRegListener;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;
import tencent.tls.report.QLog;

/**
 * Created by dgy on 15/7/8.
 */
public class TLSService {
    
    private TLSHelper tlsHelper = TLSHelper.getInstance();

    private QQLoginService qqLoginService;

    private static TLSService tlsService = null;

    public LoginCallBack loginCallBack;
    public Activity srcActivity;

    private TLSService(){}

    private RefreshUserSigListener refreshUserSigListener = new RefreshUserSigListener() {
        @Override
        public void onUserSigNotExist() {}
        @Override
        public void OnRefreshUserSigSuccess(TLSUserInfo tlsUserInfo) {}
        @Override
        public void OnRefreshUserSigFail(TLSErrInfo tlsErrInfo) {}
        @Override
        public void OnRefreshUserSigTimeout(TLSErrInfo tlsErrInfo) {}
    };

    private GuestLoginListener guestLoginListener = new GuestLoginListener() {
        @Override
        public void OnGuestLoginSuccess(TLSUserInfo userInfo) {}

        @Override
        public void OnGuestLoginFail(TLSErrInfo errInfo) {}

        @Override
        public void OnGuestLoginTimeout(TLSErrInfo errInfo) {}
    };

    public static TLSService getInstance() {
        synchronized (TLSService.class) {
            if (tlsService == null) {
                tlsService = new TLSService();
            }
        }
        return tlsService;
    }

    /**
     * @function: 初始化TLS SDK, 必须在使用TLS SDK相关服务之前调用
     * @param context: 关联的activity
     * */
    public void initTlsSdk(Context context) {
        QALSDKManager.getInstance().setEnv(0);     // 0: sso正式环境 1: sso测试环境, 即beta环境
        QALSDKManager.getInstance().init(context.getApplicationContext(), (int) TLSConfiguration.SDK_APPID);

//        TIMManager.getInstance().init(context.getApplicationContext());
//        TIMManager.getInstance().setEnv(0);
        
        tlsHelper = tlsHelper.init(context.getApplicationContext(), TLSConfiguration.SDK_APPID);
        tlsHelper.setTimeOut(TLSConfiguration.TIMEOUT);
        tlsHelper.setLocalId(TLSConfiguration.LANGUAGE_CODE);
        tlsHelper.setCountry(Integer.parseInt(TLSConfiguration.COUNTRY_CODE)); // 存储注册时所在国家，只须在初始化时调用一次
//        tlsHelper.setTestHost("", true);                     // 走sso
//        tlsHelper.setTestHost("113.108.64.238", false);        // 不走sso, beta
//        tlsHelper.setTestHost("113.108.76.104", false);      // 不走sso, test
    }

    /**
     * 代理TLSHelper的接口
     */

    public void initSmsLoginService(Activity activity,
                                    Button txtCountryCode,
                                    EditText txtPhoneNumber,
                                    EditText txtCheckCode,
                                    Button btn_requireCheckCode,
                                    Button btn_login) { // 初始化短信登录服务
        new PhoneSmsLoginService(activity, txtCountryCode, txtPhoneNumber, txtCheckCode,
                btn_requireCheckCode, btn_login);

    }

    public void initAccountLoginService(Activity activity,
                                        TextView txt_username,
                                        TextView txt_password,
                                        View btn_login) {
        new StrAccLoginService(activity, txt_username, txt_password, btn_login);
    }

    public void initPhonePwdLoginService(Activity activity,
                                         Button txt_countrycode,
                                         EditText txt_phone,
                                         EditText txt_pwd,
                                         Button btn_login) {
        new PhonePwdLoginService(activity, txt_countrycode, txt_phone, txt_pwd, btn_login);
    }

    public void initPhonePwdRegisterService(Context context,
                                            Button txt_countryCode,
                                            EditText txt_phoneNumber,
                                            EditText txt_checkCode,
                                            Button btn_requireCheckCode,
                                            Button btn_verify) {
        new PhonePwdRegisterService(context, txt_countryCode, txt_phoneNumber, txt_checkCode, btn_requireCheckCode, btn_verify);
    }

    public void initResetPhonePwdService(Context context,
                                            Button txt_countryCode,
                                            EditText txt_phoneNumber,
                                            EditText txt_checkCode,
                                            Button btn_requireCheckCode,
                                            Button btn_verify) {
        new ResetPhonePwdService(context, txt_countryCode, txt_phoneNumber, txt_checkCode, btn_requireCheckCode, btn_verify);
    }

    public void initGuestLoginService(Activity activity, View button) {
        new GuestLoginService(activity, button);
    }


    public int smsLogin(String countryCode, String phoneNumber, TLSSmsLoginListener smsLoginListener) {
        return tlsHelper.TLSSmsLogin(Util.getWellFormatMobile(countryCode, phoneNumber), smsLoginListener);
    }

    public int smsLoginAskCode(String countryCode, String phoneNumber, TLSSmsLoginListener listener) {
        return tlsHelper.TLSSmsLoginAskCode(Util.getWellFormatMobile(countryCode, phoneNumber), listener);
    }

    public int smsLoginVerifyCode(String code, TLSSmsLoginListener listener) {
        return tlsHelper.TLSSmsLoginVerifyCode(code, listener);
    }

    public String getUserSig(String identify) {
        return tlsHelper.getUserSig(identify);
    }

    public TLSUserInfo getLastUserInfo() {
        return tlsHelper.getLastUserInfo();
    }

    public String getLastUserIdentifier() {
        TLSUserInfo userInfo = getLastUserInfo();
        if (userInfo != null)
            return userInfo.identifier;
        else
            return null;
    }

    public void clearUserInfo(String identifier) {
        QLog.i("clearUserInfo");
        tlsHelper.clearUserInfo(identifier);
    }

    public boolean needLogin(String identifier) {
        if (identifier == null)
            return true;
        return tlsHelper.needLogin(identifier);
    }

    public String getSDKVersion() {
        return tlsHelper.getSDKVersion();
    }

    public void refreshUserSig(String identifier, RefreshUserSigListener refreshUserSigListener) {
        if (null == refreshUserSigListener)
            refreshUserSigListener = this.refreshUserSigListener;

        if (!needLogin(identifier))
            tlsHelper.TLSRefreshUserSig(identifier, refreshUserSigListener);
        else
            refreshUserSigListener.onUserSigNotExist();
    }

    public int TLSPwdLogin(String identifier, String password, TLSPwdLoginListener listener) {
        return tlsHelper.TLSPwdLogin(identifier, password.getBytes(), listener);
    }

    public int TLSPwdLoginVerifyImgcode(String imgCode, TLSPwdLoginListener listener) {
        return tlsHelper.TLSPwdLoginVerifyImgcode(imgCode, listener);
    }

    public int TLSPwdLoginReaskImgcode(TLSPwdLoginListener listener) {
        return tlsHelper.TLSPwdLoginReaskImgcode(listener);
    }

    public int TLSGuestLogin(TLSGuestLoginListener listener) {
        if (null == listener) {
            listener = this.guestLoginListener;
        }

        return tlsHelper.TLSGuestLogin(listener);
    }

    public String getGuestIdentifier() {
        return tlsHelper.getGuestIdentifier();
    }

    /**
     * 代理TLSHelper的接口
     */

    public void initSmsRegisterService(Context context,
                                       Button txtCountryCode,
                                       EditText txtPhoneNumber,
                                       EditText txtCheckCode,
                                       Button btn_requireCheckCode,
                                       Button btn_register) { // 初始化短信注册服务
        new PhoneSmsRegisterService(context, txtCountryCode, txtPhoneNumber, txtCheckCode,
                btn_requireCheckCode, btn_register);
    }

    public int smsRegAskCode(String countryCode, String phoneNumber, TLSSmsRegListener listener) {
        return tlsHelper.TLSSmsRegAskCode(Util.getWellFormatMobile(countryCode, phoneNumber), listener);
    }

    public int TLSPwdRegVerifyCode(String code, TLSPwdRegListener listener) {
        return tlsHelper.TLSPwdRegVerifyCode(code, listener);
    }

    public int TLSPwdRegCommit(String password, TLSPwdRegListener listener) {
        return tlsHelper.TLSPwdRegCommit(password, listener);
    }

    public int TLSPwdResetCommit(String password, TLSPwdResetListener listener) {
        return tlsHelper.TLSPwdResetCommit(password, listener);

    }

    public int smsRegVerifyCode(String code, TLSSmsRegListener listener) {
        return tlsHelper.TLSSmsRegVerifyCode(code, listener);
    }

    public int smsRegCommit(TLSSmsRegListener listener) {
        return tlsHelper.TLSSmsRegCommit(listener);
    }

    public int TLSStrAccReg(String account, String password, TLSStrAccRegListener listener) {
        return tlsHelper.TLSStrAccReg(account, password, listener);
    }

    public int TLSPwdRegAskCode(String countryCode, String phoneNumber, TLSPwdRegListener listener) {
        return tlsHelper.TLSPwdRegAskCode(Util.getWellFormatMobile(countryCode, phoneNumber), listener);
    }

    public void initAccountRegisterService(Context context,
                                           TextView txt_username,
                                           TextView txt_password,
                                           TextView txt_repassword,
                                           View btn_register) {
        new StrAccRegisterService(context, txt_username, txt_password, txt_repassword, btn_register);
    }

    public int TLSPwdResetAskCode(String countryCode, String phoneNumber, TLSPwdResetListener listener) {
        return tlsHelper.TLSPwdResetAskCode(Util.getWellFormatMobile(countryCode, phoneNumber), listener);
    }

    public int TLSPwdResetVerifyCode(String code, TLSPwdResetListener listener) {
        return tlsHelper.TLSPwdResetVerifyCode(code, listener);
    }

    /**
    * 代理QQ登录的接口
    * */
    public void initQQLoginService(Activity activity, View btn_qqlogin) {
        qqLoginService = new QQLoginService(activity, btn_qqlogin);
    }

    public boolean qqHasLogined() {
        return qqLoginService.qqHasLogined();
    }

    public void onActivityResultForQQLogin(int requestCode, int resultCode, Intent data) {
        qqLoginService.onActivityResult(requestCode, resultCode, data);
    }

    public void qqLogOut() {
        try {
            qqLoginService.qqLogout();
        } catch (Exception e) {}
    }

    /**
     * 代理微信登录的接口
     * */
    public void initWXLoginService(Context context, View btn_wxlogin) {
        new WXLoginService(context, btn_wxlogin);
    }


    public interface RefreshUserSigListener extends TLSRefreshUserSigListener {
        public void onUserSigNotExist();
    }

    public interface GuestLoginListener extends TLSGuestLoginListener{}




    /*********************************************************************************/

    /**
     * 显示手机号短信登录界面
     * */
    public void showPhoneSmsLogin(Activity activity, LoginCallBack loginCallBack) {
        if (loginCallBack == null)
            return;

        this.loginCallBack = loginCallBack;
        this.srcActivity = activity;

        Intent intent = new Intent(activity, PhoneSmsLoginActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 显示手机号密码登录界面
     * */
    public void showPhonePwdLogin(Activity activity, LoginCallBack loginCallBack) {
        if (loginCallBack == null)
            return;

        this.loginCallBack = loginCallBack;
        this.srcActivity = activity;

        Intent intent = new Intent(activity, PhonePwdLoginActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 显示字符串账号登录界面
     * */
    public void showStrAccLogin(Activity activity, LoginCallBack loginCallBack) {
        if (loginCallBack == null)
            return;

        this.loginCallBack = loginCallBack;
        this.srcActivity = activity;

        Intent intent = new Intent(activity, StrAccLoginActivity.class);
        activity.startActivity(intent);
    }

}
