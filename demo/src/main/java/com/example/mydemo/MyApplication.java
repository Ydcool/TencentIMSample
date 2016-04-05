package com.example.mydemo;

import android.app.Activity;

import com.example.mydemo.data.IMData;
import com.tencent.TIMBaseApplication;
import com.tencent.TIMManager;
import com.tencent.TIMOfflinePushListener;
import com.tencent.TIMOfflinePushNotification;
import com.tencent.qalsdk.sdk.MsfSdkUtils;
import com.tencent.qalsdk.util.QLog;

public class MyApplication extends TIMBaseApplication {
    private static final String TAG = SDKHelper.class.getSimpleName();
    private static MyApplication instance;
    private boolean isBackground;

    private IMData imdata = null;
    private String userName = null;
    private String userSig = null;
    public boolean bHostRelaytionShip = true;
    public Activity currentActivity = null;
    private boolean bThirdIdLogin = false;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        imdata = new IMData(this);
        QLog.d(TAG, "app onCreate");
        if (MsfSdkUtils.isMainProcess(this)) {
            QLog.d(TAG, "main proccess onCreate");
            TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {

                @Override
                public void handleNotification(TIMOfflinePushNotification arg0) {
                    // TODO Auto-generated method stub
                    QLog.d(TAG, "offlinemsg:" + arg0.getTitle() + ":" + arg0.getContent());
                    arg0.doNotify(MyApplication.this, R.drawable.ic_launcher);
                }

            });
        }
    }


    public static MyApplication getInstance() {
        return instance;
    }


    public String getUserName() {
        if (userName == null) {
            return imdata.getUserName();
        }
        return userName;
    }

    public void setThirdIdLogin(boolean bThirdId) {
        bThirdIdLogin = bThirdId;
    }

    public boolean getThirdIdLogin() {
        return bThirdIdLogin;
    }

    public void setUserName(String userName) {
        imdata.setUserName(userName);
        this.userName = userName;
    }

    public String getUserSig() {
        if (userSig == null) {
            return imdata.getPassword();
        }
        return userName;
    }

    public void setUserSig(String userSig) {
        imdata.setPassword(userSig);
        this.userSig = userSig;
    }

    public boolean getSettingNotification() {
        return imdata.getSettingNotification();
    }

    public void setSettingNotification(boolean bFlag) {
        imdata.setSettingNotification(bFlag);
    }

    public boolean getSettingSound() {
        return imdata.getSettingSound();
    }

    public void setSettingSound(boolean bFlag) {
        imdata.setSettingSound(bFlag);
    }

    public boolean getSettingVibrate() {
        return imdata.getSettingVibrate();
    }

    public void setSettingVibrate(boolean bFlag) {
        imdata.setSettingVibrate(bFlag);
    }

    public boolean getIsBackground() {
        return isBackground;
    }

    public void setTestEnv(boolean bFlag) {
        imdata.setTestEnvSetting(bFlag);
    }

    public boolean getTestEnvSetting() {
        return imdata.getTestEnvSetting();
    }

    public void setLogLevel(int level) {
        imdata.setLogLevel(level);
    }

    public int getLogLevel() {
        return imdata.getLogLevel();
    }

    public void setLogConsole(boolean bFlag) {
        imdata.setLogConsole(bFlag);
    }

    public boolean getLogConsole() {
        return imdata.getLogConsole();
    }

}
