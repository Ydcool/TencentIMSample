package com.example.mydemo.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mydemo.MyApplication;
import com.example.mydemo.R;
import com.example.mydemo.c2c.HttpProcessor;
import com.example.mydemo.utils.CommonHelper;
public class RegisterActivity extends MyBaseActivity {	
	private final static String TAG = RegisterActivity.class.getSimpleName();

	private EditText mETUserName;
	private EditText mETPassword;
	private EditText mETConfirmPassword;
	private StringBuilder mStrRetMsg;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		onInit();	
	}

	@Override
	protected void onResume() {

		super.onResume();
		
	}

	public void onInit() {
		mETUserName = (EditText)findViewById(R.id.et_user);
		mETPassword = (EditText)findViewById(R.id.et_password);
		mETConfirmPassword = (EditText)findViewById(R.id.et_confirm_password);
		mStrRetMsg = new StringBuilder();
		//m
	}
	
	
	public void onRegister(View view)
	{
		Log.d(TAG,"begin register");
		if(!CommonHelper.GetNetWorkStatus(getBaseContext())){
			Toast.makeText(getBaseContext(),"网络不可用，请检查网络设置!", Toast.LENGTH_LONG).show();
			return;
		}
		final String userName = mETUserName.getText().toString().trim();
		final String pwd = mETPassword.getText().toString().trim();
		if( userName.isEmpty() || 
				pwd.isEmpty()  ||
				mETConfirmPassword.getText().toString().trim().isEmpty() ){
			Toast.makeText(this, "用户名或密码不能用空", Toast.LENGTH_SHORT).show();	
			return;		
		}
		if(pwd.compareTo(mETConfirmPassword.getText().toString().trim()) != 0){
			Toast.makeText(this, "密码不一致，请重新输入密码", Toast.LENGTH_SHORT).show();
			return;
		}

		new Thread( new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				final int iRet = HttpProcessor.doRequestRegister(userName, pwd, mStrRetMsg);		
				runOnUiThread(new Runnable() {
				public void run(){
				if(iRet == 0){
					Toast.makeText(getBaseContext(), "注册成功", Toast.LENGTH_SHORT).show();
					MyApplication.getInstance().setUserName(userName);
					finish();						
				}else{					
					Toast.makeText(getBaseContext(), "注册异常:" + iRet + ":" + mStrRetMsg.toString(), Toast.LENGTH_SHORT).show();
				}
				}
			});		
			}
			
		}).start();

	}

	public void onBack(View view) {
		finish();
	}

}
