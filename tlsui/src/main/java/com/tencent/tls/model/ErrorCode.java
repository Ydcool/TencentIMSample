package com.tencent.tls.model;

/**
 * Created by markding on 2015/11/30.
 */
public enum ErrorCode {
    UNKNOWN,                    // 未知错误
    TIMEOUT,                    // 网络超时
    AUTHORIZATION_FAIL,         // 授权失败
    AUTHORIZATION_CANCEL,       // 取消授权
    EXCEPTION,                  // 发生异常
    OTHERERROR,                 // 其他错误
}
