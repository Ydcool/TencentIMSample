package com.example.mydemo.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.mydemo.R;
import com.example.mydemo.utils.GroupInfo;
import com.tencent.TIMGroupBaseInfo;

public class GroupListAdapter extends BaseAdapter {


   private static final String TAG = GroupListAdapter.class.getSimpleName();
	//private List<TIMGroupBaseInfo> listGroup;
   private List<GroupInfo> listGroup;
	private LayoutInflater inflater;

	public GroupListAdapter(Context context, List<GroupInfo> list) {
		// TODO Auto-generated constructor stub
		this.listGroup = list;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listGroup.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listGroup.get(position);
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
			convertView = inflater.inflate(R.layout.contact_group_item, null);
			holder = new ViewHolder();		
			holder.groupName = (TextView) convertView.findViewById(R.id.friend_name);
			holder.avatar = (ImageView) convertView.findViewById(R.id.img_avatar);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.groupName.setText(listGroup.get(position).getName());		
	    return convertView;
	}
	
	
	private static class ViewHolder {
		TextView groupName;
		ImageView avatar;
}		
 
}


