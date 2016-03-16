package com.tencent.tls.service;

import android.content.Context;
import android.view.View;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tls.helper.Util;

/**
 * Created by dgy on 15/7/23.
 */
public class WXLoginService {
    private IWXAPI iwxapi;
    private Context context;

    public WXLoginService(Context context, View btn_wxlogin) {
        this.context = context;

        btn_wxlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wxLogin();
            }
        });
    }

    private void wxLogin() {
        iwxapi = WXAPIFactory.createWXAPI(this.context, TLSConfiguration.WX_APP_ID, true);

        if (!iwxapi.isWXAppInstalled()) { // WeChat Client doesn't installed
            Util.showToast(context, "您尚未安装微信，无法进行微信登录!");
            return;
        }

        iwxapi.registerApp(TLSConfiguration.WX_APP_ID);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = Constants.WX_SCOPE;
        req.state = "tencent_tls_ui_wxlogin";
        iwxapi.sendReq(req);
    }
}
