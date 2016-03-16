package com.example.mydemo.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.mydemo.MyApplication;
import  com.example.mydemo.R;
import com.example.mydemo.adapter.GroupMemberAdapter;
import com.example.mydemo.c2c.UserInfoManager;
import com.example.mydemo.c2c.UserInfoManagerNew;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.mydemo.utils.Constant;
import com.example.mydemo.view.MyGridView;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMValueCallBack;


 public class GroupSimpleInfoActivity  extends MyBaseActivity {
	private final static String TAG = GroupSimpleInfoActivity.class.getSimpleName();

	private String mStrGroupID;
	private String mStrGroupType; 
	private String mStrGroupName;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_simple_info);		

		initView();	
	}
	
	public void initView() {	
		 
		 mStrGroupID = getIntent().getStringExtra("groupID");
		 mStrGroupType = getIntent().getStringExtra("groupType");
		 mStrGroupName = getIntent().getStringExtra("groupName");
		 String owner = getIntent().getStringExtra("owner");
		 String desc = getIntent().getStringExtra("describe");
		 TextView tvGroupName = (TextView) findViewById(R.id.tv_name);
		 tvGroupName.setText(mStrGroupName);
		 TextView tvCreator = (TextView) findViewById(R.id.tv_creator);
		 tvCreator.setText(UserInfoManagerNew.getInstance().getDisplayName(owner));		 
		 TextView tvGroupDesc = (TextView) findViewById(R.id.tv_group_desc);
		 tvGroupDesc.setText(desc);			 
		 
		 
		 Button btJoin = (Button) findViewById(R.id.btn_join_group);
		 btJoin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TIMGroupManager.getInstance().applyJoinGroup(mStrGroupID, "申请加入群"+mStrGroupID, new TIMCallBack(){

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e(TAG,"applyJoinGroup error:" + arg0 + ":" + arg1);
						Toast.makeText(GroupSimpleInfoActivity.this, "加群失败，失败原因："+ arg0 + ":" + arg1,Toast.LENGTH_LONG).show();
					}

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Log.e(TAG,"applyJoinGroup succ");
						UserInfoManagerNew.getInstance().UpdateGroupID2Name(mStrGroupID,mStrGroupName,mStrGroupType,true);
						
					    Intent intent = new Intent(GroupSimpleInfoActivity.this, GroupListActivity.class);
				    	intent.putExtra("groupType",mStrGroupType);
				    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					    startActivity(intent);		
						finish();
					}
					
				});
			}
			 
		 });
		 
		 ImageView ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
		 //ivAvatar.setImageResource(resId);
	}	

	
	public void onBack(View view){	
		finish();	
	}
	

}
