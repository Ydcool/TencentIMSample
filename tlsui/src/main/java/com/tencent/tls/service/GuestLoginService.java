package com.tencent.tls.service;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.tencent.tls.model.ErrorCode;
import com.tencent.tls.model.LoginSession;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by markding on 2015/9/16.
 */
public class GuestLoginService {

    private Activity mActivity;
    private View mButton;

    public GuestLoginService(Activity activity, View button) {
        this.mActivity = activity;
        this.mButton = button;

        this.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TLSService.getInstance().TLSGuestLogin(new TLSService.GuestLoginListener(){

                    @Override
                    public void OnGuestLoginSuccess(TLSUserInfo tlsUserInfo) {
                        Intent intent = new Intent();
                        intent.putExtra(Constants.EXTRA_LOGIN_WAY, Constants.GUEST_LOGIN);

//                        Util.startSuccessActivity((Activity)mContext, intent);

                        LoginSession loginSession = new LoginSession();
                        loginSession.setLoginWay(Constants.GUEST_LOGIN);
                        loginSession.setIdentifer(tlsUserInfo.identifier);
                        TLSService.getInstance().loginCallBack.onSuccess(loginSession);
                        mActivity.finish();
                    }

                    @Override
                    public void OnGuestLoginFail(TLSErrInfo tlsErrInfo) {
                        TLSService.getInstance().loginCallBack.onFailure(ErrorCode.OTHERERROR, tlsErrInfo.Msg);
                    }

                    @Override
                    public void OnGuestLoginTimeout(TLSErrInfo tlsErrInfo) {
                        TLSService.getInstance().loginCallBack.onFailure(ErrorCode.TIMEOUT, tlsErrInfo.Msg);
                    }

                });
            }
        });
    }


}
