package com.example.mydemo.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.mydemo.R;
import com.example.mydemo.TLSHelper;
import com.example.mydemo.c2c.UserInfo;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.example.mydemo.utils.SNSChangeEntity;
import com.tencent.TIMFriendAddResponse;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMSNSChangeInfo;
import com.tencent.TIMSNSSystemElem;
import com.tencent.TIMSNSSystemType;
import com.tencent.TIMValueCallBack;

public class BlackListAdapter extends BaseAdapter {


   private static final String TAG = BlackListAdapter.class.getSimpleName();
	//private List<TIMGroupBaseInfo> listGroup;
   private List<String> blackList;
	private LayoutInflater inflater;

	public BlackListAdapter(Context context, List<String > list) {
		// TODO Auto-generated constructor stub
		this.blackList = list;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return blackList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return blackList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_new_friend, null);
			holder = new ViewHolder();		
			holder.userID = (TextView) convertView.findViewById(R.id.friend_name);
			holder.avatar = (ImageView) convertView.findViewById(R.id.img_avatar);
			holder.friendName  = (TextView) convertView.findViewById(R.id.tv_content);
			
			
			convertView.setTag(holder);			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String name = blackList.get(position);;			
		holder.userID.setText(name);		
		String strName ;
		com.example.mydemo.c2c.UserInfo user= UserInfoManagerNew.getInstance().getContactsList().get(name);
		holder.friendName.setText(user!=null?user.getDisplayUserName():name);
	    return convertView;
	}
	
	
	private static class ViewHolder {
		TextView friendName;
		TextView userID;
		ImageView avatar;
}		
 
}


