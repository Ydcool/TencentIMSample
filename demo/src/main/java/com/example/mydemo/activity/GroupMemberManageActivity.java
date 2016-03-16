package com.example.mydemo.activity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import  com.example.mydemo.R;
import com.example.mydemo.TLSHelper;
import com.example.mydemo.adapter.GroupListAdapter;
import com.example.mydemo.adapter.GroupMemberManageAdapter;
import com.example.mydemo.c2c.UserInfo;
import com.example.mydemo.c2c.UserInfoManager;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.example.mydemo.utils.Constant;

import android.app.AlertDialog; 
import android.app.Dialog; 
import android.content.DialogInterface; 
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberRoleType;
import com.tencent.TIMGroupTipsType;
import com.tencent.TIMValueCallBack;


 public class GroupMemberManageActivity  extends MyBaseActivity {
	private final static String TAG = GroupMemberManageActivity.class.getSimpleName();
//	private  List<TIMGroupBaseInfo> mListGroup;
	private  List<TIMGroupMemberInfo> mListMember;
	private ListView mLVMember;
	private String mStrGroupType;
	private GroupMemberManageAdapter mAdapter;
	private String mStrGroupID;
//	private TIMGroupMemberRoleType  mMyRoleType;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_member_manage);	
		initView();	
	}
	
	public void initView() {	
		
//		mListMember = getIntent().getStringArrayListExtra("members");	
	//	mListMember.remove(Constant.INVITE_FRIEND_TO_GROUP);
		mStrGroupID = getIntent().getStringExtra("groupID");
		
		mLVMember= (ListView)findViewById(R.id.lv_group_member);	
		mListMember = new ArrayList<TIMGroupMemberInfo>();
		mAdapter = new GroupMemberManageAdapter(this,mStrGroupID,mListMember);
	//	loadContent();
		mLVMember.setAdapter(mAdapter);	
		}	
	
	public void loadContent(){
		TIMGroupManager.getInstance().getGroupMembers(mStrGroupID,new TIMValueCallBack<List<TIMGroupMemberInfo>>(){

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				  Log.e(TAG, "get gruop member failed: " + arg0 + " arg1");
			}

			@Override
			public void onSuccess(List<TIMGroupMemberInfo> arg0) {
				// TODO Auto-generated method stub
		    	mListMember.clear();
		    	String createID=null;
		    	for(int i=0;i<arg0.size();i++){
		    		
		    		TIMGroupMemberRoleType tmpRole = arg0.get(i).getRole();
		    		if(tmpRole == TIMGroupMemberRoleType.Owner){
		    			mListMember.add(0,arg0.get(i));
		    			createID = arg0.get(i).getUser();
		    		}else if(tmpRole == TIMGroupMemberRoleType.Admin){
		    			if(createID!=null){
		    				mListMember.add(1,arg0.get(i));
		    			}else{
		    				mListMember.add(0,arg0.get(i));
		    			}
		    		}else{
		    			mListMember.add(arg0.get(i));
		    		}
		    		//mListMember.add(arg0.get(i));
		    		Log.d(TAG,arg0.get(i).getUser() + ":" + arg0.get(i).getRole()+ ":" + TLSHelper.userID);
		    		if(arg0.get(i).getUser().equals(TLSHelper.userID)){
		    			mAdapter.setMyRole(arg0.get(i).getRole());		    		
		    			Log.d(TAG,"my:" + TLSHelper.userID  + arg0.get(i).getRole());
		    		}
		    	}
	        	mAdapter.notifyDataSetChanged(); 
			}
			
		});
	}


	@Override
	public void onResume() {		
		super.onResume();		
		loadContent();
		Log.d(TAG,"activity resume ,refresh list");
	}	
	
	public void onBack(View view){	
		finish();
	}
	

}
