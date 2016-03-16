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



 public class EditGroupIntroduceActivity  extends MyBaseActivity {
	private final static String TAG = EditGroupIntroduceActivity.class.getSimpleName();
	private EditText mEVGroupIntroduce;	
	private String mGroupID;

	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group_introduce);		
		initView();	
	}
	
	public void initView() {	
		mEVGroupIntroduce = (EditText) findViewById(R.id.et_group_introduce);
		Button button = (Button) findViewById(R.id.btn_edit_group_introduce);
		mGroupID = getIntent().getStringExtra("groupID");
		String groupIntroduce = getIntent().getStringExtra("groupIntroduce");
		mEVGroupIntroduce.setText(groupIntroduce);
		
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				try{
					byte[] byte_num = mEVGroupIntroduce.getText().toString().trim().getBytes("utf8");
					if(byte_num.length > Constant.MAX_GROUP_INTRODUCE_LENGTH){
						Toast.makeText(getBaseContext(), "群简介不能超过" + Constant.MAX_GROUP_INTRODUCE_LENGTH + "个字节",Toast.LENGTH_SHORT).show();
						return;
					}
				}catch(Exception e){
					e.printStackTrace();
				}								
			
				//修改群组名称
				TIMGroupManager.getInstance().modifyGroupIntroduction(mGroupID, mEVGroupIntroduce.getText().toString().trim(), new TIMCallBack() {
					
					@Override
					public void onError(int code, String desc) {	
						if(code == Constant.HAVE_SENSITIVE_WORD){
							Toast.makeText(EditGroupIntroduceActivity.this, "群简介中含有敏感词，请重新输入",Toast.LENGTH_SHORT).show();
						}
						Log.e(TAG,"modifyGroupIntroduction failed,code:" + code + ":" + desc);
					}
					@Override
					public void onSuccess() {						
						Log.e(TAG,"modifyGroupIntroduction succ!");
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
