package com.example.mydemo.adapter;

import java.math.BigInteger;
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
import com.example.mydemo.c2c.UserInfo;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.example.mydemo.utils.GroupInfo;
import com.example.mydemo.utils.SNSChangeEntity;
import com.tencent.TIMCallBack;
import com.tencent.TIMElemType;
import com.tencent.TIMFriendAddResponse;
import com.tencent.TIMFriendFutureItem;
import com.tencent.TIMFriendPendencyItem;
import com.tencent.TIMFriendResponseType;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendStatus;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMFutureFriendType;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupSystemElemType;
import com.tencent.TIMPendencyGetType;
import com.tencent.TIMSNSChangeInfo;
import com.tencent.TIMSNSSystemElem;
import com.tencent.TIMSNSSystemType;
import com.tencent.TIMValueCallBack;
import com.tencent.imcore.FriendPendencyItem;

public class NewFriendsAdapter extends BaseAdapter {


   private static final String TAG = NewFriendsAdapter.class.getSimpleName();
	//private List<TIMGroupBaseInfo> listGroup;
   private List<TIMFriendFutureItem> listNewFriend;
	private LayoutInflater inflater;

	public NewFriendsAdapter(Context context, List<TIMFriendFutureItem > list) {
		// TODO Auto-generated constructor stub
		this.listNewFriend = list;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listNewFriend.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listNewFriend.get(position);
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
			holder.friendName = (TextView) convertView.findViewById(R.id.friend_name);
			holder.avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
			holder.accept = (Button) convertView.findViewById(R.id.btn_accept_friend);
			holder.addWords = (TextView) convertView.findViewById(R.id.tv_content);			
			convertView.setTag(holder);			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		TIMFriendFutureItem entity = listNewFriend.get(position);
		holder.friendName.setText(entity.getIdentifier());
		holder.addWords.setText(entity.getAddWording());
		holder.accept.setVisibility(View.VISIBLE);
		dispalyC2CSystem(holder,entity);
//			String id = entity.getMessage().getIdentifier();		
//			holder.friendName.setText(UserInfoManagerNew.getInstance().getDisplayName(id));
//			String addWord = entity.getMessage().getWording();
//			holder.addWords.setText(addWord);
//			holder.accept.setVisibility(View.VISIBLE);
//			Log.d(TAG,"view:" + id + ":" +  entity.getType() + ":" + entity.getSubType() + ":" +addWord);			
//			dispalyC2CSystem(holder,entity);
				
	    return convertView;
	}
	
	private void dispalyC2CSystem(final ViewHolder holder,final TIMFriendFutureItem entity){
		
		if(entity.getType() == TIMFutureFriendType.TIM_FUTURE_FRIEND_PENDENCY_OUT_TYPE){
			holder.accept.setText("待验证");
			holder.accept.setBackgroundResource(R.drawable.accepted_friend);
			holder.accept.setClickable(false);
		}else if(entity.getType() == TIMFutureFriendType.TIM_FUTURE_FRIEND_PENDENCY_IN_TYPE  ){
			holder.accept.setText("接受");
			//holder.accept.setBackgroundColor(0x555555);
			holder.accept.setBackgroundResource(R.drawable.wait_accept_friend);		
			holder.accept.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					reuestAddFriend(holder,entity);
				}  
			   
		   });				
		}else if(entity.getType() == TIMFutureFriendType.TIM_FUTURE_FRIEND_RECOMMEND_TYPE ){
			holder.accept.setText("添加");
			holder.accept.setBackgroundResource(R.drawable.accepted_friend);	
			holder.accept.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					reuestAddFriend(holder,entity);					
				}  
			   
		   });				
			
		}else if(entity.getType() == TIMFutureFriendType.TIM_FUTURE_FRIEND_DECIDE_TYPE){
			holder.accept.setText("已添加");
			holder.accept.setBackgroundResource(R.drawable.accepted_friend);
			holder.accept.setClickable(false);		
		}
	}
	private void reuestAddFriend(final ViewHolder holder,final TIMFriendFutureItem entity){
		TIMFriendAddResponse  response = new TIMFriendAddResponse();
		response.setIdentifier(entity.getIdentifier());
		response.setRemark("response");
		response.setType(TIMFriendResponseType.AgreeAndAdd);
		TIMFriendshipManager.getInstance().addFriendResponse(response, new TIMValueCallBack<TIMFriendResult>(){

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.e(TAG,"addFriendResponse error:" + arg0 + ":" + arg1);						
			}

			@Override
			public void onSuccess(TIMFriendResult arg0) {
				// TODO Auto-generated method stub
				
				Log.d(TAG,"addFriendResponse succ:" + arg0.getIdentifer() + ":" + arg0.getStatus());
				if(arg0.getStatus() == TIMFriendStatus.TIM_FRIEND_STATUS_SUCC){
					holder.accept.setText("已添加");
					holder.accept.setBackgroundResource(R.drawable.accepted_friend);
					//维护本地缓存的关系链
					UserInfoManagerNew.getInstance().UpdateContactList(arg0.getIdentifer());
				
				}
			}
			
		});	
	}
	private static class ViewHolder {
		TextView friendName;
		TextView addWords;
		ImageView avatar;
		Button accept;
}		
 
}


