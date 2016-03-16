package com.example.mydemo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tls.activity.PhoneSmsLoginActivity;
import com.tencent.tls.activity.StrAccLoginActivity;
import com.tencent.tls.activity.PhonePwdLoginActivity;
import com.tencent.tls.helper.Util;
import com.tencent.tls.service.Constants;
import com.tencent.tls.service.TLSConfiguration;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import tencent.tls.platform.TLSAccessTokenInfo;
import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSHelper;
import tencent.tls.platform.TLSOpenAccessTokenListener;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    private String access_token = "";
    private String openid = "";
    private String get_access_token;

    // 获取第一步的code后，请求以下链接获取access_token
    private String GetCodeRequest = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, TLSConfiguration.WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:       // 发送成功
                String code = ((SendAuth.Resp) resp).code;

                TLSHelper.getInstance().TLSOpenAccessToken(2, code, new TLSOpenAccessTokenListener() {
                    @Override
                    public void OnOpenAccessTokenSuccess(TLSAccessTokenInfo accessTokenInfo) {
                        openid = accessTokenInfo.openid;
                        access_token = accessTokenInfo.access_token;
                        Util.showToast(WXEntryActivity.this, "登录成功");
                        returnLoginActivity(Constants.WX_LOGIN_SUCCESS);
                    }

                    @Override
                    public void OnOpenAccessTokenFail(TLSErrInfo tlsErrInfo) {
                        Util.notOK(WXEntryActivity.this, tlsErrInfo);
                        returnLoginActivity(Constants.WX_LOGIN_FAIL);
                    }

                    @Override
                    public void OnOpenAccessTokenTimeout(TLSErrInfo tlsErrInfo) {
                        Util.notOK(WXEntryActivity.this, tlsErrInfo);
                        returnLoginActivity(Constants.WX_LOGIN_FAIL);
                    }
                });
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL: // 发送取消
                Util.showToast(this, "登录被取消");
                returnLoginActivity(Constants.WX_LOGIN_FAIL);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED: // 发送被拒绝
                Util.showToast(this, "登录被拒绝");
                returnLoginActivity(Constants.WX_LOGIN_FAIL);
                break;
            default:
                returnLoginActivity(Constants.WX_LOGIN_FAIL);
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    /**
     * 获取access_token的URL（微信）
     *
     * @param code 授权时，微信回调给的
     * @return URL
     */
    private String getCodeRequest(String code) {
        String result = null;
        GetCodeRequest = GetCodeRequest.replace("APPID", urlEncodeUTF8(TLSConfiguration.WX_APP_ID));
        GetCodeRequest = GetCodeRequest.replace("SECRET", urlEncodeUTF8(TLSConfiguration.WX_APP_SECRET));
        GetCodeRequest = GetCodeRequest.replace("CODE", urlEncodeUTF8(code));
        result = GetCodeRequest;
        return result;
    }

    private static String urlEncodeUTF8(String str) {
        String result = str;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Runnable excuteTask = new Runnable() {
        @Override
        public void run() {
            WXGetAccessToken();
        }
    };

    /**
     * 获取access_token等等的信息(微信)
     */
    private void WXGetAccessToken() {
        openid = "";
        access_token = "";
        try {
            URL url = new URL(get_access_token);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String str = "";
                    StringBuffer sb = new StringBuffer();
                    while ((str = br.readLine()) != null) {
                        sb.append(str);
                    }
                    br.close();
                    JSONObject json = new JSONObject(sb.toString());
                    openid = (String) json.get("openid");
                    access_token = (String) json.get("access_token");

                } else {
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void returnLoginActivity(int login) {
        if (login != Constants.WX_LOGIN_SUCCESS) {
            finish();
            return;
        }

        finish(); // Android 5.0之前必须先finish(), 再startActivity(), 否则登录会存在多个登录界面

        SharedPreferences settings = getSharedPreferences(Constants.TLS_SETTING, 0);
        int login_way = settings.getInt(Constants.SETTING_LOGIN_WAY, Constants.NON_LOGIN);
        Intent intent = null;
        if (login_way == Constants.PHONE_SMS_LOGIN) {
            intent = new Intent(this, PhoneSmsLoginActivity.class);
        } else if (login_way == Constants.STR_ACC_LOGIN){
            intent = new Intent(this, StrAccLoginActivity.class);
        } else if (login_way == Constants.PHONE_PWD_LOGIN) {
            intent = new Intent(this, PhonePwdLoginActivity.class);
        }
        intent.putExtra(Constants.EXTRA_WX_LOGIN, login);
        intent.putExtra(Constants.EXTRA_WX_OPENID, openid);
        intent.putExtra(Constants.EXTRA_WX_ACCESS_TOKEN, access_token);
        startActivity(intent);
    }
}
