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
import android.widget.Toast;



 public class EditGroupNameActivity  extends MyBaseActivity {
	private final static String TAG = EditGroupNameActivity.class.getSimpleName();
	private EditText mEVGroupName;	
	private String mGroupID;

	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group_name);		
		initView();	
	}
	
	public void initView() {	
		mEVGroupName = (EditText) findViewById(R.id.et_group_name);
		Button button = (Button) findViewById(R.id.btn_edit_group_name);
		mGroupID = getIntent().getStringExtra("groupID");
		String groupName = getIntent().getStringExtra("groupName");
		mEVGroupName.setText(groupName);
		
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mEVGroupName.getText().toString().trim().equals("")){
					Toast.makeText(getBaseContext(), "请输入群名称!", Toast.LENGTH_SHORT).show();
					return;
				}
				try{
					byte[] byte_num = mEVGroupName.getText().toString().trim().getBytes("utf8");
					if(byte_num.length > Constant.MAX_GROUP_NAME_LENGTH){
						Toast.makeText(getBaseContext(), "群名称不能超过" + Constant.MAX_GROUP_NAME_LENGTH + "个字节",Toast.LENGTH_SHORT).show();
						return;
					}
				}catch(Exception e){
					e.printStackTrace();
				}								
				
				
				//修改群组名称
				TIMGroupManager.getInstance().modifyGroupName(mGroupID, mEVGroupName.getText().toString().trim(), new TIMCallBack() {
					
					@Override
					public void onError(int code, String desc) {						
						Log.e(TAG,"修改群名称失败,code:" + code + ":" + desc);
						if(code == Constant.HAVE_SENSITIVE_WORD){
							Toast.makeText(EditGroupNameActivity.this, "群名称中含有敏感词，请重新输入新的名称",Toast.LENGTH_SHORT).show();
						}
						finish();
					}
					@Override
					public void onSuccess() {						
						Log.e(TAG,"修改群名称成功!");
						UserInfoManagerNew.getInstance().UpdateGroupID2Name(mGroupID, mEVGroupName.getText().toString().trim(), 
																			UserInfoManagerNew.getInstance().getGroupID2Info().get(mGroupID).getType(), true);
						finish();
					}
				});				
				
			}
			
		});
			
	}	
		
	public void onBack(View view){	
		finish();
	}

}
