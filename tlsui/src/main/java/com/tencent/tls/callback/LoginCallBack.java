package com.tencent.tls.callback;

import com.tencent.tls.model.ErrorCode;
import com.tencent.tls.model.LoginSession;

/**
 * Created by markding on 2015/11/27.
 */
public interface LoginCallBack {

    /**
     * 登录成功回调
     * */
    public void onSuccess(LoginSession session);

    /**
     * 登录失败回调
     * */
    public void onFailure(ErrorCode errCode, String msg);

}
