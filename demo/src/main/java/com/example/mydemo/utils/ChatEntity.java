package com.example.mydemo.utils;

import com.tencent.TIMConversationType;
import com.tencent.TIMElem;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageStatus;

public class ChatEntity {
	
	private TIMElem elem;
	private TIMConversationType type;
	private String grpSendName;
	private long time;	
	private boolean bSelf;
	private TIMMessageStatus status;
	//为了删除功能，加上message
	private TIMMessage message;

	public ChatEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public void setElem(TIMElem elem){
		this.elem = elem;
	}
	
	public TIMElem getElem(){
		return elem;
	}
	
	public void setMessage(TIMMessage message){
		this.message = message;
	}
	
	public TIMMessage getMessage(){
		return message;
	}
		
	public void setType(TIMConversationType type) {
		this.type = type;
	}
	
	public TIMConversationType getType() {
		return type;
	}

	public String getSenderName() {
		return grpSendName;
	}

	public void setSenderName(String grpSendName) {
		this.grpSendName = grpSendName;
	}
		
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean getIsSelf() {
		return bSelf;
	}
	
	public void setIsSelf(boolean bSelf) {
		this.bSelf = bSelf;
	}
	
	public void setStatus(TIMMessageStatus status) {
		this.status = status;
	}
	
	public TIMMessageStatus getStatus() {
		return status;
	}	
	
}
