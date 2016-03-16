package com.example.mydemo.activity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import  com.example.mydemo.R;
import com.example.mydemo.adapter.GroupListAdapter;
import com.example.mydemo.c2c.UserInfoManager;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.example.mydemo.utils.Constant;
import com.example.mydemo.utils.GroupInfo;

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
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupTipsType;
import com.tencent.TIMManager;
import com.tencent.TIMValueCallBack;


 public class GroupListActivity  extends MyBaseActivity {
	private final static String TAG = GroupListActivity.class.getSimpleName();
//	private  List<GroupEntity> mListGroup;
	private  List<GroupInfo> mListGroup;
	private ListView mLVGroup;
	private String mStrGroupType;
	private Button mBtnCreateGroup;
	public GroupListAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grouplist);	
		initView();	
	}
	
	public void initView() {	
		mStrGroupType = getIntent().getStringExtra("groupType");		
		TextView title = (TextView) findViewById(R.id.chat_name);
		if(mStrGroupType.equals(Constant.TYPE_PUBLIC_GROUP)){
			title.setText(Constant.PUBLIC_GROUP_NICK);
		}else if(mStrGroupType.equals(Constant.TYPE_PRIVATE_GROUP)){
			title.setText(Constant.PRIVATE_GROUP_NICK);
		}else{
			title.setText(Constant.CHAT_ROOM_NICK);
		}
		Log.d(TAG,"group type:" + mStrGroupType);
		mBtnCreateGroup = (Button) findViewById(R.id.btn_goto_create_group);
		mLVGroup= (ListView)findViewById(R.id.lv_groups);	
		//mListGroup = new ArrayList<TIMGroupBaseInfo>();
		mListGroup = new ArrayList<GroupInfo>();
		mAdapter = new GroupListAdapter(getBaseContext(),mListGroup);
		mLVGroup.setAdapter(mAdapter);
		Button btJoinGroup = (Button) findViewById(R.id.btn_goto_search_group);
		btJoinGroup.setVisibility(View.INVISIBLE);
		if((mStrGroupType.equals(Constant.TYPE_PUBLIC_GROUP)) || (mStrGroupType.equals(Constant.TYPE_CHAT_ROOM)) ){
			btJoinGroup.setVisibility(View.VISIBLE);
			btJoinGroup.setOnClickListener( new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent intent =new Intent(GroupListActivity.this,SearchGroupActivity.class);
				    intent.putExtra("groupType", mStrGroupType);
					startActivity(intent);	
					
				} 			
			});
			
		}
		mBtnCreateGroup.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent =new Intent(GroupListActivity.this,CreateGroupActivity.class);
				intent.putExtra("groupType", mStrGroupType);
				startActivity(intent);	
				
			} 			
		});
		

		mLVGroup.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {			
				//TIMGroupBaseInfo entity = (TIMGroupBaseInfo) mAdapter.getItem(position);
				GroupInfo entity = (GroupInfo) mAdapter.getItem(position);
			    Intent intent = new Intent(GroupListActivity.this, ChatNewActivity.class);
			    intent.putExtra("chatType", ChatNewActivity.CHATTYPE_GROUP);
    	    	intent.putExtra("groupID", entity.getID());
		    	intent.putExtra("groupName", entity.getName());	 
		    	intent.putExtra("groupType",mStrGroupType);
		    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    startActivity(intent);				
			}
		});
		
	
		mLVGroup.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
		//		final TIMGroupBaseInfo entity = (TIMGroupBaseInfo) mAdapter.getItem(position);
				final GroupInfo entity = (GroupInfo) mAdapter.getItem(position);
			       Dialog alertDialog = new AlertDialog.Builder(GroupListActivity.this). 
			                setTitle("确定退出？"). 
			                setMessage("确定退出该群组吗？"). 			       
			                setNegativeButton("确定", new DialogInterface.OnClickListener() { 
			                     
			                    @Override 
			                    public void onClick(DialogInterface dialog, int which) { 
			                        // TODO Auto-generated method stub  
			                    	TIMGroupManager.getInstance().quitGroup(entity.getID(), new TIMCallBack(){
										@Override
										public void onError(int arg0, String arg1) {
											// TODO Auto-generated method stub
											Log.e(TAG,"quit group error:" + arg0 + ":" + arg1);
											if(arg0 == 10009){
												Toast.makeText(getBaseContext(),"群创建者不能退群", Toast.LENGTH_SHORT).show();
											}
										}
			
										@Override
										public void onSuccess() {
									
									//		Map<String,String> groupID2Name = UserInfoManager.getInstance().getGroupID2Name();
//											Map<String,GroupInfo> groupID2Info = UserInfoManagerNew.getInstance().getGroupID2Info();
//											groupID2Info.remove(entity.getID());
											Log.d(TAG,"quit group succ");
											UserInfoManagerNew.getInstance().UpdateGroupID2Name(entity.getID(), entity.getName(), entity.getType(), false);
											loadGroupList();
											
											TIMManager.getInstance().deleteConversationAndLocalMsgs(TIMConversationType.Group,entity.getID());
										}
			        					
			        				});			                    	
			                    } 
			                }). 
			                setPositiveButton("取消", new DialogInterface.OnClickListener() { 
			                     
			                    @Override 
			                    public void onClick(DialogInterface dialog, int which) { 
			                        // TODO Auto-generated method stub  
			                    } 
			                }). 			               
			                create(); 
			        alertDialog.show(); 	     
					 

				return true;
			}
		});			
	
		
		
	}	


	@Override
	public void onResume() {		
		super.onResume();			
		Log.d(TAG,"activity resume ,refresh list");
		loadGroupList();		
	}	
	
	private void loadGroupList(){ 
	 	mListGroup.clear();
	 	Set<Entry<String, String>> sets = null;
	 	if(mStrGroupType.equals(Constant.TYPE_PUBLIC_GROUP) ){
	 		sets = UserInfoManagerNew.getInstance().getPublicGroupID2Name().entrySet();
	 		Log.d(TAG,"list size:" + UserInfoManagerNew.getInstance().getPublicGroupID2Name().size() + ":" + sets.size());
	 	}else if(mStrGroupType.equals(Constant.TYPE_PRIVATE_GROUP)){
	 		sets = UserInfoManagerNew.getInstance().getPrivateGroupID2Name().entrySet();
	 	}else if(mStrGroupType.equals(Constant.TYPE_CHAT_ROOM)){
	 		sets = UserInfoManagerNew.getInstance().getChatRoomID2Name().entrySet();
	 	}else{
	 		Log.e(TAG,"group type error:" + mStrGroupType);
	 		return;
	 	}
	 	
		for(Map.Entry<String, String> entity : sets){
			GroupInfo tmp = new GroupInfo();
			tmp.setID(entity.getKey());
			tmp.setName(entity.getValue());
			tmp.setType(UserInfoManagerNew.getInstance().getGroupID2Info().get(entity.getKey()).getType());
			mListGroup.add(tmp);
		}
		Log.d(TAG,"group list:" + mListGroup.size());
    	mLVGroup.setVisibility(View.VISIBLE);
    	mAdapter.notifyDataSetChanged();        
	}
	
	public void onBack(View view){	
		finish();
	}
	

}
