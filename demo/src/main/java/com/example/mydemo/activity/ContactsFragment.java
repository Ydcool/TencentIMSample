package com.example.mydemo.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.example.mydemo.MyApplication;
import  com.example.mydemo.R;
import com.example.mydemo.adapter.ContactsListAdapter;
import com.example.mydemo.c2c.HttpProcessor;
import com.example.mydemo.c2c.UserInfo;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.example.mydemo.utils.ChnToSpell;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.example.mydemo.utils.Constant;

 public class ContactsFragment extends Fragment {
	private static final String TAG = ContactsFragment.class.getSimpleName();
	private  List<UserInfo> contactList;

	public final static int FOR_GOTO_ADDFRIEND = 2; 
	private ContactsListAdapter adapter;
	private boolean mHidden = false;
	private static StringBuilder mStrRetMsg = new StringBuilder();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fg_contacts, container, false);
		contactList = new ArrayList<UserInfo>();
		initView(view);
		return view;
	}

	public void initView(View view) {
		ListView lvContacts = (ListView) view.findViewById(R.id.list_contacts);
		Button btAddFriend = (Button) view.findViewById(R.id.btn_goto_add_friend);
//		if(MyApplication.getInstance().getThirdIdLogin()){
//			btAddFriend.setVisibility(View.GONE);
//		}else{
			btAddFriend.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				//	if(MyApplication.getInstance().bHostRelaytionShip){
						startActivity(new Intent(getActivity(), AddFriendNewActivity.class));					
//					}else{
//						startActivity(new Intent(getActivity(), AddFriendActivity.class));
//					}
				}
			});
	//	}

//		loadContactsContent();
		adapter = new ContactsListAdapter(getActivity(),contactList);
		lvContacts.setAdapter(adapter);		
		lvContacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final UserInfo entity = (UserInfo) adapter.getItem(position);
				if(Constant.PUBLIC_GROUP_USERNAME.equals(entity.getName())){
				    Intent intent = new Intent(getActivity(), GroupListActivity.class);		
				    intent.putExtra("groupType", Constant.TYPE_PUBLIC_GROUP);
				    startActivity(intent);
				}else if(Constant.PRIVATE_GROUP_USERNAME.equals(entity.getName())){
				    Intent intent = new Intent(getActivity(), GroupListActivity.class);		
				    intent.putExtra("groupType", Constant.TYPE_PRIVATE_GROUP);
				    startActivity(intent);
				}else if(Constant.CHAT_ROOM_USERNAME.equals(entity.getName())){
				    Intent intent = new Intent(getActivity(), GroupListActivity.class);
				    intent.putExtra("groupType", Constant.TYPE_CHAT_ROOM);
				    startActivity(intent);
				}else if(Constant.SYSTEM_TIPS_USERNAME.equals(entity.getName())){		
					startActivity(new Intent(getActivity(), SystemTipsActivity.class));
				}else if(Constant.NEW_FRIENDS_USERNAME.equals(entity.getName())){		
					startActivity(new Intent(getActivity(), NewFriendsActivity.class));
				}else{			
				    Intent intent = new Intent(getActivity(), ChatNewActivity.class);
				    intent.putExtra("chatType", ChatNewActivity.CHATTYPE_C2C);
	                intent.putExtra("userName", entity.getName());			
				    startActivity(intent);
				}
			}
		});
	}	
	
	public void  loadContactsContent()
	{			
		contactList.clear();	
		
		Map<String, UserInfo> users;
		users = UserInfoManagerNew.getInstance().getContactsList();
		if(users == null){
			Log.e(TAG,"users null!");
			return;
		}
		for(Map.Entry<String,UserInfo> entry: users.entrySet())	{
			contactList.add(entry.getValue());		
			Log.d(TAG,"user id:" + entry.getValue().getName());
		}
		
		Collections.sort(contactList, new Comparator<UserInfo>() {
			@Override
			public int compare(final UserInfo user1, final UserInfo user2) {					
				String str1,str2;
				str1= (user1.getNick()==null?user1.getName():user1.getNick());
				str2= (user2.getNick()==null?user2.getName():user2.getNick());
				return ChnToSpell.MakeSpellCode(str1, ChnToSpell.TRANS_MODE_PINYIN_INITIAL).toLowerCase()
												.compareTo( ChnToSpell.MakeSpellCode(str2, ChnToSpell.TRANS_MODE_PINYIN_INITIAL).toLowerCase() );
			}
		});	
	
		UserInfo chatRoomUsers = new UserInfo();                  
		chatRoomUsers.setName(Constant.CHAT_ROOM_USERNAME);
		chatRoomUsers.setNick(Constant.CHAT_ROOM_NICK);    
        contactList.add(0, chatRoomUsers);				

		UserInfo privateGroupUsers = new UserInfo();                  
		privateGroupUsers.setName(Constant.PRIVATE_GROUP_USERNAME);
		privateGroupUsers.setNick(Constant.PRIVATE_GROUP_NICK);    
        contactList.add(0, privateGroupUsers);		
        
		 UserInfo publicGroupUsers = new UserInfo();                  
		 publicGroupUsers.setName(Constant.PUBLIC_GROUP_USERNAME);
		 publicGroupUsers.setNick(Constant.PUBLIC_GROUP_NICK);    
         contactList.add(0, publicGroupUsers);		
         
         UserInfo systemTips = new UserInfo();
         systemTips.setName(Constant.SYSTEM_TIPS_USERNAME);
         systemTips.setNick(Constant.SYSTEM_TIPS_NICK);
         systemTips.setUnRead(UserInfoManagerNew.getInstance().getUnReadSystem());
         Log.d(TAG,"getUnReadSystem:" + UserInfoManagerNew.getInstance().getUnReadSystem());
         contactList.add(0, systemTips);
         
         UserInfo newFriends = new UserInfo();
         newFriends.setName(Constant.NEW_FRIENDS_USERNAME);
         newFriends.setNick(Constant.NEW_FRIENDS_NICK);
         //newFriends.setUnRead(UserInfoManagerNew.getInstance().getUnReadSystem());
         Log.d(TAG,"getUnReadSystem:" + UserInfoManagerNew.getInstance().getUnReadSystem());
         contactList.add(0, newFriends);
         
         adapter.notifyDataSetChanged();	
         Log.d(TAG,"contact refresh");
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		mHidden = hidden;
	}
	
	@Override
	public void onResume() {		
		super.onResume();
		Log.d(TAG,"Resume" + mHidden);
		loadContactsContent();
	}	
	
	public void updateNewFriendUnReadNum(long num){
		for(UserInfo user : contactList){
			if(user.getName().equals("Constant.NEW_FRIENDS_USERNAME")){
				user.setUnRead(num);
				break;
			}
		}
		adapter.notifyDataSetChanged();	
	}
	
	 private void getContactsFromServer(){
			new Thread( new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub		
					final int iRet = HttpProcessor.doRequestGetFriend(MyApplication.getInstance().getUserName(), mStrRetMsg);		
					if(getActivity() == null){
						return;
					}
					getActivity().runOnUiThread(new Runnable() {
						public void run(){
						if(iRet == 0){
							loadContactsContent();
							adapter.notifyDataSetChanged();							
						}else{					
							Toast.makeText(getActivity().getBaseContext(), "获取好友列表:" + iRet + ":" + mStrRetMsg, Toast.LENGTH_SHORT).show();
						}
					}
				});		
				}
				
			}).start();  	
		 }	
}
