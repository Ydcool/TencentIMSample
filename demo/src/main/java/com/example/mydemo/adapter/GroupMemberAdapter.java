package com.example.mydemo.adapter;

import java.util.List;


import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mydemo.R;
import com.example.mydemo.TLSHelper;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.example.mydemo.utils.Constant;
public class GroupMemberAdapter extends BaseAdapter{
	private static final String TAG = GroupMemberAdapter.class.getSimpleName(); 
	private List<String> listMemeber;
	private LayoutInflater inflater;
	SparseIntArray positionOfSection;
	SparseIntArray sectionOfPosition;
	List<String> list;
	public GroupMemberAdapter(Context context, List<String> list) {
		// TODO Auto-generated constructor stub
		this.listMemeber = list;
		inflater = LayoutInflater.from(context);		

	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listMemeber.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listMemeber.get(position);
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
			convertView = inflater.inflate(R.layout.item_group_member, null);
			holder = new ViewHolder();		
			holder.userName = (TextView) convertView.findViewById(R.id.tv_username);		
			holder.avatar = (ImageView) convertView.findViewById(R.id.im_avatar);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final String name = listMemeber.get(position);
		if(name == Constant.INVITE_FRIEND_TO_GROUP){
			holder.avatar.setImageResource(R.drawable.add_friend);
			holder.userName.setText("");
		}
		else if(name == Constant.DELETE_GROUP_MEMBER){
			holder.avatar.setImageResource(R.drawable.delete_grp_member);
			holder.userName.setText("");
		}else{
			holder.userName.setVisibility(View.VISIBLE);
//			String strName ;
//			if(name.equals(TLSHelper.userID)){
//				strName = UserInfoManagerNew.getInstance().getNickName();
//			}else{
//				com.example.mydemo.c2c.UserInfo user= UserInfoManagerNew.getInstance().getContactsList().get(name);
//				strName = (user!=null?user.getDisplayUserName():name);
//			}
			holder.userName.setText(UserInfoManagerNew.getInstance().getDisplayName(name));
		}
			
		return convertView;
	}
		
	private static class ViewHolder {
		TextView userName;
		ImageView avatar;
	}	
	
}


