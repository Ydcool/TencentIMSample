package com.example.mydemo;

import android.content.Context;
import android.util.Log;

import com.example.mydemo.utils.Constant;
import com.tencent.TIMConnListener;
import com.tencent.TIMLogLevel;
import com.tencent.TIMManager;

public class SDKHelper {
    private static final String TAG = SDKHelper.class.getSimpleName();
    boolean bSDKInited = false;
    boolean isClientStart = true;
    Context context;

    public final static int LOG_OFF = 1;
    public final static int LOG_ERROR = 2;
    public final static int LOG_WARN = 3;
    public final static int LOG_INFO = 4;
    public final static int LOG_DEBUG = 5;

    public synchronized boolean init(Context context) {
        if (bSDKInited) {
            return true;
        }
        this.context = context;
        InitTIMSDK();
        //  imdata	= new IMData(context);

        bSDKInited = true;
        return true;
    }

    public boolean getClientStarted() {
        return isClientStart;
    }


    private void InitTIMSDK() {
        //设置log级别
        TIMManager.getInstance().setLogPrintEanble(MyApplication.getInstance().getLogConsole());
        int loglevel = MyApplication.getInstance().getLogLevel();
        TIMLogLevel timLevel = TIMLogLevel.DEBUG;
        if (loglevel == LOG_OFF) {
            timLevel = TIMLogLevel.OFF;
        } else if (loglevel == LOG_ERROR) {
            timLevel = TIMLogLevel.ERROR;
        } else if (loglevel == LOG_WARN) {
            timLevel = TIMLogLevel.WARN;
        } else if (loglevel == LOG_INFO) {
            timLevel = TIMLogLevel.INFO;
        } else if (loglevel == LOG_DEBUG) {
            timLevel = TIMLogLevel.DEBUG;
        }
        Log.d(TAG, "set log level:" + timLevel);
        TIMManager.getInstance().setLogLevel(timLevel);

        if (MyApplication.getInstance().getTestEnvSetting()) {
            Log.e(TAG, "set test env");
            TIMManager.getInstance().setEnv(1);
        }
        TIMManager.getInstance().init(context, Constant.SDK_APPID, String.valueOf(Constant.ACCOUNT_TYPE));
        SetConnCallBack();

//        TIMManager.getInstance().setRefreshLiistener(new TIMRefreshListener(){
//
//			@Override
//			public void onRefresh() {
//				// TODO Auto-generated method stub
//				
//			}
//        	
//        });
    }

    private void SetConnCallBack() {

        //设置网络连接监听器，连接建立／断开时回调wsd 
        TIMManager.getInstance().setConnectionListener(new TIMConnListener() {
            @Override
            public void onConnected() {
                Log.e(TAG, "connected");
                isClientStart = true;
            }

            @Override
            public void onDisconnected(int code, String desc) {
                //接口返回了错误码code和错误描述desc，可用于定位连接断开原因
                Log.e(TAG, "disconnected");
            }

            @Override
            public void onWifiNeedAuth(String arg0) {
                // TODO Auto-generated method stub

            }
        });
    }


}
