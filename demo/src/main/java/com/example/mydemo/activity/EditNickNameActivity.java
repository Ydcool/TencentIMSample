package com.example.mydemo.activity;

import  com.example.mydemo.R;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.example.mydemo.utils.Constant;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



 public class EditNickNameActivity  extends MyBaseActivity {
	private final static String TAG = EditNickNameActivity.class.getSimpleName();
	private EditText mEVNickName;	
//	private String mGroupID;

	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_nickname);		
		initView();	
	}
	
	public void initView() {	
		mEVNickName = (EditText) findViewById(R.id.et_nick_name);
		Button button = (Button) findViewById(R.id.btn_edit_nick_name);
	//	mGroupID = getIntent().getStringExtra("groupID");
		String nickName = getIntent().getStringExtra("nickName");
		mEVNickName.setText(nickName);
		
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				final String strNickName = mEVNickName.getText().toString().trim();

				if(strNickName.equals("")){
					Toast.makeText(getBaseContext(), "请输入昵称!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				try{
					byte[] byte_num = strNickName.getBytes("utf8");
					if(byte_num.length > Constant.MAX_USER_NICK_NAME){
						Toast.makeText(getBaseContext(), "昵称不能超过" + Constant.MAX_USER_NICK_NAME + "个字节",Toast.LENGTH_SHORT).show();
						return;
					}
				}catch(Exception e){
					e.printStackTrace();
				}								
			
				//这里调用修改接口
				TIMFriendshipManager.getInstance().setNickName(strNickName, new TIMCallBack(){

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e(TAG,"error:"+ arg0 + ":" + arg1);
						finish();
					}

					@Override
					public void onSuccess() {
						Log.d(TAG,"modify nickname ok!");
						UserInfoManagerNew.getInstance().setNickName(strNickName);
						finish();
						// TODO Auto-generated method stub
						
					}
					
				});
				
			}
			
		});
			
	}	
		
	public void onBack(View view){	
		finish();
	}

}
