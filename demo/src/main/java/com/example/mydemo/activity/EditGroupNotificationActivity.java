package com.example.mydemo.activity;

import  com.example.mydemo.R;
import com.example.mydemo.utils.Constant;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupManager;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



 public class EditGroupNotificationActivity  extends MyBaseActivity {
	private final static String TAG = EditGroupNotificationActivity.class.getSimpleName();
	private EditText mEVGroupNotification;	
	private String mGroupID;

	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group_notification);		
		initView();	
	}
	
	public void initView() {	
		mEVGroupNotification = (EditText) findViewById(R.id.et_group__notification);
		Button button = (Button) findViewById(R.id.btn_edit_group__notification);
		mGroupID = getIntent().getStringExtra("groupID");
		String groupIntroduce = getIntent().getStringExtra("groupNotification");
		mEVGroupNotification.setText(groupIntroduce);
		
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				try{
					byte[] byte_num = mEVGroupNotification.getText().toString().trim().getBytes("utf8");
					if(byte_num.length > Constant.MAX_GROUP_NOTICE_LEN){
						Toast.makeText(getBaseContext(), "群公告不能超过" + Constant.MAX_GROUP_NOTICE_LEN + "个字节",Toast.LENGTH_SHORT).show();
						return;
					}
				}catch(Exception e){
					e.printStackTrace();
				}								
			
				//修改群组名称
				TIMGroupManager.getInstance().modifyGroupNotification(mGroupID, mEVGroupNotification.getText().toString().trim(), new TIMCallBack() {
					
					@Override
					public void onError(int code, String desc) {	
						if(code == Constant.HAVE_SENSITIVE_WORD){
							Toast.makeText(EditGroupNotificationActivity.this, "群公告中含有敏感词，请重新输入",Toast.LENGTH_SHORT).show();
						}
						Log.e(TAG,"modifyGroupNotification failed,code:" + code + ":" + desc);
					}
					@Override
					public void onSuccess() {						
						Log.e(TAG,"modifyGroupNotification succ!");
					}
				});				
				finish();
			}
			
		});
			
	}	
		
	public void onBack(View view){	
		finish();
	}

}
