package com.example.mydemo.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.mydemo.MyApplication;
import  com.example.mydemo.R;
import com.example.mydemo.adapter.GroupMemberAdapter;
import com.example.mydemo.c2c.UserInfoManager;
import com.example.mydemo.c2c.UserInfoManagerNew;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.mydemo.utils.Constant;
import com.example.mydemo.view.MyGridView;
import com.tencent.TIMAddFriendRequest;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.TIMDelFriendType;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMManager;
import com.tencent.TIMValueCallBack;


 public class UserInfoActivity  extends MyBaseActivity {
	private final static String TAG = UserInfoActivity.class.getSimpleName();

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);		

		initView();	
	}
	
	public void initView() {	
		 
		 TextView tvUserName = (TextView) findViewById(R.id.tv_name);	
		 TextView tvNickName = (TextView) findViewById(R.id.tv_nick_name);
		 	 
		 
		 final String userID = getIntent().getStringExtra("userID");
		 com.example.mydemo.c2c.UserInfo userInfo = UserInfoManagerNew.getInstance().getContactsList().get(userID);
		 if(userInfo!=null){
			 tvUserName.setText(userInfo.getNick());
		 }
		 tvNickName.setText(userID);	
		 		 
		 final ToggleButton tbBlack =(ToggleButton) findViewById(R.id.tb_set_black);
		 tbBlack.setChecked(UserInfoManagerNew.getInstance().getBlackList().contains(userID));
		//	tbNotify.setChecked(MyApplication.getInstance().getSettingNotification());
		 
		 
		 tbBlack.setOnCheckedChangeListener(new OnCheckedChangeListener(){         
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
						tbBlack.setChecked(isChecked);
					
						List<String> bList = new ArrayList<String>();
						bList.add(userID);
						if(isChecked){
							TIMFriendshipManager.getInstance().addBlackList(bList, new TIMValueCallBack<List<TIMFriendResult>> (){
	
								@Override
								public void onError(int arg0, String arg1) {
									// TODO Auto-generated method stub
									Log.e(TAG,"addBlackList error:" + arg0 + ":" + arg1);
								}
	
								@Override
								public void onSuccess(List<TIMFriendResult> arg0) {
									// TODO Auto-generated method stub
									Log.d(TAG,"addBlackList succ:" + arg0.size());
									List<String> mBlackList = UserInfoManagerNew.getInstance().getBlackList();
									for(TIMFriendResult arg : arg0){
										mBlackList.add(arg.getIdentifer());
										UserInfoManagerNew.getInstance().getContactsList().remove(arg.getIdentifer());
									}
																		
								}
								
							});
						}else{
							TIMFriendshipManager.getInstance().delBlackList(bList, new TIMValueCallBack<List<TIMFriendResult>> (){
								
								@Override
								public void onError(int arg0, String arg1) {
									// TODO Auto-generated method stub
									Log.e(TAG,"delBlackList error:" + arg0 + ":" + arg1);
								}
	
								@Override
								public void onSuccess(List<TIMFriendResult> arg0) {
									// TODO Auto-generated method stub
									Log.d(TAG,"delBlackList succ:" + arg0.size());
									List<String> mBlackList = UserInfoManagerNew.getInstance().getBlackList();
									for(TIMFriendResult arg : arg0){
										mBlackList.remove(arg.getIdentifer());
									}
									
								}
								
							});							
						}
				}
	        });	
		 
		 Button btDelete = (Button) findViewById(R.id.btn_delete_friend);
		 btDelete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG,"delete friend" + userID);
				List<TIMAddFriendRequest> list = new ArrayList<TIMAddFriendRequest>();
				TIMAddFriendRequest req = new TIMAddFriendRequest();
				req.setIdentifier(userID);
				list.add(req);
				TIMFriendshipManager.getInstance().delFriend(TIMDelFriendType.TIM_FRIEND_DEL_BOTH, list, new TIMValueCallBack<List<TIMFriendResult>>(){

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e(TAG,"delFriend error:" + arg0 + ":" + arg1);
					}

					@Override
					public void onSuccess(List<TIMFriendResult> arg0) {
						// TODO Auto-generated method stub
						Log.d(TAG,"delFriend succ");	
						
						//删除会话
				      	boolean result = TIMManager.getInstance().deleteConversation(TIMConversationType.C2C,userID);
                    	Log.d(TAG,"delete conversition result:"+ result);
						//维护本地关系链
						UserInfoManagerNew.getInstance().getContactsListFromServer();
						Intent intent = new Intent(UserInfoActivity.this,MainActivity.class); 
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
					
				});
			}
			 
		 });
		 
		 ImageView ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
		 //ivAvatar.setImageResource(resId);
	}	

	
	public void onBack(View view){	
		finish();	
	}
	

}
