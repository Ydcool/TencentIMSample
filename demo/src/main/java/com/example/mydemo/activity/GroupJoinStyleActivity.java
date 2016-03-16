package com.example.mydemo.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.example.mydemo.R;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupAddOpt;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupReceiveMessageOpt;
public class GroupJoinStyleActivity extends MyBaseActivity {	
	private final static String TAG = GroupJoinStyleActivity.class.getSimpleName();
	private String mGroupID;
	private int  mAddOpt;	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_join_style);
		
		onInit();	
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	public void onInit() {
		mGroupID = getIntent().getStringExtra("groupID");
		mAddOpt = getIntent().getIntExtra("currentOPT", 1);
		RadioGroup rg_group_join_style = (RadioGroup) findViewById(R.id.rg_group_join_style);
		final RadioButton  rb_forbid = (RadioButton) findViewById(R.id.rb_forbid);
		final RadioButton  rb_need_confirm = (RadioButton) findViewById(R.id.rb_need_confirm);
		final RadioButton  rb_allow_any = (RadioButton) findViewById(R.id.rb_allow_any);
		

		if(mAddOpt == GroupInfoActivity.ADD_ALLOW_ANY){
			rg_group_join_style.check(rb_allow_any.getId());
		}else if(mAddOpt == GroupInfoActivity.ADD_FORBID){
			rg_group_join_style.check(rb_forbid.getId());
		}else if(mAddOpt == GroupInfoActivity.ADD_NEED_CONFIRM){
			rg_group_join_style.check(rb_need_confirm.getId());
		}			
				

		rg_group_join_style.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				TIMGroupAddOpt opt = TIMGroupAddOpt.TIM_GROUP_ADD_ANY;
				if(rb_allow_any.getId() == checkedId){
					opt = TIMGroupAddOpt.TIM_GROUP_ADD_ANY;
				}else if(rb_forbid.getId() == checkedId){
					opt = TIMGroupAddOpt.TIM_GROUP_ADD_FORBID;
				}else if (rb_need_confirm.getId() == checkedId){
					opt = TIMGroupAddOpt.TIM_GROUP_ADD_AUTH;
				}
				TIMGroupManager.getInstance().modifyGroupAddOpt(mGroupID,opt,new TIMCallBack(){

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e(TAG,"modifyGroupAddOpt error:" + arg0 + ":" + arg1);
					}

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Log.d(TAG,"modifyGroupAddOpt ok");
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
