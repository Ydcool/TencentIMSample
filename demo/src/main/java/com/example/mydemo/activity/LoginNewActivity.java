package com.example.mydemo.activity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSSmsLoginListener;
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


import com.example.mydemo.activity.RegisterNewActivity.TimeCount;
import com.example.mydemo.c2c.HttpProcessor;
import com.example.mydemo.c2c.UserInfo;
import com.example.mydemo.c2c.UserInfoManager;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.example.mydemo.utils.CommonHelper;
import com.example.mydemo.utils.Constant;
import com.example.mydemo.MyApplication;
import com.example.mydemo.R;
import com.example.mydemo.TLSHelper;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMManager;
import com.tencent.TIMUser;
import com.tencent.TIMValueCallBack;


public class LoginNewActivity extends MyBaseActivity{
	private static final String TAG = LoginNewActivity.class.getSimpleName();
	private EditText mAccount, mPassword,mSmsVerifyCode;
	private Button mReqVerifyCode;
	private TimeCount time;
	private MyApplication application;	
	private final static int GO_TO_REGISTER = 1;
	private String mStrUserName,mStrPassWord,mStrVerifyCode;
	boolean mBLogin=false;
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_new);		application = (MyApplication) this.getApplicationContext();
		onInit();	
	}

	@Override
	protected void onResume() {
		super.onResume();
	
	}

	public void onInit() {	
		mAccount = (EditText) findViewById(R.id.login_account);
		mPassword = (EditText) findViewById(R.id.login_password);
		mSmsVerifyCode = (EditText) findViewById(R.id.et_verifycode);
		mReqVerifyCode = (Button) findViewById(R.id.btn_reqVerifyCode);
		time = new TimeCount(30000, 1000);
		Log.d(TAG,"ready to login:" + TLSHelper.userID );
		if(TLSHelper.userID != null){	
			mAccount.setText(processDisplayAccout(TLSHelper.userID));	
		}
	}
	
	private boolean IsEnvironmentOK(){
		if(mBLogin){
			Toast.makeText(this, "已经在登陆中，请稍等！", Toast.LENGTH_SHORT).show();
			return false;
		}	
		 mStrUserName = mAccount.getText().toString().trim();		
		if(!CommonHelper.GetNetWorkStatus(getBaseContext())){
			Toast.makeText(getBaseContext(),"网络不可用，请检查网络设置!", Toast.LENGTH_LONG).show();			
			return false;
		}		
		if (!application.isClientStart()){
			Toast.makeText(this, "亲！sdk暂未初始化", Toast.LENGTH_SHORT).show();			
			return false;
		}
		if ( mStrUserName.length() == 0   ) {
			Toast.makeText(this, "亲！帐号不能为空哦", Toast.LENGTH_SHORT).show();
			return false;
		} 
	   //涉及到界面输入的部分id都不展示国家前缀（demo都是中国）
		mStrUserName = TLSHelper.PREFIX + mStrUserName;
		return true;
	}
