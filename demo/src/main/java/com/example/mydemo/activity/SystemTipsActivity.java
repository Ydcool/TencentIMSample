package com.example.mydemo.activity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.mydemo.MyApplication;
import  com.example.mydemo.R;
import com.example.mydemo.adapter.GroupListAdapter;
import com.example.mydemo.adapter.GroupMemberOperaAdapter;
import com.example.mydemo.adapter.NewFriendsAdapter;
import com.example.mydemo.adapter.GroupMemberOperaAdapter.ViewHolder;
import com.example.mydemo.adapter.SystemTipsAdapter;
import com.example.mydemo.c2c.UserInfo;
import com.example.mydemo.c2c.UserInfoManager;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.example.mydemo.utils.Constant;
import com.example.mydemo.utils.SNSChangeEntity;

import android.app.ActivityManager;
import android.app.AlertDialog; 
import android.app.Dialog; 
import android.content.ComponentName;
import android.content.DialogInterface; 
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupTipsElem;
import com.tencent.TIMGroupTipsType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMSNSChangeInfo;
import com.tencent.TIMSNSSystemElem;
import com.tencent.TIMSNSSystemType;
import com.tencent.TIMValueCallBack;


 public class SystemTipsActivity  extends MyBaseActivity {
	private final static String TAG = SystemTipsActivity.class.getSimpleName();
	private  List<SNSChangeEntity> mListNewFriend;
	private ListView mLVNewFriend;
	private Button mBtnCreateGroup;
	public SystemTipsAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_friends);	
		initView();	
		 TIMManager.getInstance().addMessageListener(msgListener);
	}
	
	
	public void initView() {	
	//	mBtnCreateGroup = (Button) findViewById(R.id.btn_goto_create_group);
		mLVNewFriend= (ListView)findViewById(R.id.lv_new_friends);	
		mListNewFriend = new ArrayList<SNSChangeEntity >();
		mAdapter = new SystemTipsAdapter(getBaseContext(),mListNewFriend);
		mLVNewFriend.setAdapter(mAdapter);		
		getMessage();
		
	
		Button btAddFriend = (Button) findViewById(R.id.btn_goto_add_friend);
		btAddFriend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		//		if(MyApplication.getInstance().bHostRelaytionShip){
					startActivity(new Intent(SystemTipsActivity.this, AddFriendNewActivity.class));					
//				}else{
//					startActivity(new Intent(SystemTipsActivity.this, AddFriendActivity.class));
//				}
			}
		});		
		
		
		mLVNewFriend.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				return true;
			}
		});			
	
	}	
	
	private void getMessage(){
		
		long conversation_num = TIMManager.getInstance().getConversationCount();		
		Log.d(TAG,"load notify tips begin:" + conversation_num);
		mListNewFriend.clear();
		
		for(long i = 0; i < conversation_num; ++i) 	{			
			final TIMConversation conversation = TIMManager.getInstance().getConversationByIndex(i);
			Log.d(TAG,"loadRecentContent:" + i + ":" + conversation.getType());
			if(conversation.getType() == TIMConversationType.System){
				conversation.getMessage(50, null, new TIMValueCallBack<List<TIMMessage>>() {
	            @Override
	            public void onError(int code, String desc) {
	                Log.e(TAG, "get msgs failed");
	            }

	            @Override
	            public void onSuccess(List<TIMMessage> msgs) {
	            	Log.d(TAG,"system num:" + msgs.size());
	            	if(msgs.size()<1){
	            		return;
	            	}       
	            
	            	conversation.setReadMessage(msgs.get(0));
	            	UserInfoManagerNew.getInstance().setUnReadSystem(0);            		
	            	for(TIMMessage msg: msgs){            			
            			for(int i=0;i<msg.getElementCount();i++){								
							TIMElem elem = msg.getElement(i);
							Log.d(TAG,"system:" + elem.getType()  + ":" + mListNewFriend.size());
							if(elem.getType() == TIMElemType.SNSTips){
								TIMSNSSystemElem  tipsElem = (TIMSNSSystemElem )elem;								
								for(TIMSNSChangeInfo tmp :tipsElem.getChangeInfoList()){
									Log.d(TAG,"add friend req:"+ msg.isSelf() + ":"+ tipsElem.getSubType() + "id:" + tmp.getIdentifier() + ":source:" + tmp.getSource());
									//删除的消息通知不需要展现
									//为了多终端同步的问题，自己发送的请求也会自己收到通知，这里简单的过滤
									if(tipsElem.getSubType() == TIMSNSSystemType.TIM_SNS_SYSTEM_DEL_FRIEND_REQ 
											//|| tipsElem.getSubType() == TIMSNSSystemType.TIM_SNS_SYSTEM_DEL_FRIEND
											 || (tipsElem.getSubType() == TIMSNSSystemType.TIM_SNS_SYSTEM_ADD_FRIEND_REQ && msg.isSelf())) {
											continue;									
									}
									SNSChangeEntity entity = new SNSChangeEntity();
									entity.setMessage(tmp);
									entity.setType(elem.getType());
									entity.setSubType(tipsElem.getSubType());								
									Log.d(TAG,"mListNewFriend:"+ tipsElem.getSubType() + "id:" + tmp.getIdentifier() + ":source:" + tmp.getSource());
									mListNewFriend.add(entity);
								}
								
							}else if(elem.getType() == TIMElemType.GroupSystem){
								TIMGroupSystemElem tipsElem = (TIMGroupSystemElem) elem;
								SNSChangeEntity entity = new SNSChangeEntity();
								entity.setGroupMessage(tipsElem);
								entity.setType(TIMElemType.GroupSystem);								
								Log.d(TAG,"group system:"+ tipsElem.getSubtype() + "id:" + tipsElem.getOpUser() + ":source:" + tipsElem.getOpReason());
								mListNewFriend.add(entity);	
							}
						}
            		}
            		Log.d(TAG,"mListNewFriend size:" + mListNewFriend.size());
            	  	mAdapter.notifyDataSetChanged();
	            }
	        });	
	        break;
		}	
	}
}

	private boolean isTopActivity()  
    {       
		boolean isTop = false;
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);  
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;          
        if (cn.getClassName().contains(TAG)){  
        	isTop = true;  
        }  
        Log.d(TAG,"is Top Activity:" + isTop);
        return isTop;  
    }  
	
	private TIMMessageListener msgListener = new TIMMessageListener() {
		
		@Override
		public boolean onNewMessages(List<TIMMessage> arg0) {
			// TODO Auto-generated method stub
			Log.d(TAG,"new messge listnener:" + arg0.size());
			if(isTopActivity()){
				for(TIMMessage msg: arg0){		
					Log.d(TAG,"msg:" + msg.getElementCount() + ":" + msg.getConversation().getType());		
						for(int i=0;i<msg.getElementCount();i++){								
							TIMElem elem = msg.getElement((int)i);
								Log.d(TAG,"msg type:" + elem.getType());
							if(elem.getType() == TIMElemType.SNSTips){
								
								TIMSNSSystemElem snsElem = (TIMSNSSystemElem) elem;
								Log.d(TAG,"snsn tips type:" +snsElem.getSubType());
								Toast.makeText(getBaseContext(), "snsn tips type:" +snsElem.getSubType(), Toast.LENGTH_LONG).show();
								//后台数据更新统一放到mainacitivity
//								if(snsElem.getSubType() == TIMSNSSystemType.TIM_SNS_SYSTEM_ADD_FRIEND || snsElem.getSubType() == TIMSNSSystemType.TIM_SNS_SYSTEM_DEL_FRIEND){
//									UserInfoManagerNew.getInstance().getContactsListFromServer();
//								}								
								getMessage();
								break;
							}			
						
					}
				}
			}
			return false;
		}
     
   }; 
	@Override
	public void onResume() {		
		super.onResume();			
		Log.d(TAG,"activity resume ,refresh list");
		loadNewFriends();		
	}	
	
	private void loadNewFriends(){
		
	}
	
	public void onBack(View view){	
		finish();
	}
	
	@Override
	public void onDestroy() {
		TIMManager.getInstance().removeMessageListener(msgListener);
		super.onDestroy();
	}	
	
}
