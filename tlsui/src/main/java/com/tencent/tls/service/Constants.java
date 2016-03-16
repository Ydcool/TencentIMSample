package com.tencent.tls.service;

/**
 * Created by dgy on 15/8/13.
 */
public class Constants {

    // 自动填写短信验证的相关常量
    public final static String SMS_INBOX_URI = "content://sms/inbox";
    public final static String SMS_LOGIN_SENDER             = "10655020069275";
    public final static String SMS_REGISTER_SENDER          = "10655020051075";
    public final static String PHONEPWD_RESET_SENDER        = "10655020051075";
    public final static String PHONEPWD_REGISTER_SENDER     = "10655020051075";

    public final static int    CHECKCODE_LENGTH = 4;

    // 配置信息
    public final static String TLS_SETTING = "tencent.tls.ui.TLS_SETTING"; // 配置名
    public final static String SETTING_LOGIN_WAY = "tencent.tls.ui.SETTING_LOGIN_WAY"; // Key

    // 定义业务数据键值
    public final static String COUNTRY_NAME = "tencent.tls.ui.COUNTRY_NAME";
    public final static String COUNTRY_CODE = "tencent.tls.ui.COUNTRY_CODE";
    public final static String PHONE_NUMBER = "tencent.tls.ui.PHONE_NUMBER";
    public final static String USERNAME = "tencent.tls.ui.USERNAME";
    public final static String PASSWORD = "tencent.tls.ui.PASSWORD";

    // 登录方式
    public final static String EXTRA_LOGIN_WAY = "com.tencent.tls.LOGIN_WAY";
    public final static int NON_LOGIN = 0;
    public final static int PHONE_SMS_LOGIN = (1 << 0);
    public final static int QQ_LOGIN = (1 << 1);
    public final static int WX_LOGIN = (1 << 2);
    public final static int SINA_WEIBO_LOGIN = (1 << 3);
    public final static int STR_ACC_LOGIN = (1 << 4);
    public final static int PHONE_PWD_LOGIN = (1 << 5);
    public final static int GUEST_LOGIN = (1 << 6);

    public final static int SMS_REG = PHONE_SMS_LOGIN + 1;
    public final static int PHONE_PWD_REG = PHONE_PWD_LOGIN + 1;
    public final static int PHONE_PWD_RESET = PHONE_PWD_LOGIN + 2;

    // 短信注册的相关常量
    public final static int SMS_REG_REQUEST_CODE = 0;
    public final static String EXTRA_SMS_REG = "com.tencent.tls.SMS_REG";
    public final static int SMS_REG_FAIL = 0;
    public final static int SMS_REG_SUCCESS = 1;
    public final static int SMS_REG_NON = 2;


    // 短信登录的相关常量
    public final static String EXTRA_MOBILE = "com.tencent.tls.MOBILE";
    public final static String EXTRA_SMS_LOGIN = "com.tencent.tls.SMS_LOGIN";
    public final static int SMS_LOGIN_FAIL = 0;
    public final static int SMS_LOGIN_SUCCESS = 1;
    public final static int SMS_LOGIN_NON = 2;

    // QQ相关的常量
    public final static String EXTRA_QQ_LOGIN = "com.tencent.tls.QQ_LOGIN";
    public final static int QQ_LOGIN_FAIL = 0;
    public final static int QQ_LOGIN_SUCCESS = 1;
    public final static int QQ_LOGIN_NON = 2;
    public final static String EXTRA_QQ_OPENID = "com.tencent.tls.QQ_OPENID";
    public final static String EXTRA_QQ_ACCESS_TOKEN = "com.tencent.tls.QQ_ACCESS_TOKEN";

    // 微信相关的常量
    public final static String EXTRA_WX_LOGIN = "com.tencent.tls.WX_LOGIN";
    public final static int WX_LOGIN_FAIL = 0;
    public final static int WX_LOGIN_SUCCESS = 1;
    public final static int WX_LOGIN_NON = 2;
    public final static String EXTRA_WX_OPENID = "com.tencent.tls.WX_OPENID";
    public final static String EXTRA_WX_ACCESS_TOKEN = "com.tencent.tls.WX_ACCESS_TOKEN";

    // 新浪微浪相关常量
    public final static String EXTRA_SINA_WEIBO_LOGIN = "com.tencent.tls.SINA_WEIBO_LOGIN";
    public final static int SINA_WEIBO_LOGIN_FAIL = 0;
    public final static int SINA_WEIBO_LOGIN_SUCCESS = 1;
    public final static int SINA_WEIBO_LOGIN_NON = 2;
    public final static String EXTRA_SINA_WEIBO_OPENID = "com.tencent.tls.SINA_WEIBO_OPENID";
    public final static String EXTRA_SINA_WEIBO_ACCESS_TOKEN = "com.tencent.tls.SINA_WEIBO_ACCESS_TOKEN";

    // 账号密码注册相关常量
    public final static int USRPWD_REG_REQUEST_CODE = 1;
    public final static String EXTRA_USRPWD_REG = "com.tencent.tls.SMS_REG";
    public final static int USRPWD_REG_FAIL = 0;
    public final static int USRPWD_REG_SUCCESS = 1;
    public final static int USRPWD_REG_NON = 2;

    // 账号密码登录的相关常量
    public final static String EXTRA_USRPWD_LOGIN = "com.tencent.tls.USRPWD_LOGIN";
    public final static int USRPWD_LOGIN_FAIL = 0;
    public final static int USRPWD_LOGIN_SUCCESS = 1;
    public final static int USRPWD_LOGIN_NON = 2;

    // 手机密码注册的相关常量
    public final static String EXTRA_PHONEPWD_REG_RST = "com.tencent.tls.PHONEPWD_REG_RST";
    public final static int PHONEPWD_NON = 0;
    public final static int PHONEPWD_REGISTER = 1;
    public final static int PHONEPWD_RESET = 2;
    public final static int PHONEPWD_REG_REQUEST_CODE = 2;

    // 手机密码重置的相关常量
    public final static int PHONEPWD_RESET_REQUEST_CODE = 3;

    // 手机密码登录的相关常量
    public final static String EXTRA_PHONEPWD_LOGIN = "com.tencent.tls.EXTRA_PHONEPWD_LOGIN";
    public final static int PHONEPWD_LOGIN_FAIL = 0;
    public final static int PHONEPWD_LOGIN_SUCCESS = 1;
    public final static int PHONEPWD_LOGIN_NON = 2;

    public final static String EXTRA_IMG_CHECKCODE = "com.tencent.tls.EXTRA_IMG_CHECKCODE";

    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     *
     * <p>
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响，
     * 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     *
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     *
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     *
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String SINA_WEIBO_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    public static final String QQ_SCOPE = "all"; // 所有权限
    public static final String WX_SCOPE = "snsapi_userinfo";

}
