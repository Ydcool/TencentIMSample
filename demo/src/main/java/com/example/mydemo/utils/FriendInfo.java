package com.example.mydemo.utils;


public class FriendInfo  {
	private String id;
	private String name;
	private boolean needVerify; 

	public FriendInfo() {
		// TODO Auto-generated constructor stub
	}

	public void setID(String id){
		this.id = id;
	}
	
	public String getID(){
		return id;
	}	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	
	public void setNeedVerify(boolean needVerify){
		this.needVerify = needVerify;
	}
	
	public boolean getNeddVerify(){
		return needVerify;
	}		
	
}
