package com.example.mydemo.adapter;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.mydemo.activity.EditGroupNickNameActivity;
import com.example.mydemo.activity.GroupInfoActivity;
import com.example.mydemo.activity.GroupListActivity;
import com.example.mydemo.activity.SetSlienceTimeActivity;
import com.example.mydemo.c2c.UserInfo;
import com.example.mydemo.c2c.UserInfoManager;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberRoleType;
import com.example.mydemo.activity.GroupMemberManageActivity;

public class GroupMemberManageAdapter extends BaseAdapter {


   private static final String TAG = GroupMemberManageAdapter.class.getSimpleName();
	//private List<TIMGroupBaseInfo> listGroup;
   private List<TIMGroupMemberInfo> listMember;
	private LayoutInflater inflater;
	private  Activity activity;
	private String mStrGroupID;
	private TIMGroupMemberRoleType myRoleType;

	public GroupMemberManageAdapter(Context context, String groupID,List<TIMGroupMemberInfo> list) {
		// TODO Auto-generated constructor stub
		this.listMember = list;
		mStrGroupID = groupID;	
		activity=(Activity)context;
		inflater = LayoutInflater.from(context);
	}
	
	public void setMyRole(TIMGroupMemberRoleType role){
		myRoleType = role;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listMember.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listMember.get(position);
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
			convertView = inflater.inflate(R.layout.item_member_manage, null);
			holder = new ViewHolder();		
			holder.memberName = (TextView) convertView.findViewById(R.id.member_name);
			holder.nickName = (TextView) convertView.findViewById(R.id.nick_name);
			holder.avatar = (ImageView) convertView.findViewById(R.id.img_avatar);
			holder.btSetRole = (Button) convertView.findViewById(R.id.bt_set_role);
			holder.btSetSilence = (Button) convertView.findViewById(R.id.bt_set_silence);
			holder.btSetNameCard =(Button) convertView.findViewById(R.id.bt_set_namecard);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final String name = listMember.get(position).getUser();
		String strName,strType ;
		if(name.equals(TLSHelper.userID)){
			strName = UserInfoManagerNew.getInstance().getNickName();
		}else{
			com.example.mydemo.c2c.UserInfo user= UserInfoManagerNew.getInstance().getContactsList().get(name);
			strName = (user!=null?user.getDisplayUserName():name);
		}	
		holder.memberName.setText(strName);	
		
		final TIMGroupMemberInfo memberInfo = listMember.get(position);
		holder.nickName.setText(memberInfo.getNameCard());
		holder.btSetRole.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Log.d(TAG,"btSetRole:" + TLSHelper.userID + ":" +  myRoleType  + ":"+ name);
				if(name.equals(TLSHelper.userID)){
					Toast.makeText(activity, "用户不能修改自己的角色!", Toast.LENGTH_LONG).show();
					return;			
				}
				
				if(myRoleType == TIMGroupMemberRoleType.Owner){
					SetRoleNormal(memberInfo,holder);
				}else{
					Toast.makeText(activity, "只有群创建者才有权限设置!", Toast.LENGTH_LONG).show();				
				}	
			}
			
		});
		
		holder.btSetSilence.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Log.d(TAG,"btSetSilence:" + TLSHelper.userID  +":"+  myRoleType + ":" + name + ":" + memberInfo.getRole() );
				if(name.equals(TLSHelper.userID)){
					Toast.makeText(activity, "用户不能禁言自己!", Toast.LENGTH_LONG).show();
					return;			
				}
				//禁言的角色要大于被禁言的角色
				if( myRoleType==TIMGroupMemberRoleType.Owner || 
						( (myRoleType==TIMGroupMemberRoleType.Admin) && (memberInfo.getRole()== TIMGroupMemberRoleType.Normal)) ){				
				
					Intent intent = new Intent(activity,SetSlienceTimeActivity.class);
					intent.putExtra("groupID", mStrGroupID);
					intent.putExtra("memberID", memberInfo.getUser());
					activity.startActivity(intent);
				}else{
					Toast.makeText(activity, "您没有权限禁言该用户!", Toast.LENGTH_LONG).show();					
				}
			}
			
		});			
		
		holder.btSetNameCard.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {

				//禁言的角色要大于被禁言的角色
				if( name.equals(TLSHelper.userID) || myRoleType==TIMGroupMemberRoleType.Owner || 
						( (myRoleType==TIMGroupMemberRoleType.Admin) && (memberInfo.getRole()== TIMGroupMemberRoleType.Normal)) ){	
					// TODO Auto-generated method stub
	    	    	Intent intent = new Intent(activity,EditGroupNickNameActivity.class);    	    	
	    	    	intent.putExtra("groupID", mStrGroupID);
	    	    	intent.putExtra("userID",memberInfo.getUser());
	    	    	intent.putExtra("groupNameCard",holder.nickName.getText().toString() );
	    	    	activity.startActivity(intent);	
				}else{
					Toast.makeText(activity, "您没有权限修改该用户名片!", Toast.LENGTH_LONG).show();					
				}
			}
			
		});		
		
		if(TIMGroupMemberRoleType.Owner == listMember.get(position).getRole()){
			strType="创建者";			
		}else if (TIMGroupMemberRoleType.Admin == listMember.get(position).getRole()){
			strType="管理者";
		}else if (TIMGroupMemberRoleType.Normal == listMember.get(position).getRole()){
			strType="普通成员";
		}else{
			strType="非成员";
		}
		holder.btSetRole.setText(strType);	
	    return convertView;
	}
	
	private void SetRoleNormal(TIMGroupMemberInfo userInfo,final ViewHolder holder){
			final TIMGroupMemberInfo user = userInfo;
			 final TIMGroupMemberRoleType tobeset ;
			String tips;
        	if (TIMGroupMemberRoleType.Admin == user.getRole()){
        		tobeset = TIMGroupMemberRoleType.Normal;
        		tips = "确定要取消管理员资格吗？";
			}else if (TIMGroupMemberRoleType.Normal == user.getRole()){
				tobeset = TIMGroupMemberRoleType.Admin;
				tips = "确定要设置为管理员吗?";
			}else{
				return;
			}
        	
	       Dialog alertDialog = new AlertDialog.Builder(activity). 
	                setTitle("提示"). 
	                setMessage(tips). 			       
	                setNegativeButton("确定", new DialogInterface.OnClickListener() { 
	                     
	                    @Override 
	                    public void onClick(DialogInterface dialog, int which) { 
	                        // TODO Auto-generated method stub  

	                    	TIMGroupManager.getInstance().modifyGroupMemberInfoSetRole(mStrGroupID,user.getUser(),tobeset,new TIMCallBack(){

								@Override
								public void onError(int arg0, String arg1) {
									// TODO Auto-generated method stub
									Log.e(TAG,"modifyGroupMemberInfoSetRole error:" + arg0 +":" +arg1);
									if(arg0 == 10004){
										Toast.makeText(activity, "讨论组不能设置管理员", Toast.LENGTH_SHORT).show();
									}
								}

								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									Log.d(TAG,"modifyGroupMemberInfoSetRole succ:" + tobeset);
									((GroupMemberManageActivity) activity).loadContent();									
							       	if (TIMGroupMemberRoleType.Normal == tobeset){
							       		holder.btSetRole.setText("普通成员");
									}else {
										holder.btSetRole.setText("管理员");
									}							       	
								}
	                    		
	                    	});
	                    	} 
	                }). 
	                setPositiveButton("取消", new DialogInterface.OnClickListener() { 
	                     
	                    @Override 
	                    public void onClick(DialogInterface dialog, int which) { 
	                        // TODO Auto-generated method stub  
	                    } 
	                }). 			               
	                create(); 
	        alertDialog.show(); 	     
			 
	}
	private static class ViewHolder {
		TextView memberName;
		TextView nickName;
		Button btSetRole;
		Button btSetSilence;
		Button btSetNameCard;
		ImageView avatar;
}		
 
}


