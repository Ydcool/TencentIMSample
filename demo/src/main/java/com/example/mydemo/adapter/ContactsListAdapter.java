package com.example.mydemo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.mydemo.R;
import com.example.mydemo.c2c.UserInfo;

import com.example.mydemo.utils.Constant;


public class ContactsListAdapter extends BaseAdapter implements SectionIndexer{


	private static final String TAG = ContactsListAdapter.class.getSimpleName();	
	private List<UserInfo> listContacts;

	private LayoutInflater inflater;
	SparseIntArray positionOfSection;
	SparseIntArray sectionOfPosition;
	List<String> list;
	public ContactsListAdapter(Context context, List<UserInfo> list) {
		// TODO Auto-generated constructor stub
		this.listContacts = list;
		inflater = LayoutInflater.from(context);		

	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listContacts.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listContacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.contacts_item, null);
			holder = new ViewHolder();		
			holder.userName = (TextView) convertView.findViewById(R.id.friend_name);		
			holder.avatar = (ImageView)convertView.findViewById(R.id.iv_avatar);
			holder.unread_num = (TextView)convertView.findViewById(R.id.unread_msg_num);
			holder.head = (TextView)convertView.findViewById(R.id.tv_header);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.unread_num.setVisibility(View.INVISIBLE);
		final UserInfo entity = listContacts.get(position);		
		if(entity.getName().equals(Constant.PUBLIC_GROUP_USERNAME) 
				|| entity.getName().equals(Constant.PRIVATE_GROUP_USERNAME)
				|| entity.getName().equals(Constant.CHAT_ROOM_USERNAME)){			
			holder.avatar.setImageResource(R.drawable.icon_group);
			
			entity.setHeader("");
		}else if(entity.getName().equals(Constant.SYSTEM_TIPS_USERNAME)){
			if(entity.getUnRead() > 0){
				holder.unread_num.setVisibility(View.VISIBLE);
				holder.unread_num.setText(String.valueOf(entity.getUnRead()) );
			}
			holder.avatar.setImageResource(R.drawable.system_tips);
		}else{			
			holder.avatar.setImageResource(R.drawable.user_icon);
		}	
		
		String strHeader = entity.getHeader();		
		if(position == 0){
			 holder.head.setVisibility(View.GONE);
		}else if( strHeader != null && !strHeader.equals(((UserInfo)getItem(position-1)).getHeader())){
			if ("".equals(strHeader)) {
			    holder.head.setVisibility(View.GONE);
			} else {
			    holder.head.setVisibility(View.VISIBLE);
			    holder.head.setText(strHeader);
			}	
		}else{
			holder.head.setVisibility(View.GONE);
		}		

			
		if(entity.getNick()!=null && ! entity.getNick().trim().equals("")){
			holder.userName.setText(entity.getNick());
		}else {
			holder.userName.setText(entity.getName());
		}
	//	holder.unread_num.setVisibility(View.INVISIBLE);

		return convertView;
	}
		
	private static class ViewHolder {
		TextView userName;
		TextView unread_num;
		ImageView avatar;		
		TextView head;
	}	
	
	public int getPositionForSection(int section) {
		return positionOfSection.get(section);
	}

	public int getSectionForPosition(int position) {
		return sectionOfPosition.get(position);
	}
	
	@Override
	public Object[] getSections() {
		positionOfSection = new SparseIntArray();
		sectionOfPosition = new SparseIntArray();
		int count = getCount();
		list = new ArrayList<String>();
		list.add("test");
		positionOfSection.put(0, 0);
		sectionOfPosition.put(0, 0);
		for (int i = 1; i < count; i++) {

			String letter = ((UserInfo) getItem(i)).getHeader();
			int section = list.size() - 1;
			if (list.get(section) != null && !list.get(section).equals(letter)) {
				list.add(letter);
				section++;
				positionOfSection.put(section, i);
			}
			sectionOfPosition.put(i, section);
		}
		return list.toArray(new String[list.size()]);
	}
}


