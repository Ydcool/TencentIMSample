package com.example.mydemo.activity;

import java.util.ArrayList;
import java.util.List;

import  com.example.mydemo.R;
import com.tencent.TIMAddFriendRequest;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMValueCallBack;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

 public class AddFriendInfoActivity  extends MyBaseActivity {
	private final static String TAG = AddFriendInfoActivity.class.getSimpleName();
	private EditText mETRequestInfo;
	private String mStrFriendID;

	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend_info);		
		initView();	
	}
	
	public void initView() {	
		mETRequestInfo = (EditText) findViewById(R.id.et_request_info);
		mStrFriendID = getIntent().getStringExtra("userID");
	}	
		
	public void onBack(View view){	
		finish();
	}
	
	public void onRequestFriend(View view){
		String info = mETRequestInfo.getText().toString().trim();
		if(info.equals("")){
		//	Toast.makeText(getBaseContext(), "请输入请求信息", Toast.LENGTH_SHORT).show();
		//	return ;
			info = "请求添加你为好友";
		}
		
	
		List<TIMAddFriendRequest> reqList = new ArrayList<TIMAddFriendRequest>();
		TIMAddFriendRequest friend = new TIMAddFriendRequest();
		friend.setAddWording(info);
		friend.setIdentifier(mStrFriendID);
		friend.setRemark(info);
		friend.setAddrSource("qq");
		reqList.add(friend);
		Log.d(TAG,"add friend here:" +mStrFriendID);
		TIMFriendshipManager.getInstance().addFriend(reqList, new TIMValueCallBack<List<TIMFriendResult>>(){

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.e(TAG,"add friend error:" + arg0 + ":" + arg1);
				finish();
			}

			@Override
			public void onSuccess(List<TIMFriendResult> arg0) {
				// TODO Auto-generated method stub
				Log.d(TAG,"add friend response" );
				for( TIMFriendResult arg : arg0){
					Log.d(TAG, "add friend  result:" + arg.getIdentifer() + arg.getStatus());
				}
				finish();
			}
			
		});
		Log.d(TAG,"add friend here1:" +mStrFriendID);
	}

}
