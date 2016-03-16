package com.example.mydemo.utils;

import com.example.mydemo.c2c.UserInfo;
import com.tencent.TIMConversationType;
import com.tencent.TIMElemType;
import com.tencent.TIMMessage;

public class RecentEntity extends UserInfo {
	
	private TIMMessage message;
	private String msg;
	private String time;	
	private long count;
	private boolean bGroupMsg;
	//private TIMConversationType type;
	

	public RecentEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public void setMessage(TIMMessage message){
		this.message = message;
	}
	
	public TIMMessage getMessage(){
		return message;
	}
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public long getCount() {
		return count;
	}
	
	public void setCount(long l) {
		this.count = l;
	}
	
//	public TIMConversationType getType() {
//		return type;
//	}
//	
//	public void setType(TIMConversationType type) {
//		this.type = type;
//	}	
	
	public boolean getIsGroupMsg() {
		return bGroupMsg;
	}

	public void setIsGroupMsg(boolean bGroupMsg) {
		this.bGroupMsg = bGroupMsg;
	}
	
}
