package com.example.mydemo.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.mydemo.MyApplication;
import  com.example.mydemo.R;
import com.example.mydemo.TLSHelper;
import com.example.mydemo.adapter.GroupMemberOperaAdapter;
import com.example.mydemo.adapter.GroupMemberOperaAdapter.ViewHolder;
//import com.example.mydemo.adapter.CreateGroupContactdapter.ViewHolder;
import com.example.mydemo.c2c.UserInfo;
import com.example.mydemo.c2c.UserInfoManagerNew;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.mydemo.utils.ChnToSpell;
import com.example.mydemo.utils.Constant;
//import com.example.mydemo.utils.PingYinUtil;
import com.tencent.TIMGroupManager;
import com.tencent.TIMValueCallBack;


 public class CreateGroupActivity  extends MyBaseActivity {
	private final static String TAG = CreateGroupActivity.class.getSimpleName();
	private  List<UserInfo> mListUser;
	private ListView mLVUser;
	private EditText mETGroupName;
	private Button mBtnCreateGroup;
	private String mStrGroupType;
	public GroupMemberOperaAdapter mAdapter;
	public static int HANDLE_MSG_GROUPINFO = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);		
		initView();	
	}
	
	public void initView() {	
		mStrGroupType = getIntent().getStringExtra("groupType");
		mETGroupName = (EditText) findViewById(R.id.et_groupname);
		mBtnCreateGroup = (Button) findViewById(R.id.btn_create_group);
		mLVUser= (ListView)findViewById(R.id.lv_contacts);		
		mListUser = new ArrayList<UserInfo>();
		
		TextView tvName = (TextView) findViewById(R.id.chat_name);
		if(mStrGroupType.equals(Constant.TYPE_PRIVATE_GROUP)){
			tvName.setText("创建讨论组");
		}else if(mStrGroupType.equals(Constant.TYPE_CHAT_ROOM)){
			tvName.setText("创建聊天室");
		}else if(mStrGroupType.equals(Constant.TYPE_PUBLIC_GROUP)){
			tvName.setText("创建群");
		}
		loadContactsContent();
		mAdapter = new GroupMemberOperaAdapter(getBaseContext(),mListUser);		
		mLVUser.setAdapter(mAdapter);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mBtnCreateGroup.setOnClickListener( new OnClickListener(){

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
				if(MyApplication.getInstance().bHostRelaytionShip){
					lUsers.add(TLSHelper.userID);
				}else{
					lUsers.add(MyApplication.getInstance().getUserName());
				}
				String strGroupName =  mETGroupName.getText().toString().trim();
				if(strGroupName.isEmpty()){
					Toast.makeText(getBaseContext(), "请输入群名称",Toast.LENGTH_SHORT).show();
					return;
				}
				try{
					byte[] byte_num = strGroupName.getBytes("utf8");
					if(byte_num.length > Constant.MAX_GROUP_NAME_LENGTH){
						Toast.makeText(getBaseContext(), "群名称不能超过" + Constant.MAX_GROUP_NAME_LENGTH + "个字符",Toast.LENGTH_SHORT).show();
						return;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
								
				if(lUsers.size() >= 3){
					createGroup(lUsers);
				}else{
					Toast.makeText(getBaseContext(), "请选择至少两个好友",Toast.LENGTH_SHORT).show();
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
		Map<String, UserInfo> users;
		//if(MyApplication.getInstance().bHostRelaytionShip){
			users= UserInfoManagerNew.getInstance().getContactsList();
//		}else{
//			users= UserInfoManager.getInstance().getContactsList();
//		}
		if(users == null){
			Log.e(TAG,"users null!");
			return;
		}
		for(Map.Entry<String,UserInfo> entry: users.entrySet())	{
			mListUser.add(entry.getValue());				 
		}
		
		Collections.sort(mListUser, new Comparator<UserInfo>() {
			@Override
			public int compare(final UserInfo user1, final UserInfo user2) {					
				String str1,str2;
				str1= (user1.getNick()==null?user1.getName():user1.getNick());
				str2= (user2.getNick()==null?user2.getName():user2.getNick());
				return ChnToSpell.MakeSpellCode(str1, ChnToSpell.TRANS_MODE_PINYIN_INITIAL)
									.compareTo( ChnToSpell.MakeSpellCode(str2, ChnToSpell.TRANS_MODE_PINYIN_INITIAL) );
			}
		});				
		
	}
	
	private void createGroup(ArrayList<String> list){   
		String strGroupName = mETGroupName.getText().toString().trim();
		try{
			byte[] byte_num = strGroupName.getBytes("utf8");
			if(byte_num.length > Constant.MAX_GROUP_NAME_LENGTH){
				Toast.makeText(getBaseContext(), "消息太长，最多" + Constant.MAX_GROUP_NAME_LENGTH + "个字符",Toast.LENGTH_SHORT).show();
				return;
			}
		}catch(Exception e){
			e.printStackTrace();
			return;
		}	
		
        TIMGroupManager.getInstance().createGroup(mStrGroupType,list, strGroupName,
	        		new TIMValueCallBack<String>() {
	            @Override
	            public void onError(int code, String desc) {
	                Log.e(TAG, "create group failed: " + mStrGroupType + ":" + code + " desc");
	                String errmsg="创建群失败:" + code + ":" + desc;
	                if(code ==80001){
	                	errmsg = "创建群失败.群名称为敏感词，请更换重新创建。";
	                }
	                Toast.makeText(getBaseContext(),errmsg , Toast.LENGTH_SHORT).show();
	            }
	
	            @Override
	            public void onSuccess(String s) {
	                Log.d(TAG, "create group succ: " + mStrGroupType + ":" + s);
	                UserInfoManagerNew.getInstance().UpdateGroupID2Name(s,mETGroupName.getText().toString().trim(),mStrGroupType,true);
					
					Intent intent = new Intent(CreateGroupActivity.this, ChatNewActivity.class);
				    intent.putExtra("chatType", ChatNewActivity.CHATTYPE_GROUP);		     
				    intent.putExtra("groupID", s);	
				    intent.putExtra("groupType",mStrGroupType);
			    	intent.putExtra("groupName", mETGroupName.getText().toString().trim());
			    	
			    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				    startActivity(intent);		
	            }
	        });		
	}
	public void onBack(View view){	
		finish();
	}
	

}
