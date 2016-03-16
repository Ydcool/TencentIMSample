package com.example.mydemo.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.example.mydemo.R;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendAllowType;
import com.tencent.TIMFriendshipManager;
public class PassStyleActivity extends MyBaseActivity {	
	private final static String TAG = PassStyleActivity.class.getSimpleName();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pass_style);
		
		onInit();	
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	public void onInit() {
		RadioGroup rg_pass_style = (RadioGroup) findViewById(R.id.rg_pass_style);
		final RadioButton  rb_allow_any = (RadioButton) findViewById(R.id.rb_allow_any);
		final RadioButton  rb_deny_any = (RadioButton) findViewById(R.id.rb_deny_any);
		final RadioButton  rb_need_confirm = (RadioButton) findViewById(R.id.rb_need_confirm);
		
		TIMFriendAllowType  type = UserInfoManagerNew.getInstance().getAllowType(); 
		if(type != null){
			if(type == TIMFriendAllowType.TIM_FRIEND_ALLOW_ANY){
				rg_pass_style.check(rb_allow_any.getId());
			}else if(type == TIMFriendAllowType.TIM_FRIEND_DENY_ANY){
				rg_pass_style.check(rb_deny_any.getId());
			}else if(type == TIMFriendAllowType.TIM_FRIEND_NEED_CONFIRM){
				rg_pass_style.check(rb_need_confirm.getId());
			}			
		}else{
			rg_pass_style.check(rb_allow_any.getId());
		}

		rg_pass_style.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				 TIMFriendAllowType allowType = TIMFriendAllowType.TIM_FRIEND_NEED_CONFIRM;
				if(rb_allow_any.getId() == checkedId){
					allowType = TIMFriendAllowType.TIM_FRIEND_ALLOW_ANY;
				}else if(rb_deny_any.getId() == checkedId){
					allowType = TIMFriendAllowType.TIM_FRIEND_DENY_ANY;
				}else if (rb_need_confirm.getId() == checkedId){
					allowType = TIMFriendAllowType.TIM_FRIEND_NEED_CONFIRM;
				}
				final TIMFriendAllowType tmpType = allowType;
				TIMFriendshipManager.getInstance().setAllowType(allowType, new TIMCallBack(){

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e(TAG,"set allow type error:" + arg0 + ":" + arg1);
					}

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Log.d(TAG,"set allow type ok");
						UserInfoManagerNew.getInstance().SetAllowType(tmpType);
					}
					
				});
				finish();
			}
			
		});
	}
	
	
	public void onBack(View view) {
		finish();
	}

}
