package com.example.mydemo.c2c;

import java.util.HashMap;
import java.util.Map;


public class UserInfoManager {
	private  Map<String,UserInfo> mContactsList;
	private Map<String,String> mGroupID2Name;
	private static UserInfoManager instance;	
	
	UserInfoManager()
	{
		mContactsList = new HashMap<String,UserInfo>();
	}
	
	public static UserInfoManager getInstance()
	{
		if(instance == null){
			instance = new UserInfoManager();
		}
		return instance;
	}
	
	
	public Map<String,UserInfo> getContactsList() {
		return mContactsList;
	}

	public void setGroupID2Name(Map<String,String> mGroupID2Name) {
		 this.mGroupID2Name = mGroupID2Name;
	}		
	public Map<String,String> getGroupID2Name() {
		return mGroupID2Name;
	}	
}
