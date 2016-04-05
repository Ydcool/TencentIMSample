package com.example.mydemo;

import android.content.Context;
import android.util.Log;

import com.example.mydemo.utils.Constant;
import com.tencent.tls.service.TLSConfiguration;
import com.tencent.tls.service.TLSService;

public class TLSHelper {
    private static final String TAG = TLSHelper.class.getSimpleName();

    Context context;

    public static String userID = null;
    //	public static TLSLoginHelper loginHelper;
//	public static TLSAccountHelper accountHelper;
    static int LANG = 2052;
    static String appVer = "1.0";
    static int country = 86;
    static boolean NoPwdReg = false;
    public final static String PREFIX = "86-";
    public static TLSService tlsService;

    public synchronized boolean init(Context context) {

        this.context = context;
        InitTLSSDK();
        return true;
    }

    private void InitTLSSDK() {

        // 设置使用TLS SDK所需的配置信息

//      TLSConfiguration.setAccountType(Constant.ACCOUNT_TYPE);           // 必须项, accountType
//      TLSConfiguration.setAppVersion(appVer);          // 可选项, 表示app当前版本, 默认为1.0
        TLSConfiguration.setSdkAppid(Constant.SDK_APPID);        // 必须项, sdkAppid
        TLSConfiguration.setTimeout(8000);              // 可选项, 表示网络操作超时时间, 默认为8s//        
        tlsService = TLSService.getInstance();

        // 设置QQ APP_ID和APP_KEY
        //     TLSConfiguration.setQqAppIdAndAppKey("1104701569", "CXtj4p63eTEB2gSu");
        TLSConfiguration.setQqAppIdAndAppKey("222222", "CXtj4p63eTEB2gSu");
        // 设置微信APP_ID和APP_SECRET   
        TLSConfiguration.setWxAppIdAndAppSecret("wx65f71c2ea2b122da", "1d30d40f8db6d3ad0ee6492e62ad5d57");
        // 设置新浪微博        
        TLSConfiguration.setSinaWeiboAppIdAndAppSecret("1119045858", "9e6828b25ca69debcf2b7d14123a7ba8");

        userID = tlsService.getLastUserIdentifier();
//       TLSUserInfo userInfo = tlsService.getLastUserInfo();
//       if(userInfo!=null){
        Log.d("test", "last user:" + userID);
        //}
        //boolean hasLogin =  userInfo != null && !tlsHelper.needLogin(userInfo.identifier);

    }


}
