package com.example.mydemo.activity;

import  com.example.mydemo.R;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupManager;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



 public class SetSlienceTimeActivity  extends MyBaseActivity {
	private final static String TAG = SetSlienceTimeActivity.class.getSimpleName();
	private EditText mEVTime;	
//	private String mGroupID;

	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_silence_time);		
		initView();	
	}
	
	public void initView() {	
		mEVTime = (EditText) findViewById(R.id.et_silence_time);
		Button button = (Button) findViewById(R.id.btn_set_silence_time);
		final String groupID = getIntent().getStringExtra("groupID");
		final String memberID = getIntent().getStringExtra("memberID");
		
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				final String strTime = mEVTime.getText().toString().trim();
				if(strTime.equals("")){
					Toast.makeText(getBaseContext(), "请输入禁言时间(秒)!", Toast.LENGTH_SHORT).show();
					return;
				}			
			
				//这里调用修改接口
				TIMGroupManager.getInstance().modifyGroupMemberInfoSetSilence(groupID, memberID, new Integer(strTime), new TIMCallBack(){

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e(TAG,"modifyGroupMemberInfoSetSilence error:"+ arg0 + ":" + arg1);					
					}

					@Override
					public void onSuccess() {
						Log.d(TAG,"modifyGroupMemberInfoSetSilence succ!");						
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
