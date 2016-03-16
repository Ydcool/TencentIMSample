package com.example.mydemo.activity;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdRegListener;
import tencent.tls.platform.TLSSmsRegListener;
import tencent.tls.platform.TLSUserInfo;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mydemo.MyApplication;
import com.example.mydemo.R;
import com.example.mydemo.TLSHelper;
import com.example.mydemo.c2c.HttpProcessor;
import com.example.mydemo.utils.CommonHelper;
public class RegisterNewActivity extends MyBaseActivity {	
	private final static String TAG = RegisterNewActivity.class.getSimpleName();

	private EditText mETUserName;
//	private EditText mETPassword;
	private EditText mETVerifyCode;
	private Button mBTNReqVerifyCode;
	private TimeCount time;
	private SmsRegListener	smsRegListener = new SmsRegListener();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_new);
		
		onInit();	
	}

	@Override
	protected void onResume() {

		super.onResume();
		
	}

	public void onInit() {
		mETUserName = (EditText)findViewById(R.id.et_user);
		mETVerifyCode = (EditText)findViewById(R.id.et_verifycode);		
		mBTNReqVerifyCode = (Button) findViewById(R.id.btn_reqverifycode);
		time = new TimeCount(30000, 1000);
	}
	
	public void onReqVerifyCode(View view){
		if(!CommonHelper.GetNetWorkStatus(getBaseContext())){
			Toast.makeText(getBaseContext(),"网络不可用，请检查网络设置!", Toast.LENGTH_LONG).show();
			return;
		}
		 String userID = mETUserName.getText().toString().trim();
		if( userID.isEmpty()   ){
			Toast.makeText(this, "手机号不能用空", Toast.LENGTH_SHORT).show();	
			return;		
		}	
		userID = TLSHelper.PREFIX  + userID;
	//	TLSHelper.accountHelper.TLSSmsRegAskCode(userID, smsRegListener);
		time.start();
	}
	
	
	public void onRegister(View view)
	{
		 String verifyCode = mETVerifyCode.getText().toString().trim();
		 if(mETUserName.getText().toString().trim().isEmpty()){
			Toast.makeText(this, "请输入用户ID并请求验证码!", Toast.LENGTH_SHORT).show();	
			return;				 
		 }
		if( verifyCode.isEmpty() ){
			Toast.makeText(this, "验证码不能为空!", Toast.LENGTH_SHORT).show();	
			return;		
		}			
		Log.d(TAG,"begin register");

//		TLSHelper.accountHelper.TLSSmsRegVerifyCode(verifyCode, smsRegListener);
	}

	public void onBack(View view) {
		finish();
	}
	
	public void showToast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		Log.d(TAG,msg);
	}
	
	private void notOK(TLSErrInfo errInfo) {
		showToast(String.format("%s: 错误码: %d, 详情: %s", errInfo.ErrCode == TLSErrInfo.TIMEOUT ? "网络超时" : "错误", errInfo.ErrCode, errInfo.Msg));
	}		
	
	class SmsRegListener implements TLSSmsRegListener {
		@Override
		public void OnSmsRegAskCodeSuccess(int reaskDuration, int expireDuration) {
			showToast("验证码已下发,请查收.");
		}

		@Override
		public void OnSmsRegReaskCodeSuccess(int reaskDuration, int expireDuration) {
			showToast("注册短信已经重新下发。。。" + "next_time: " + reaskDuration + ", total_time: " + expireDuration);
		}

		@Override
		public void OnSmsRegVerifyCodeSuccess() {
			Log.d(TAG,"验证成功，开始注册");
		//	TLSHelper.accountHelper.TLSSmsRegCommit(smsRegListener);
		}

		@Override
		public void OnSmsRegCommitSuccess(TLSUserInfo userInfo) {
			showToast("成功注册:" + userInfo.identifier + ":" + userInfo.accountType);
			TLSHelper.userID = userInfo.identifier;	
			finish();	
		}

		@Override
		public void OnSmsRegFail(TLSErrInfo errInfo) {
			String str = errInfo.Msg;
			if(errInfo.ErrCode == TLSErrInfo.ACCOUNT_SESSION_NOTFOUND){
				str = "请先获取短信验证码!";
			}else if(errInfo.ErrCode == TLSErrInfo.TIMEOUT){
				str = "网络超时";
			}
			showToast("注册失败!" + str);
			Log.e(TAG,"注册失败" + errInfo.ErrCode + ":" + errInfo.Msg);
			mBTNReqVerifyCode.setText("获取短信验证码");
			mBTNReqVerifyCode.setBackgroundResource(R.drawable.btn_register_bg);
			mBTNReqVerifyCode.setClickable(true);
			time.cancel();
		}

		@Override
		public void OnSmsRegTimeout(TLSErrInfo errInfo) {
			notOK(errInfo);
		}
	}	
	
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}
		
		@Override
		public void onFinish() {
			mBTNReqVerifyCode.setText("获取短信验证码");
			mBTNReqVerifyCode.setBackgroundResource(R.drawable.btn_register_bg);
			mBTNReqVerifyCode.setClickable(true);
		}
		@Override
		public void onTick(long millisUntilFinished){//计时过程显示
			mBTNReqVerifyCode.setClickable(false);
			mBTNReqVerifyCode.setBackgroundResource(R.drawable.btn_gray_bg);
			mBTNReqVerifyCode.setText(millisUntilFinished /1000+"秒后再次请求");
		}
	}
}
