package com.tencent.tls.callback;

/**
 * Created by markding on 2015/11/27.
 */
public interface RegisterCallBack {

    /**
     * 注册成功回调
     * */
    public void onSuccess();

    /**
     * 注册失败回调
     * */
    public void onFailure();

}
