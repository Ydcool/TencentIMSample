package com.example.mydemo.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import  com.example.mydemo.R;
import com.example.mydemo.adapter.GroupMemberOperaAdapter;
import com.example.mydemo.adapter.GroupMemberOperaAdapter.ViewHolder;
import com.example.mydemo.c2c.UserInfo;
import com.example.mydemo.c2c.UserInfoManager;
import com.example.mydemo.c2c.UserInfoManagerNew;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.mydemo.utils.ChnToSpell;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberResult;
import com.tencent.TIMValueCallBack;


 public class InviteToGroupActivity  extends MyBaseActivity {
	private final static String TAG = InviteToGroupActivity.class.getSimpleName();
	private  List<UserInfo> mListUser;
	private ListView mLVUser;
	private ArrayList<String> mListMember;
	private String mStrGroupID;
	private String mStrGroupName;
	private String mStrGroupType;
	private Button mBtnInviteGroup;
	public GroupMemberOperaAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_to_group);		
		initView();	
	}
	
	public void initView() {		
		mBtnInviteGroup = (Button) findViewById(R.id.btn_invite_group);
		mLVUser= (ListView)findViewById(R.id.lv_contacts);		
		mListUser = new ArrayList<UserInfo>();
		
		mListMember = getIntent().getStringArrayListExtra("members");	
	   	
		mStrGroupID = getIntent().getStringExtra("groupID");
		mStrGroupName = getIntent().getStringExtra("groupName");
		mStrGroupType = getIntent().getStringExtra("groupType");
		loadContactsContent();
		mAdapter = new GroupMemberOperaAdapter(getBaseContext(),mListUser);		
		mLVUser.setAdapter(mAdapter);
	
		mBtnInviteGroup.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HashMap<Integer,Boolean> bMap = GroupMemberOperaAdapter.getIsSelected();
				ArrayList<String> lUsers = new ArrayList<String>();
				for(Map.Entry<Integer,Boolean> entry: bMap.entrySet()){
					if(entry.getValue()){
						lUsers.add(mListUser.get(entry.getKey()).getName());
						Log.d(TAG,"selected user:" + mListUser.get(entry.getKey()).getName());						
					}
				}			
				
				if(lUsers.size() >= 1){
					InviteGroup(lUsers);
				}else{
					Toast.makeText(getBaseContext(), "请选择至少一个好友",Toast.LENGTH_SHORT).show();
					return;
				}
			} 			
		});
		
		mLVUser.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {               
				ViewHolder holder = (ViewHolder) view.getTag();  
	                holder.bselect.toggle();  	     
	                GroupMemberOperaAdapter.getIsSelected().put(position, holder.bselect.isChecked()); 	               
	            }  
			});
	}	
	
	private void  loadContactsContent()
	{			
		mListUser.clear();
	//	Map<String, UserInfo> users = UserInfoManager.getInstance().getContactsList();
		Map<String, UserInfo> users = UserInfoManagerNew.getInstance().getContactsList();
		if(users == null){
			Log.e(TAG,"users null!");
			return;
		}
		for(Map.Entry<String,UserInfo> entry: users.entrySet())	{			
			if(!mListMember.contains(entry.getKey())){ 
				mListUser.add(entry.getValue());		
			}
		}
		
		Collections.sort(mListUser, new Comparator<UserInfo>() {
			@Override
			public int compare(final UserInfo user1, final UserInfo user2) {					
				String str1,str2;
				str1= (user1.getNick()==null?user1.getName():user1.getNick());
				str2= (user2.getNick()==null?user2.getName():user2.getNick());
				return  ChnToSpell.MakeSpellCode(str1, ChnToSpell.TRANS_MODE_PINYIN_INITIAL) 
							.compareTo( ChnToSpell.MakeSpellCode(str2, ChnToSpell.TRANS_MODE_PINYIN_INITIAL)  );
			}
		});				
		
	}
	
	private void InviteGroup(ArrayList<String> list){   
		
		TIMValueCallBack<List<TIMGroupMemberResult>> cb = new TIMValueCallBack<List<TIMGroupMemberResult>>() {
            @Override
            public void onError(int code, String desc) {
                Log.e(TAG, "invited to group failed: " + code + " desc:" + desc);
                if(code == 10004){
                	desc = "该好友注册后还未登陆过，不能添加";
                }
                Toast.makeText(getBaseContext(), "邀请加人失败:" + code + ":" + desc, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<TIMGroupMemberResult> arg0) {
                Log.d(TAG, "invited group succ: " + arg0.size());   
				Intent intent = new Intent(InviteToGroupActivity.this, ChatNewActivity.class);
				intent.putExtra("chatType", ChatNewActivity.CHATTYPE_GROUP);		      	     
			    intent.putExtra("groupID", mStrGroupID);	
		    	intent.putExtra("groupName",mStrGroupName);	
		    	intent.putExtra("groupType",mStrGroupType);
		       	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    startActivity(intent);			
            }
        };


        TIMGroupManager.getInstance().inviteGroupMember(mStrGroupID, list, cb);		
	}
	public void onBack(View view){	
		finish();
	}
	

}