//	public void onPwdLogin(View view)
//	{	
//		if(!IsEnvironmentOK()){
//			return;			
//		}
//		 mStrPassWord = mPassword.getText().toString().trim();  
//		if ( mStrPassWord.length()==0 ) {
//			Toast.makeText(this, "亲！密码不能为空哦", Toast.LENGTH_SHORT).show();
//			mBLogin=false;
//			return;
//		} 
//		 Log.d(TAG,"begin to login.." + mStrUserName + ":" + mStrPassWord);
//		 TLSHelper.loginHelper.TLSPwdLogin(mStrUserName, mStrPassWord.getBytes(), new PwdLoginListener());			
//	}

	public void onReqVerifyCode(View view){
		if(!IsEnvironmentOK()){
			return;
		}			
		Log.d(TAG,"req verfity:" + mStrUserName);
	//	TLSHelper.loginHelper.TLSSmsLoginAskCode(mStrUserName, new SmsLoginListener());
		time.start();
				
	}
	
	public void onSmsLogin(View view)
	{	
		if(!IsEnvironmentOK()){
			return;
		}
		mStrVerifyCode = mSmsVerifyCode.getText().toString().trim();  
		if ( mSmsVerifyCode.length()==0 ) {
			Toast.makeText(this, "亲！验证码不能为空哦", Toast.LENGTH_SHORT).show();
			mBLogin=false;
			return;
		} 
		 Log.d(TAG,"verify login code" + mStrUserName + ":" + mStrVerifyCode);
	//	 TLSHelper.loginHelper.TLSSmsLoginVerifyCode(mStrVerifyCode,new SmsLoginListener());			
	}	
	
	public void onGoToResgiter(View view)
	{
		Intent intent = new Intent();
		intent.setClass(this, RegisterNewActivity.class);
		startActivityForResult(intent,GO_TO_REGISTER);
	}	
 
	public void showToast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	private void notOK(TLSErrInfo errInfo) {
		String str = String.format("%s: 错误码: %d, 详情: %s", errInfo.ErrCode == TLSErrInfo.TIMEOUT ? "网络超时" : "错误", errInfo.ErrCode, errInfo.Msg);
		showToast(str);
		Log.e(TAG,str);
	}
		
