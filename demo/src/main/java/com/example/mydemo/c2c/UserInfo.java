package com.example.mydemo.c2c;

import java.io.IOException;

import android.text.TextUtils;
import android.util.Log;

//import com.example.mydemo.utils.Constant;
//import com.example.mydemo.utils.PingYinUtil;
import com.example.mydemo.utils.ChnToSpell;



public class UserInfo {

//	private int id;
	private String name = null;
	private String nick = null;	
	private int avatar;
	private String header;
	private long unReadNum;
	//临时测试用
    public String openId;
    public String openKey;
    

	public UserInfo() {
		// TODO Auto-generated constructor stub
	}

	public UserInfo(int id, int avatar, int count, String name,
			String time, String msg,String header) {
		super();
	//	this.id = id;
		this.avatar = avatar;
		this.name = name;
		this.header = header;

	}


	public String getDisplayUserName(){
		return nick!=null?nick:name;
	}
	
	public int getavatar() {
		return avatar;
	}
	
	public void setavatar(int avatar) {
		this.avatar = avatar;
	}
	
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	//	ProcessHeader();
	}
	
	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}	


	public long getUnRead() {
		return unReadNum;
	}

	public void setUnRead(long unReadNum) {
		this.unReadNum = unReadNum;
	}	
	public boolean equals(Object object) {
		if (object == null)
			return false;

		if (object == this)
			return true;

		if (object instanceof UserInfo) {
			UserInfo entity = (UserInfo) object;
			if (entity.name == this.name)
				return true;
		}
		return false;
	}

	public void ProcessHeader() {
		String headerName = null;
		if (!TextUtils.isEmpty(nick)) {
			headerName = nick;
		} else {
			headerName = name;
		}
		if (Character.isDigit(headerName.charAt(0))) {
			header = "#";
		} else {
			try{
				String tmpStr = ChnToSpell.MakeSpellCode(headerName.substring(0, 1), ChnToSpell.TRANS_MODE_PINYIN_INITIAL);
				header = tmpStr.substring(0,1).toUpperCase();
				char tmpHeader = header.toLowerCase().charAt(0);
				if (tmpHeader < 'a' || tmpHeader > 'z') {
					header = "#";
				}
			}
			catch(Exception e){
				Log.e("UserInfo",headerName + ":" + e.getMessage());
				header = "#";
			}
		}
	}	
	
	@Override
	public String toString() {
		return "UserInfo [id=" + name + ", avatar=" + avatar + ", " +
				" name=" + nick + "]";
	}

}
