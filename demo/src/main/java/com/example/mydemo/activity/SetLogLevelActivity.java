package com.example.mydemo.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.example.mydemo.MyApplication;
import com.example.mydemo.R;
import com.example.mydemo.SDKHelper;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendAllowType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMLogLevel;
import com.tencent.TIMManager;
public class SetLogLevelActivity extends MyBaseActivity {

	private final static String TAG = SetLogLevelActivity.class.getSimpleName();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_level);
		
		onInit();	
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	public void onInit() {
		RadioGroup rg_log_level = (RadioGroup) findViewById(R.id.rg_log_level);
		final RadioButton  rb_off = (RadioButton) findViewById(R.id.rb_off);
		final RadioButton  rb_error = (RadioButton) findViewById(R.id.rb_error);
		final RadioButton  rb_warn = (RadioButton) findViewById(R.id.rb_warn);
		final RadioButton  rb_info = (RadioButton) findViewById(R.id.rb_info);
		final RadioButton  rb_debug = (RadioButton) findViewById(R.id.rb_debug);
		
		int loglevel = MyApplication.getInstance().getLogLevel(); 
	
//		if(loglevel == TIMLogLevel.OFF){
//			rg_log_level.check(rb_off.getId());
//		}else if(loglevel == TIMLogLevel.ERROR){
//			rg_log_level.check(rb_error.getId());
//		}else if(loglevel == TIMLogLevel.WARN){
//			rg_log_level.check(rb_warn.getId());
//		}else if(loglevel == TIMLogLevel.INFO){
//			rg_log_level.check(rb_info.getId());
//		}else if(loglevel == TIMLogLevel.DEBUG){
//			rg_log_level.check(rb_debug.getId());
//		}
		if(loglevel == SDKHelper.LOG_OFF){
			rg_log_level.check(rb_off.getId());
		}else if(loglevel == SDKHelper.LOG_ERROR){
			rg_log_level.check(rb_error.getId());
		}else if(loglevel == SDKHelper.LOG_WARN){
			rg_log_level.check(rb_warn.getId());
		}else if(loglevel == SDKHelper.LOG_INFO){
			rg_log_level.check(rb_info.getId());
		}else if(loglevel == SDKHelper.LOG_DEBUG){
			rg_log_level.check(rb_debug.getId());
		}				
		
		

		rg_log_level.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
			//	 TIMFriendAllowType allowType = TIMFriendAllowType.TIM_FRIEND_NEED_CONFIRM;
				TIMLogLevel loglevel = TIMLogLevel.INFO;
				int nLog= SDKHelper.LOG_INFO;
				if(rb_off.getId() == checkedId){
					loglevel = TIMLogLevel.OFF;
					nLog=SDKHelper.LOG_OFF;
				}else if(rb_error.getId() == checkedId){
					loglevel = TIMLogLevel.ERROR;
					nLog=SDKHelper.LOG_ERROR;
				}else if (rb_warn.getId() == checkedId){
					loglevel = TIMLogLevel.WARN;
					nLog=SDKHelper.LOG_WARN;
				}else if (rb_info.getId() == checkedId){
					loglevel = TIMLogLevel.INFO;
					nLog=SDKHelper.LOG_INFO;
				}else if (rb_debug.getId() == checkedId){
					loglevel = TIMLogLevel.DEBUG;
					nLog=SDKHelper.LOG_DEBUG;
				}
				MyApplication.getInstance().setLogLevel(nLog);
				Log.d(TAG,"set log level:" + loglevel);
				TIMManager.getInstance().setLogLevel(loglevel);
				finish();
			}
			
		});
	}
	
	
	public void onBack(View view) {
		finish();
	}

}