//	 class PwdLoginListener implements TLSPwdLoginListener {
//			@Override
//			public void OnPwdLoginSuccess(TLSUserInfo userInfo) {
//				Log.d(TAG,"密码登录成功");
//				showToast("密码登录成功！");
//				LoginToIMServer();
//			}
//
//			@Override
//			public void OnPwdLoginReaskImgcodeSuccess(byte[] picData) {
//				Log.d(TAG,"OnPwdLoginReaskImgcodeSuccess");
//			//	ImgVerifyAct.fillImageview(picData);
//			}
//
//			@Override
//			public void OnPwdLoginNeedImgcode(byte[] picData, TLSErrInfo errInfo) {
//				mBLogin=false;	
//				Log.d(TAG,"OnPwdLoginNeedImgcode");				
//			//	notOK(errInfo);
//				Toast.makeText(getApplicationContext(), "需要输入验证码!", Toast.LENGTH_SHORT).show();
//				Intent intent = new Intent(LoginNewActivity.this, ImgVerifyActivity.class);
//				intent.putExtra("picdata", picData);
//				startActivity(intent);
//			}
//
//			@Override
//			public void OnPwdLoginFail(TLSErrInfo errInfo) {
//				mBLogin=false;	
//				Log.d(TAG,"OnPwdLoginFail" + String.format("%s: ret: %d, msg: %s", errInfo.ErrCode == TLSErrInfo.TIMEOUT ? "网络超时" : "错误", errInfo.ErrCode, errInfo.Msg));
//				notOK(errInfo);
//			}
//
//			@Override
//			public void OnPwdLoginTimeout(TLSErrInfo errInfo) {
//				mBLogin=false;	
//				Log.d(TAG,"OnPwdLoginTimeout");
//				notOK(errInfo);
//			}
//		}
     
		class SmsLoginListener implements TLSSmsLoginListener {
			@Override
			public void OnSmsLoginAskCodeSuccess(int reaskDuration, int expireDuration) {
				Log.d(TAG,"OnSmsLoginAskCodeSuccess succ:" + reaskDuration + ":" +expireDuration);
				showToast("短信已下发，请查收!");
			}

			@Override
			public void OnSmsLoginReaskCodeSuccess(int reaskDuration, int expireDuration) {
				showToast("TLS登录验证短信已经重新下发...");
			}

			@Override
			public void OnSmsLoginVerifyCodeSuccess() {
				Log.d(TAG,"这时候仅仅是短信验证码通过验证，还要再调一次登录！");
		//		TLSHelper.loginHelper.TLSSmsLogin(mStrUserName, new SmsLoginListener());
			}

			@Override
			public void OnSmsLoginSuccess(TLSUserInfo userSigInfo) {
				Log.d(TAG,"OnSmsLoginSuccess");
				showToast("短信验证码登录成功！");
			//	Log.d(TAG,"OnSmsLoginSuccess:" + userSigInfo.identifier + ":" + TLSHelper.loginHelper.getUserSig(userSigInfo.identifier));
				TLSHelper.userID = mStrUserName;
				LoginToIMServer();
			}

			@Override
			public void OnSmsLoginFail(TLSErrInfo errInfo) {				
				String str = errInfo.Msg;
				if(errInfo.ErrCode == TLSErrInfo.ACCOUNT_SESSION_NOTFOUND){
					str = "请先获取短信验证码!";
				}else if(errInfo.ErrCode == TLSErrInfo.TIMEOUT){
					str = "网络超时";
				}
				showToast("登陆失败!" + str);
				Log.e(TAG,"登陆失败" + errInfo.ErrCode + ":" + errInfo.Msg);
				mReqVerifyCode.setText("获取短信验证码");
				mReqVerifyCode.setBackgroundResource(R.drawable.btn_register_bg);
				mReqVerifyCode.setClickable(true);
				time.cancel();
			}

			@Override
			public void OnSmsLoginTimeout(TLSErrInfo errInfo) {
				notOK(errInfo);
			}
		}
	 
	 private void LoginToIMServer(){
	   TIMUser user = new TIMUser();
	   user.setAccountType(String.valueOf(Constant.ACCOUNT_TYPE));
	   user.setAppIdAt3rd(String.valueOf(Constant.SDK_APPID));
	   user.setIdentifier(mStrUserName);
		 
		 //发起到IM后台登录请求
//      TIMManager.getInstance().login(Constant.SDK_APPID,user,TLSHelper.loginHelper.getUserSig(mStrUserName),
//       			new TIMCallBack() {//回调接口
//                   @Override
//                   public void onSuccess() {//登录成功
//                       Log.d(TAG, "login succ");              
//                       MyApplication.getInstance().setUserName(mStrUserName);                        
//	       	
//    					UserInfoManagerNew.getInstance().getGroupListFromServer();
//    					UserInfoManagerNew.getInstance().getSelfProfile();
//        				UserInfoManagerNew.getInstance().getContactsListFromServer();
//        				UserInfoManagerNew.getInstance().getBlackListFromServer();
//	    				
//	            		Intent intent = new Intent();
//	            		intent.setClass(LoginNewActivity.this, MainActivity.class);
//	            		startActivity(intent);         
//               	}
//
//                   @Override
//                   public void onError(int code, String desc) {
//                   	Toast.makeText(getBaseContext(), "IMServer登录失败:" + code + ":" + desc, Toast.LENGTH_SHORT).show();
//                       Log.e(TAG, "login imserver failed. code: " + code + " errmsg: " + desc);
//                       mBLogin=false;	
//                   }
//               });	 
	 }
	 
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);	
		if (requestCode == GO_TO_REGISTER) {			
			if(TLSHelper.userID != null){
				mAccount.setText(processDisplayAccout(TLSHelper.userID));
				mPassword.setText("");
			}					
		
		}
	} 

	private String processDisplayAccout(String strAccount){
		String strTmp = strAccount;
		if(TLSHelper.userID.startsWith("86-", 0)){
			strTmp = strAccount.substring(3);
		}
		return strTmp;
	}
	
	
	@Override
	public void onDestroy() {
		Log.d(TAG,"onDestroy" );	
		super.onDestroy();
		//TIMManager.getInstance().logout();			
		System.exit(0);			
	}	
	
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}
		
		@Override
		public void onFinish() {
			mReqVerifyCode.setText("获取短信验证码");
			mReqVerifyCode.setBackgroundResource(R.drawable.btn_register_bg);
			mReqVerifyCode.setClickable(true);
		}
		@Override
		public void onTick(long millisUntilFinished){//计时过程显示
			mReqVerifyCode.setClickable(false);
			mReqVerifyCode.setBackgroundResource(R.drawable.btn_gray_bg);
			mReqVerifyCode.setText(millisUntilFinished /1000+"秒后再次请求");
		}
	}	
}


