package com.example.mydemo.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.example.mydemo.R;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.example.mydemo.utils.GroupInfo;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupReceiveMessageOpt;
public class ReceiveMsgOptActivity extends MyBaseActivity {	
	private final static String TAG = ReceiveMsgOptActivity.class.getSimpleName();
	private String mGroupID;
	private int  mMsgOpt;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receive_msg_opt);
		
		onInit();	
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	public void onInit() {
		mGroupID = getIntent().getStringExtra("groupID");
		mMsgOpt = getIntent().getIntExtra("currentOPT", 1);
		RadioGroup rg_receive_msg_opt = (RadioGroup) findViewById(R.id.rg_receive_msg_opt);
		final RadioButton  rb_receive_and_notify = (RadioButton) findViewById(R.id.rb_receive_and_notify);
		final RadioButton  rb_receive_not_notify = (RadioButton) findViewById(R.id.rb_receive_not_notify);
		final RadioButton  rb_not_receive = (RadioButton) findViewById(R.id.rb_not_receive);
		

		if(mMsgOpt == GroupInfoActivity.MSG_NOT_RECEIVE){
			rg_receive_msg_opt.check(rb_not_receive.getId());
		}else if(mMsgOpt == GroupInfoActivity.MSG_RECEIVE_AND_NOTIFY){
			rg_receive_msg_opt.check(rb_receive_and_notify.getId());
		}else if(mMsgOpt == GroupInfoActivity.MSG_RECEIVE_NOT_NOTIFY){
			rg_receive_msg_opt.check(rb_receive_not_notify.getId());
		}			
				

		rg_receive_msg_opt.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				TIMGroupReceiveMessageOpt opt = TIMGroupReceiveMessageOpt.ReceiveAndNotify;
				if(rb_receive_and_notify.getId() == checkedId){
					opt = TIMGroupReceiveMessageOpt.ReceiveAndNotify;
				}else if(rb_receive_not_notify.getId() == checkedId){
					opt = TIMGroupReceiveMessageOpt.ReceiveNotNotify;
				}else if (rb_not_receive.getId() == checkedId){
					opt = TIMGroupReceiveMessageOpt.NotReceive;
				}
				final TIMGroupReceiveMessageOpt fOpt = opt;
				TIMGroupManager.getInstance().modifyReceiveMessageOpt(mGroupID,opt,new TIMCallBack(){

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e(TAG,"modifyReceiveMessageOpt error:" + arg0 + ":" + arg1);
					}

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Log.d(TAG,"modifyReceiveMessageOpt ok");
						GroupInfo info = UserInfoManagerNew.getInstance().getGroupID2Info().get(mGroupID);
						if(info!=null){
							info.setMessageOpt(fOpt);
						}
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
