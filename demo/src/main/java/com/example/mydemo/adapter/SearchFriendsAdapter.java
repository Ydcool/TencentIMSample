package com.example.mydemo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydemo.R;
import com.example.mydemo.TLSHelper;
import com.example.mydemo.activity.AddFriendInfoActivity;
import com.example.mydemo.activity.AddFriendNewActivity;
import com.example.mydemo.utils.FriendInfo;
import com.example.mydemo.utils.GroupInfo;
import com.tencent.TIMAddFriendRequest;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendStatus;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMValueCallBack;

public class SearchFriendsAdapter extends BaseAdapter {


   private static final String TAG = SearchFriendsAdapter.class.getSimpleName();
	//private List<TIMGroupBaseInfo> listGroup;
   private List<FriendInfo> listFriends;
	private LayoutInflater inflater;
	private Context context;
	private Activity activity;
	public SearchFriendsAdapter(Context context, List<FriendInfo> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.listFriends = list;
		activity = (Activity)context;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listFriends.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listFriends.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {	
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_search_friend, null);
			holder = new ViewHolder();		
			holder.friendName = (TextView) convertView.findViewById(R.id.tv_friend_name);
			holder.avatar = (ImageView) convertView.findViewById(R.id.img_avatar);
			holder.addFriend = (Button) convertView.findViewById(R.id.btn_add_friend);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final FriendInfo info = listFriends.get(position);
		holder.addFriend.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(info.getID().equals(TLSHelper.userID)){
					Toast.makeText(context, "不能加自己为好友!", Toast.LENGTH_SHORT).show();
					return;
				}
				if(info.getNeddVerify()){
					Intent intent = new Intent(activity,AddFriendInfoActivity.class);
					intent.putExtra("userID",info.getID());
					//intent.putExtra("needVerify", mNeedVerify);
					activity.startActivity(intent);
				}else{
					List<TIMAddFriendRequest> reqList = new ArrayList<TIMAddFriendRequest>();
					TIMAddFriendRequest friend = new TIMAddFriendRequest();
					friend.setIdentifier(info.getID());
					friend.setRemark("qq");
					friend.setAddrSource("qq");
					reqList.add(friend);
					TIMFriendshipManager.getInstance().addFriend(reqList, new TIMValueCallBack<List<TIMFriendResult>>(){

						@Override
						public void onError(int arg0, String arg1) {
							// TODO Auto-generated method stub
							Log.e(TAG,"add friend error:" + arg0 + ":" + arg1);
							activity.finish();
						}

						@Override
						public void onSuccess(List<TIMFriendResult> arg0) {
							// TODO Auto-generated method stub
							Log.d(TAG,"add friend response" );
							for( TIMFriendResult arg : arg0){
								Log.d(TAG, "add friend  result:" + arg.getIdentifer() + arg.getStatus());
								if(arg.getStatus() == TIMFriendStatus.TIM_ADD_FRIEND_STATUS_FRIEND_SIDE_FORBID_ADD){
									Toast.makeText(context, "该用户不允许添加好友!", Toast.LENGTH_SHORT).show();
								}
							}
							activity.finish();
						}
						
					});
				}
			}
			
		});
		String name = info.getName();
		if(name == null || name.length()==0){
			name = info.getID();
		}
		holder.friendName.setText(name);		
	    return convertView;
	}
	

	private static class ViewHolder {
		TextView friendName;
		ImageView avatar;
		Button addFriend;
}		
 
}


