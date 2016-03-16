package com.example.mydemo.data;

import com.tencent.TIMLogLevel;

import android.content.Context;
import android.util.Log;

public class IMData {
	private String KEY_SETTING_NOTIFICATION = "key_setting_notification";
	private String KEY_SETTING_SOUND = "key_setting_sound";
	private String KEY_SETTING_VIBRATE = "key_setting_vibrate";
	private String KEY_SETTING_TESTENV = "key_setting_testenv";
	private String KEY_SETTING_LOGCONSOLE = "key_setting_logconsole";
	private String KEY_LOG_LEVEL = "key_setting_loglevel";
	private String KEY_USER_NAME = "key_username";
	private String KEY_PASSWORD = "key_pwd";
	public IMData(Context context){
		SharedPreferencesHelper.init(context);
	}
	
	
    public void setSettingNotification(boolean bFlag) {        	
    	SharedPreferencesHelper.getInstance().saveData(KEY_SETTING_NOTIFICATION, bFlag);
    }

   
    public boolean getSettingNotification() {
    	return (Boolean) SharedPreferencesHelper.getInstance().getData(KEY_SETTING_NOTIFICATION, false);
    }

    public void setSettingSound(boolean bFlag) {
     
    	SharedPreferencesHelper.getInstance().saveData(KEY_SETTING_SOUND, bFlag);
    }

    public boolean getSettingSound() {
    	return (Boolean)SharedPreferencesHelper.getInstance().getData(KEY_SETTING_SOUND, false);
    }

  
    public void setSettingVibrate(boolean bFlag) {
      
    	SharedPreferencesHelper.getInstance().saveData(KEY_SETTING_VIBRATE, bFlag);
    }


    public boolean getSettingVibrate() {   
    	   return (Boolean)SharedPreferencesHelper.getInstance().getData(KEY_SETTING_VIBRATE, false);
    }

  
    public void setUserName(String name) {
    	SharedPreferencesHelper.getInstance().saveData(KEY_USER_NAME, name);
    }


    public String getUserName() {
    	return (String) SharedPreferencesHelper.getInstance().getData(KEY_USER_NAME, "");
    }

  
    public void setPassword(String pwd) {
    	SharedPreferencesHelper.getInstance().saveData(KEY_PASSWORD, pwd); 
    }

    public String getPassword() {
    	return (String) SharedPreferencesHelper.getInstance().getData(KEY_PASSWORD, "");
    }
	
	
    public void setTestEnvSetting(boolean bFlag){
    	Log.d("imdata","set testenv:" +bFlag );
    	SharedPreferencesHelper.getInstance().saveData(KEY_SETTING_TESTENV, bFlag);
    }

    public boolean getTestEnvSetting() {   
 	   return (Boolean)SharedPreferencesHelper.getInstance().getData(KEY_SETTING_TESTENV, false);
    }
    
    public void setLogConsole(boolean bFlag){
    	Log.d("imdata","set testenv:" +bFlag );
    	SharedPreferencesHelper.getInstance().saveData(KEY_SETTING_LOGCONSOLE, bFlag);
    }

    public boolean getLogConsole() {   
 	   return (Boolean)SharedPreferencesHelper.getInstance().getData(KEY_SETTING_LOGCONSOLE, true);
    }    
    
    public void setLogLevel(int level){
    	Log.d("imdata","log level:" + level);
    	SharedPreferencesHelper.getInstance().saveData(KEY_LOG_LEVEL, level);
    }

    public int getLogLevel() {
    	//Log.d("imdata","log level:" + (TIMLogLevel)SharedPreferencesHelper.getInstance().getData(KEY_LOG_LEVEL,TIMLogLevel.INFO));
 	   return (Integer)SharedPreferencesHelper.getInstance().getData(KEY_LOG_LEVEL,4);
    }    
}
