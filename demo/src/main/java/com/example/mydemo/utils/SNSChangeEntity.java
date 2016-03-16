package com.example.mydemo.utils;


import com.tencent.TIMElemType;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMSNSChangeInfo;
import com.tencent.TIMSNSSystemType;

public class SNSChangeEntity {
	
	private TIMSNSChangeInfo message;
	private TIMGroupSystemElem groupSystem;
	private TIMElemType type;
	private TIMSNSSystemType subType;	
	private boolean isSelf;
	

	public SNSChangeEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public void setMessage(TIMSNSChangeInfo message){
		this.message = message;
	}
	
	public TIMSNSChangeInfo getMessage(){
		return message;
	}

	public void setGroupMessage(TIMGroupSystemElem groupSystem){
		this.groupSystem = groupSystem;
	}
	
	public TIMGroupSystemElem getGroupMessage(){
		return groupSystem;
	}	

	public void setSubType(TIMSNSSystemType subType) {
		this.subType = subType;
	}

	public TIMSNSSystemType getSubType() {
		return subType;
	}
	
	public void setType(TIMElemType type) {
		this.type = type;
	}

	public TIMElemType getType() {
		return type;
	}	
	public void setIsSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}

	public boolean getIsSelf() {
		return isSelf;
	}	
	
}
