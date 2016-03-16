package com.example.mydemo.activity;

import  com.example.mydemo.R;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.example.mydemo.utils.Constant;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupManager;


import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



 public class EditGroupNickNameActivity  extends MyBaseActivity {
	private final static String TAG = EditGroupNickNameActivity.class.getSimpleName();
	private EditText mEVNickName;	
	private String mGroupID;
	private String mUserID;

	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group_name);		
		initView();	
	}
	
	public void initView() {	
		mEVNickName = (EditText) findViewById(R.id.et_group_name);
		Button button = (Button) findViewById(R.id.btn_edit_group_name);
		mGroupID = getIntent().getStringExtra("groupID");
		mUserID = getIntent().getStringExtra("userID");
		final String nameCard = getIntent().getStringExtra("groupNameCard");
		TextView tvTag = (TextView) findViewById(R.id.tv_tag);
		tvTag.setText("修改群名片");
		mEVNickName.setHint("输入群名片");
		if(nameCard!= null && nameCard.length()!=0)
			mEVNickName.setText(nameCard);
		
		
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mEVNickName.getText().toString().trim().equals("")){
					Toast.makeText(getBaseContext(), "请输入群名片!", Toast.LENGTH_SHORT).show();
					return;
				}
				try{
					byte[] byte_num = mEVNickName.getText().toString().trim().getBytes("utf8");
					if(byte_num.length > Constant.MAX_GROUP_NAME_LENGTH){
						Toast.makeText(getBaseContext(), "昵称不能超过" + Constant.MAX_GROUP_NAME_LENGTH + "个字节",Toast.LENGTH_SHORT).show();
						return;
					}
				}catch(Exception e){
					e.printStackTrace();
				}								
				Log.d(TAG,"modify namecard:" + mGroupID +":" + mEVNickName.getText().toString().trim()+ ":" + mUserID);
				TIMGroupManager.getInstance().modifyGroupMemberInfoSetNameCard(mGroupID, mUserID, mEVNickName.getText().toString().trim(), new TIMCallBack(){

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(EditGroupNickNameActivity.this, "修改昵称失败.error code:"+arg0+":"+arg1, Toast.LENGTH_SHORT).show();
						Log.e(TAG,"modifyGroupMemberInfoSetNameCard error:"+arg0+":"+arg1);
					}

					@Override
					public void onSuccess() {
						Log.e(TAG,"modifyGroupMemberInfoSetNameCard onSuccess");
						// TODO Auto-generated method stub
						finish();
						
					}
					
				});
				//修改群昵称				
			}
			
		});
			
	}	
		
	public void onBack(View view){	
		finish();
	}

}
