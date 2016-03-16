package com.tencent.tls.service;

/**
 * Created by dgy on 15/7/20.
 * Description: 该类用于保存TLS SDK、QQ SDK、WeChat SDK相关的配置信息
 */
public class TLSConfiguration {

    // TLS SDK
    public static long SDK_APPID = -1;
    public static int ACCOUNT_TYPE = -1;
    public static String COUNTRY_CODE = "86";
    public static int LANGUAGE_CODE = 2052;
    public static int TIMEOUT = 8000;
    public static String APP_VERSION = "1.0";     // 指的是TLSDemo的版本号，而不是TLSSDK的版本号

    // QQ SDK
    public static String QQ_APP_ID;
    public static String QQ_APP_KEY;

    // WeChat SDK
    public static String WX_APP_ID;
    public static String WX_APP_SECRET;

    // Sina WeiBo
    public static String SINA_WEIBO_APP_ID;
    public static String SINA_WEIBO_APP_SECRET;


    public static void setSdkAppid(long sdkAppid) {
        SDK_APPID = sdkAppid;
    }

    public static void setQqAppIdAndAppKey(String qqAppId, String qqAppKey) {
        QQ_APP_ID = qqAppId;
        QQ_APP_KEY = qqAppKey;
    }

    public static void setWxAppIdAndAppSecret(String wxAppId, String wxAppSecret) {
        WX_APP_ID = wxAppId;
        WX_APP_SECRET = wxAppSecret;
    }

    public static void setSinaWeiboAppIdAndAppSecret(String appid, String appSecret) {
        SINA_WEIBO_APP_ID = appid;
        SINA_WEIBO_APP_SECRET = appSecret;
    }

    public static void setAccountType(int accountType) {
        ACCOUNT_TYPE = accountType;
    }

    public static void setAppVersion(String appVersion) {
        APP_VERSION = appVersion;
    }

    public static void setTimeout(int timeout) {
        TIMEOUT = timeout;
    }

    public TLSConfiguration(){}
}

/**
 * eclipse碰到NoClassDefFoundError错误的解决方案
 * 1 更新sdk和adt
 * 2 检查文件夹名是否为libs
 * 3 Project -> Properties -> Java Build Path -> Order & Export中勾选相关的包，顺序检查，将gen放到src前面
 * 4 Project -> Properties -> Java Build Path -> source 中加入libs文件夹
 * 5 clean工程
 * 或者
 * 重命名tlsdemo.jar为tls_demo.jar
 * 或者
 * 推荐使用Android Studio
 * */