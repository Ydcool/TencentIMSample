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
import com.tencent.TIMFriendResponseType;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendStatus;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupSystemElemType;
import com.tencent.TIMSNSChangeInfo;
import com.tencent.TIMSNSSystemElem;
import com.tencent.TIMSNSSystemType;
import com.tencent.TIMValueCallBack;

public class SystemTipsAdapter extends BaseAdapter {


   private static final String TAG = SystemTipsAdapter.class.getSimpleName();
	//private List<TIMGroupBaseInfo> listGroup;
   private List<SNSChangeEntity> listNewFriend;
	private LayoutInflater inflater;

	public SystemTipsAdapter(Context context, List<SNSChangeEntity > list) {
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
		
		SNSChangeEntity entity = listNewFriend.get(position);
		if(entity.getType() == TIMElemType.GroupSystem){
			holder.avatar.setImageResource(R.drawable.icon_group);
			String id = entity.getGroupMessage().getOpUser();
			holder.friendName.setText(UserInfoManagerNew.getInstance().getDisplayName(id));		
			Log.d(TAG,"view:" + id + ":" +  entity.getType() + ":" + entity.getGroupMessage().getSubtype() + ":" +entity.getGroupMessage().getOpReason());
			displayGroupSystem(holder,entity);
		}else {
			String id = entity.getMessage().getIdentifier();		
			holder.friendName.setText(UserInfoManagerNew.getInstance().getDisplayName(id));
			String addWord = entity.getMessage().getWording();
			holder.addWords.setText(addWord);
			holder.accept.setVisibility(View.VISIBLE);
			Log.d(TAG,"view:" + id + ":" +  entity.getType() + ":" + entity.getSubType() + ":" +addWord);			
			dispalyC2CSystem(holder,entity);
		}		
	    return convertView;
	}
	
	private void displayGroupSystem(final ViewHolder holder,SNSChangeEntity entity){
		final TIMGroupSystemElem elem = entity.getGroupMessage();
		GroupInfo info = UserInfoManagerNew.getInstance().getGroupID2Info().get(elem.getGroupId());
		String strGroupName = (info != null)?info.getName(): elem.getGroupId();
		if(elem.getSubtype() == TIMGroupSystemElemType.TIM_GROUP_SYSTEM_ADD_GROUP_ACCEPT_TYPE){
			holder.addWords.setText("同意你加入群" + strGroupName);
		}else if(elem.getSubtype() == TIMGroupSystemElemType.TIM_GROUP_SYSTEM_ADD_GROUP_REFUSE_TYPE){
			holder.addWords.setText("拒绝你加入群" + strGroupName);
		}else if(elem.getSubtype() == TIMGroupSystemElemType.TIM_GROUP_SYSTEM_ADD_GROUP_REQUEST_TYPE){
			holder.addWords.setText(entity.getGroupMessage().getOpReason());
			holder.accept.setVisibility(View.VISIBLE);
			holder.accept.setText("接受");
			//holder.accept.setBackgroundColor(0x555555);
			holder.accept.setBackgroundResource(R.drawable.wait_accept_friend);		
			holder.accept.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					elem.accept("同意", new TIMCallBack(){

						@Override
						public void onError(int arg0, String arg1) {
							// TODO Auto-generated method stub
							Log.e(TAG,"group accept error:" + arg0 + ":" + arg1);
						}

						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							Log.e(TAG,"group accept success");
							holder.accept.setText("已添加");
							holder.accept.setBackgroundResource(R.drawable.accepted_friend);
						}
						
					});
				}  
			   
		   });				
					
			
		}else if(elem.getSubtype() == TIMGroupSystemElemType.TIM_GROUP_SYSTEM_CANCEL_ADMIN_TYPE){
			holder.addWords.setText("取消你在群" + strGroupName +"的管理员角色");
		}else if(elem.getSubtype() == TIMGroupSystemElemType.TIM_GROUP_SYSTEM_CREATE_GROUP_TYPE){
			holder.addWords.setText("创建群" + strGroupName);
		}else if(elem.getSubtype() == TIMGroupSystemElemType.TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE){
			holder.addWords.setText("删除群"+strGroupName);
		}else if(elem.getSubtype() == TIMGroupSystemElemType.TIM_GROUP_SYSTEM_GRANT_ADMIN_TYPE){
			holder.addWords.setText("设为群"+strGroupName +"管理员身份");
		}else if(elem.getSubtype() == TIMGroupSystemElemType.TIM_GROUP_SYSTEM_KICK_OFF_FROM_GROUP_TYPE){
			holder.addWords.setText("将你移除群"+strGroupName);
		}else if(elem.getSubtype() == TIMGroupSystemElemType.TIM_GROUP_SYSTEM_QUIT_GROUP_TYPE){
			holder.addWords.setText("退出群"+strGroupName);
		}else if(elem.getSubtype() == TIMGroupSystemElemType.TIM_GROUP_SYSTEM_REVOKE_GROUP_TYPE){
			holder.addWords.setText("恢复群" +strGroupName);
		}else {
			holder.addWords.setText(strGroupName + ":" + elem.getOpReason());
		}			
			
	}
	
	private void dispalyC2CSystem(final ViewHolder holder,final SNSChangeEntity entity){
		if(entity.getSubType() == TIMSNSSystemType.TIM_SNS_SYSTEM_DEL_FRIEND){
			holder.accept.setText("已删除");
			holder.accept.setBackgroundResource(R.drawable.accepted_friend);
		}else if(entity.getSubType() == TIMSNSSystemType.TIM_SNS_SYSTEM_ADD_FRIEND ){				
			holder.accept.setText("已添加");
			holder.accept.setBackgroundResource(R.drawable.accepted_friend);				
		}else if(entity.getSubType() == TIMSNSSystemType.TIM_SNS_SYSTEM_ADD_FRIEND_REQ  ){
			holder.accept.setText("接受");
			//holder.accept.setBackgroundColor(0x555555);
			holder.accept.setBackgroundResource(R.drawable.wait_accept_friend);		
			holder.accept.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					TIMFriendAddResponse  response = new TIMFriendAddResponse();
					response.setIdentifier(entity.getMessage().getIdentifier());
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
			   
		   });				
		}	
	}
	
	private static class ViewHolder {
		TextView friendName;
		TextView addWords;
		ImageView avatar;
		Button accept;
}		
 
}


