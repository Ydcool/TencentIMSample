package com.example.mydemo.adapter;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;


import com.example.mydemo.R;
import com.example.mydemo.activity.ChatMenuActivity;
import com.example.mydemo.activity.ChatNewActivity;
import com.example.mydemo.activity.DisplayOrgPicActivity;
import com.example.mydemo.activity.DisplayVideoActivity;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.example.mydemo.MyApplication;
import com.example.mydemo.utils.ChatEntity;
import com.example.mydemo.utils.Constant;
import com.example.mydemo.utils.DateHelper;
import com.example.mydemo.utils.EmojiUtil;
import com.tencent.TIMCallBack;
import com.tencent.TIMElem;
import com.tencent.TIMFileElem;
import com.tencent.TIMConversationType;
import com.tencent.TIMGroupTipsElemGroupInfo;
import com.tencent.TIMGroupTipsGroupInfoType;
import com.tencent.TIMImage;
import com.tencent.TIMImageType;
import com.tencent.TIMGroupTipsElem;
import com.tencent.TIMGroupTipsType;
import com.tencent.TIMImageElem;
import com.tencent.TIMMessageStatus;
import com.tencent.TIMSNSSystemElem;
import com.tencent.TIMSNSSystemType;
import com.tencent.TIMSnapshot;
import com.tencent.TIMSoundElem;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;
import com.tencent.TIMElemType;
import com.tencent.TIMVideo;
import com.tencent.TIMVideoElem;
public class ChatMsgListAdapter extends BaseAdapter {

	private static String TAG = ChatMsgListAdapter.class.getSimpleName();
	private static final int ITEMCOUNT = 11;
	private List<ChatEntity> listMessage = null;
	private LayoutInflater inflater;
	public static final int TYPE_TEXT_SEND = 0;
	public static final int TYPE_TEXT_RECV = 1;
	public static final int TYPE_IMAGE_SEND = 2;
	public static final int TYPE_IMAGE_RECV = 3;
	public static final int TYPE_FILE_SEND = 4;
	public static final int TYPE_FILE_RECV = 5;
	public static final int TYPE_SOUND_SEND = 6;
	public static final int TYPE_SOUND_RECV = 7;
	public static final int TYPE_GROUP_TIPS = 8;
	public static final int TYPE_VIDEO_SEND = 9;
	public static final int TYPE_VIDEO_RECV = 10;
	private  Context context;
	private  Activity activity;
	public static MediaPlayer   mPlayer = null;  
	private  boolean mIsVoicePalying = false;
	private ImageView currentPalyingIV ;
	private AnimationDrawable currentAnimation;
	private ProgressDialog progressDialog;
//	private  AnimationDrawable animateDrawable;
	private int playPos = 0;
	private static final int HANDLER_MESSAGE_REFRESH_LIST = 1;
	private static final int HANDLER_MESSAGE_SEEK_TO = 2;
	public ChatMsgListAdapter(Context context, List<ChatEntity> messages) {
		this.listMessage = messages;	
		this.context = context;
		this.activity = (Activity)context;	
		inflater = LayoutInflater.from(context);

	}

	public int getCount() {
		if(listMessage!=null){			
			return listMessage.size();
		}
		return 0;
	}

	public Object getItem(int position) {
		if(listMessage!=null){		
			return listMessage.get(position);
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	
	public int getItemViewType(int position) {		
		ChatEntity entity = listMessage.get(position);		
		if(entity.getElem().getType()==TIMElemType.Text || entity.getElem().getType()==TIMElemType.SNSTips){
			return entity.getIsSelf() ? TYPE_TEXT_SEND : TYPE_TEXT_RECV;
		} else if(entity.getElem().getType()==TIMElemType.Image){
			return entity.getIsSelf() ? TYPE_IMAGE_SEND : TYPE_IMAGE_RECV;
		}else if(entity.getElem().getType()==TIMElemType.File){
			return entity.getIsSelf()? TYPE_FILE_SEND : TYPE_FILE_RECV;
		}else if(entity.getElem().getType()==TIMElemType.Sound){
			return entity.getIsSelf() ? TYPE_SOUND_SEND : TYPE_SOUND_RECV;
		}else if(entity.getElem().getType() == TIMElemType.GroupTips){
			return TYPE_GROUP_TIPS;
		}else if(entity.getElem().getType()==TIMElemType.Video){
			return entity.getIsSelf() ? TYPE_VIDEO_SEND : TYPE_VIDEO_RECV;
		}
		return -1;			
	}


	public int getViewTypeCount() {
		return ITEMCOUNT;
	}
	
	private View dynamicCreateView(int position) {
		int type = getItemViewType(position); 
		switch(type){
		case TYPE_TEXT_SEND:
			return inflater.inflate(R.layout.chat_item_text_right, null);
		case TYPE_TEXT_RECV:
			return inflater.inflate(R.layout.chat_item_text_left, null);
		case TYPE_IMAGE_SEND:
			return inflater.inflate(R.layout.chat_item_pic_right, null) ;
		case TYPE_IMAGE_RECV:
			return inflater.inflate(R.layout.chat_item_pic_left, null);
		case TYPE_FILE_SEND:
			return inflater.inflate(R.layout.chat_item_file_right, null);
		case TYPE_FILE_RECV:
			return inflater.inflate(R.layout.chat_item_file_left, null);	
		case TYPE_SOUND_SEND:
			return inflater.inflate(R.layout.chat_item_ptt_right, null);
		case TYPE_SOUND_RECV:	
			return  inflater.inflate(R.layout.chat_item_ptt_left, null);	
		case TYPE_GROUP_TIPS:
			return  inflater.inflate(R.layout.chat_item_group_tips, null);	
		case TYPE_VIDEO_SEND:
			return inflater.inflate(R.layout.chat_item_video_right, null);
		case TYPE_VIDEO_RECV:
			return inflater.inflate(R.layout.chat_item_video_left, null);			
		default:
			return inflater.inflate(R.layout.chat_item_text_right, null);			
		}
	
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		ChatEntity entity = listMessage.get(position);
		final TIMElem elem = entity.getElem();
		
		ViewHolder viewHolder = null;
		if (convertView == null) {			
			convertView = dynamicCreateView(position);			
			viewHolder = new ViewHolder();
			viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			if(elem.getType() == TIMElemType.GroupTips){
				viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_msg_content);
			}else{
				viewHolder.avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
				viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
				viewHolder.tvUserName.setWidth(350);
				viewHolder.tvUserName.setEllipsize(TruncateAt.END);
				viewHolder.pbSending = (ProgressBar)convertView.findViewById(R.id.pb_status);
				if(entity.getIsSelf()){
					viewHolder.ivMsgStatus = (ImageView)convertView.findViewById(R.id.iv_msg_status);					
				}	
				
				if(elem.getType()== TIMElemType.Text){		
					viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
					
				}
				else if(elem.getType()== TIMElemType.Image){
					viewHolder.ivContent = (ImageView)convertView.findViewById(R.id.iv_chat_item_content);
					viewHolder.rl_pic_new_content = (RelativeLayout)convertView.findViewById(R.id.rl_pic_new_content);
				//	viewHolder.pbSending = (ProgressBar)convertView.findViewById(R.id.pb_status);
				}
				else if(elem.getType()== TIMElemType.File){
					viewHolder.tvContent = (TextView)convertView.findViewById(R.id.tv_file_name);
					viewHolder.tvFileSize = (TextView)convertView.findViewById(R.id.tv_file_size);
					viewHolder.rl_file_content = (RelativeLayout) convertView.findViewById(R.id.rl_chat_item_content);
					if(!entity.getIsSelf()){
						viewHolder.tvSaveState = (TextView) convertView.findViewById(R.id.tv_state); 
					}
				}	
				else if(elem.getType()== TIMElemType.Sound){				
					viewHolder.tvContent = (TextView)convertView.findViewById(R.id.tv_total_time);
					viewHolder.ivContent = (ImageView)convertView.findViewById(R.id.iv_chat_item_content);
					viewHolder.rlPttContent = (RelativeLayout)convertView.findViewById(R.id.rl_chat_item_content);
				}else if(elem.getType()== TIMElemType.SNSTips){
					viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);		
				}else if(elem.getType() == TIMElemType.Video){
					viewHolder.ivContent = (ImageView)convertView.findViewById(R.id.iv_chat_item_content);
					viewHolder.rl_pic_new_content = (RelativeLayout)convertView.findViewById(R.id.rl_pic_new_content);
				}
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if(position==0){
			viewHolder.tvSendTime.setText(DateHelper.GetStringFormat(entity.getTime()));
		}else {		
			if (DateHelper.LongInterval(entity.getTime(), listMessage.get(position - 1).getTime())) {
				viewHolder.tvSendTime.setText(DateHelper.GetStringFormat(entity.getTime()));
				viewHolder.tvSendTime.setVisibility(View.VISIBLE);
			} else {
				viewHolder.tvSendTime.setVisibility(View.GONE);
			}
		}	
		if(elem.getType() != TIMElemType.GroupTips ){
			if(entity.getType() == TIMConversationType.Group){
				String sendName;
				if(entity.getIsSelf()){
					sendName=UserInfoManagerNew.getInstance().getNickName();
				}else{
					com.example.mydemo.c2c.UserInfo user= UserInfoManagerNew.getInstance().getContactsList().get(entity.getSenderName());
					sendName = (user!=null?user.getDisplayUserName():entity.getSenderName());
				}
				
				viewHolder.tvUserName.setText(sendName);
			}else{
				viewHolder.tvUserName.setVisibility(View.GONE);
			}
			viewHolder.avatar.setImageResource(R.drawable.chat_default_avatar);
		}
		Log.d(TAG, "msg status:" + entity.getStatus());
		if((viewHolder.ivMsgStatus != null) && (entity.getStatus() == TIMMessageStatus.SendFail)){
			
			viewHolder.ivMsgStatus.setVisibility(View.VISIBLE);
		}		
		else if (viewHolder.ivMsgStatus != null){
			viewHolder.ivMsgStatus.setVisibility(View.GONE);
		}
		if(elem.getType()== TIMElemType.Text){
			DisplayTextMsg(elem,viewHolder,position,entity.getStatus());
		}else if(elem.getType()== TIMElemType.Image){			
			DisplayPicMsg(elem,entity.getIsSelf(),entity.getStatus(),viewHolder,position);
		}else if(elem.getType()== TIMElemType.File){
			DisplayFileMsg(elem,entity.getIsSelf(),viewHolder,position,entity.getStatus());
		}else if(elem.getType()== TIMElemType.Sound){
			DisplayPttMsg(elem,entity.getIsSelf(),viewHolder,position,entity.getStatus());
		}else if(elem.getType() == TIMElemType.GroupTips){
			DisplayGroupTips(elem,viewHolder,position);
		}else if(elem.getType() == TIMElemType.SNSTips){
			DisplaySNSTips(elem,viewHolder,position);
		}else if(elem.getType() == TIMElemType.Video){
			DisplayVideoMsg(elem,entity.getIsSelf(),entity.getStatus(),viewHolder,position);
		}
		
//		if((elem.getType() != TIMElemType.GroupTips) || (elem.getType() != TIMElemType.SNSTips)){
//			viewHolder.tvContent.setOnLongClickListener(new OnLongClickListener(){
//
//				@Override
//				public boolean onLongClick(View v) {
//					// TODO Auto-generated method stub
//					Intent intent = new Intent(activity,ChatMenuActivity.class);
//					intent.putExtra("item", position);
//					if(elem.getType()== TIMElemType.Text){
//						intent.putExtra("type", ChatNewActivity.FOR_CHAT_TEXT_MENU);
//						activity.startActivityForResult(intent,ChatNewActivity.FOR_CHAT_TEXT_MENU);
//					}else{
//						intent.putExtra("type", ChatNewActivity.FOR_CHAT_IMAGE_MENU);
//						activity.startActivityForResult(intent,ChatNewActivity.FOR_CHAT_IMAGE_MENU);						
//					}
//					Log.d(TAG,"menu,put item:" + position);
//					
//					return true;
//				}
//				
//			});
//		}
		return convertView;
	}

	static class ViewHolder {
		public TextView tvSendTime;
		public TextView tvUserName;
		public TextView tvContent;
		public ImageView ivContent;
		public ImageView avatar;
		public ImageView ivMsgStatus;
		public TextView tvFileSize;	
		public RelativeLayout rlPttContent;
		public RelativeLayout rl_file_content;
		public ProgressBar pbSending;
		public TextView tvSaveState;
		public RelativeLayout rl_pic_new_content;
	}

	
	private void DisplaySNSTips(TIMElem elem,ViewHolder viewHolder,final int position){
		TIMSNSSystemElem tipsElem = (TIMSNSSystemElem)elem;
		//已经加为好友，展现在聊天窗口
		if( tipsElem.getSubType() == TIMSNSSystemType.TIM_SNS_SYSTEM_ADD_FRIEND){
			viewHolder.tvContent.setText("我通过了你的好友验证请求，现在我们可以开始聊天了");
		}else{
			Log.d(TAG,"sns tips:" +tipsElem.getSubType() );
		}
			
	}
	
	private void DisplayTextMsg(TIMElem elem,ViewHolder viewHolder,final int position,final TIMMessageStatus status){
		TIMTextElem textElem = (TIMTextElem)elem;
		SpannableString spannableString = EmojiUtil.getInstace().getSpannableString(context, textElem.getText());
		viewHolder.tvContent.setText(spannableString);			

    	if(status == TIMMessageStatus.Sending){
        //	viewHolder.ivContent.setVisibility(View.VISIBLE);
		//	viewHolder.ivContent.setImageResource(R.drawable.defaultpic);
			viewHolder.pbSending.setVisibility(View.VISIBLE);
    		return;
    	}
    	viewHolder.pbSending.setVisibility(View.GONE);
    	
		viewHolder.tvContent.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(activity,ChatMenuActivity.class);
				intent.putExtra("item", position);
				intent.putExtra("type", ChatNewActivity.FOR_CHAT_TEXT_MENU);
				if(status == TIMMessageStatus.SendFail ){
					intent.putExtra("needReSend", true);
				}else{
					intent.putExtra("needReSend", false);
				}
				activity.startActivityForResult(intent,ChatNewActivity.FOR_CHAT_TEXT_MENU);
				Log.d(TAG,"menu,put item:" + position);
				
				return true;
			}
			
		});
	}
	
	private void DisplayGroupTips(TIMElem elem,ViewHolder viewHolder,int position){
		TIMGroupTipsElem tipsElem = (TIMGroupTipsElem)elem;
		String strTmp = new String();
		Log.d(TAG,"DisplayGroupTips:" + tipsElem.getOpUser() + ":" + tipsElem.getTipsType() );
		String strOpUser = UserInfoManagerNew.getInstance().getDisplayName(tipsElem.getOpUser());
		if(tipsElem.getTipsType() == TIMGroupTipsType.Join){
			strTmp = strOpUser + "邀请";
			for(int i=0;i<tipsElem.getUserList().size();i++){
				strTmp += UserInfoManagerNew.getInstance().getDisplayName(tipsElem.getUserList().get(i));
				if(i!= (tipsElem.getUserList().size()-1)){
					strTmp += ",";	
				}
			}
			strTmp += "加入了群聊";					
		}else if(tipsElem.getTipsType() == TIMGroupTipsType.ModifyGroupInfo){
		//	strTmp = tipsElem.getOpUser() + "修改群名称为" + tipsElem.getGroupName();
			strTmp = strOpUser;
			Log.d(TAG,"ModifyGroupInfo:" + tipsElem.getGroupInfoList().size());
			for(int i=0;i<tipsElem.getGroupInfoList().size();i++){
				TIMGroupTipsElemGroupInfo info = tipsElem.getGroupInfoList().get(i);
				Log.d(TAG,info.getType() + ":" + info.getContent());
				if(info.getType() == TIMGroupTipsGroupInfoType.ModifyName){
					strTmp += "修改群名称为";				
				}else if(info.getType() == TIMGroupTipsGroupInfoType.ModifyIntroduction){
					strTmp += "修改群简介为";
				}else if (info.getType() == TIMGroupTipsGroupInfoType.ModifyNotification){
					strTmp += "修改群公告为";
				}else{
					strTmp += "修改群资料为";
				}
				strTmp += info.getContent();
			}
			
		}else if(tipsElem.getTipsType() == TIMGroupTipsType.Quit){			
			strTmp = strOpUser + "退出群聊";
		}else if(tipsElem.getTipsType() == TIMGroupTipsType.Kick){	
			for(int i=0;i<tipsElem.getUserList().size();i++){
				strTmp += UserInfoManagerNew.getInstance().getDisplayName(tipsElem.getUserList().get(i));
				if(i!= (tipsElem.getUserList().size()-1)){
					strTmp += ",";	
				}
			}			
			strTmp += "被踢出群";			
		}else if(tipsElem.getTipsType() == TIMGroupTipsType.SetAdmin){	
			strTmp = strOpUser + "设置";
			Log.d(TAG,"set admin:" + tipsElem.getUserList().size());
			for(int i=0;i<tipsElem.getUserList().size();i++){
				strTmp += UserInfoManagerNew.getInstance().getDisplayName(tipsElem.getUserList().get(i));
				if(i!= (tipsElem.getUserList().size()-1)){
					strTmp += ",";	
				}
			}			
			strTmp += "为管理员"; 
		}else if(tipsElem.getTipsType() == TIMGroupTipsType.CancelAdmin){	
			strTmp = strOpUser + "取消";
			for(int i=0;i<tipsElem.getUserList().size();i++){
				strTmp += UserInfoManagerNew.getInstance().getDisplayName(tipsElem.getUserList().get(i));
				if(i!= (tipsElem.getUserList().size()-1)){
					strTmp += ",";	
				}
			}			
			strTmp += "管理员身份"; 
		}else if(tipsElem.getTipsType() == TIMGroupTipsType.ModifyMemberInfo){
			strTmp = strOpUser +"禁言";
			for(int i=0;i<tipsElem.getUserList().size();i++){
				strTmp += UserInfoManagerNew.getInstance().getDisplayName(tipsElem.getUserList().get(i));
				if(i!= (tipsElem.getUserList().size()-1)){
					strTmp += ",";	
				}				
			}			
			 			
		}
		
		viewHolder.tvContent.setText(strTmp);	
	}
	
	private void SaveMap(String filePath,byte[] bytes){
		try {  
		    File file = new File(filePath);
		    if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
	        FileOutputStream fops = new FileOutputStream(file);
	        fops.write(bytes);
	        fops.flush();
	        fops.close();        
	           
	        
         } catch (IOException e) {  
             e.printStackTrace();  
             Log.e(TAG,e.toString());
          
         }
				
	}
	public static Bitmap GetRightOritationNew(String filePath){
		Bitmap bitmap  = BitmapFactory.decodeFile(filePath);		
		ExifInterface exif = null;	
		try{
			exif = new ExifInterface(filePath);  
		}catch (IOException e) {  
            e.printStackTrace();  
            Log.e(TAG,e.toString());
            exif = null;  
            return bitmap;
        }			
		int degree=0;
		if (exif != null) { 
	         int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,  
	                 ExifInterface.ORIENTATION_UNDEFINED);  
	
	         switch (ori) {  
	         case ExifInterface.ORIENTATION_ROTATE_90:  
	        	 degree = 90;  
	             break;  
	         case ExifInterface.ORIENTATION_ROTATE_180:  
	        	 degree = 180;  
	             break;  
	         case ExifInterface.ORIENTATION_ROTATE_270:  
	        	 degree = 270;  
	             break;  
	         default:  
	        	 degree = 0;  
	             break;  
	         }		    
		}		
		if (degree != 0) {  
			Log.d(TAG,"degree:"+degree);
             // 旋转图片  
             Matrix m = new Matrix();  
             m.postRotate(degree);  
             bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);   
         }		
		Log.d(TAG,"degree:"+degree);
		return bitmap;		
	}
	
	private void DisplayPicMsg(TIMElem e,boolean isSelf,final TIMMessageStatus status,ViewHolder holder,final int position){
        final TIMImageElem elem = (TIMImageElem)e; 
        final ViewHolder viewHolder = holder;
        Log.d(TAG,"DisplayPicMsg:");


		viewHolder.ivContent.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(activity,ChatMenuActivity.class);
				intent.putExtra("item", position);
				intent.putExtra("type", ChatNewActivity.FOR_CHAT_IMAGE_MENU);
				if(status == TIMMessageStatus.SendFail ){
					intent.putExtra("needReSend", true);
				}else{
					intent.putExtra("needReSend", false);
				}
				activity.startActivityForResult(intent,ChatNewActivity.FOR_CHAT_IMAGE_MENU);	
				Log.d(TAG,"pic menu,put item:" + position);
				
				return true;
			}
			
		});
		
    	viewHolder.ivContent.setVisibility(View.GONE);
    	if(status == TIMMessageStatus.Sending){
        	viewHolder.ivContent.setVisibility(View.VISIBLE);
			viewHolder.ivContent.setImageResource(R.drawable.defaultpic);
			viewHolder.pbSending.setVisibility(View.VISIBLE);
    		return;
    	}else if(status == TIMMessageStatus.SendFail){
    		viewHolder.ivContent.setVisibility(View.VISIBLE);
    		viewHolder.ivContent.setImageResource(R.drawable.defaultpic);
    		viewHolder.pbSending.setVisibility(View.GONE);
    		return;
    	}else{
    		viewHolder.pbSending.setVisibility(View.GONE);
    	}
    	
    	for(TIMImage image : elem.getImageList()){
    	    Log.d(TAG, "image type: " + image.getType()  +
                    " image size: " + image.getSize()  +
                    " image height: " + image.getHeight() + 
                    " image width: " + image.getWidth());
    	    //先展示缩略图
    	    if(image.getType() == TIMImageType.Thumb){
    	    	final String filePath = Constant.TH_IMG_CACHE_DIR  + image.getUuid() + ".jpg";
    	    	File thumbFile = new File(filePath);
    	    	if(thumbFile.exists()){
    	    		viewHolder.ivContent.setVisibility(View.VISIBLE);
					viewHolder.ivContent.setImageBitmap(GetRightOritationNew(filePath));
    	    	}else{
		            image.getImage(new TIMValueCallBack<byte[]>() {
		                @Override
		                public void onError(int code, String desc) {//获取图片失败
		                    //服务器返回了错误码code和错误描述desc，可用于定位请求失败原因
		                    //错误码code列表请参见附录
		                    Log.e(TAG, "getThumbPic failed. code: " + code + " errmsg: " + desc);		
		              //      Toast.makeText(activity, "获取缩略图失败. code: " + code + " errmsg: " + desc, Toast.LENGTH_SHORT).show();
	      	              	viewHolder.ivContent.setVisibility(View.VISIBLE);
	      					viewHolder.ivContent.setImageResource(R.drawable.defaultpic);          					
		                }
		
		                @Override
		                public void onSuccess(byte[] data) {
		                    //doSomething
		                    
                        	SaveMap(filePath,data);
                        	viewHolder.ivContent.setVisibility(View.VISIBLE);    			
                  //      	Bitmap bitmap = GetRightOritationNew(filePath);  
                        	viewHolder.ivContent.setImageBitmap(GetRightOritationNew(filePath));   
                        	notifyDataSetChanged();
                        	
//                        	android.view.ViewGroup.LayoutParams params = viewHolder.rl_chat_item_content.getLayoutParams();
//                        	params.height=bitmap.getHeight();
//                        	params.width =bitmap.getWidth();
//                        	Log.d(TAG, "getThumbPic success. data size: " + data.length + "height:" + bitmap.getHeight());
//                        	viewHolder.rl_chat_item_content.setLayoutParams(params);
//                        	
		                }
		            });
	    	    }
	            break;
    	    }
    	    
    	}

    	holder.ivContent.setClickable(true); 
    	holder.ivContent.setOnClickListener(new OnClickListener(){  
			@Override
			public void onClick(View v) {				
				  progressDialog = ProgressDialog.show(activity, "加载中...", "请稍后...", true, false);  
				  progressDialog.setCancelable(true);
				// TODO Auto-generated method stub
				  
			    	for(TIMImage image : elem.getImageList()){
			    	    Log.d(TAG, "image type: " + image.getType() +
			                    " image size " + image.getSize() +
			                    " image height " + image.getHeight() +
			                    " image width " + image.getWidth());
			    	    //先展示缩略图
			    	    if(image.getType() == TIMImageType.Original){			    	    	
			    	    	final String filePath = Constant.ORG_IMG_CACHE_DIR  + image.getUuid() + ".jpg";
			    	    	File orgFile = new File(filePath);
			    	    	if(orgFile.exists()){	
			    	    		Intent intent = new Intent(activity,DisplayOrgPicActivity.class);
	                        	intent.putExtra("filePath", filePath);
	                        	activity.startActivity(intent);
	                        	progressDialog.dismiss();		
			    	    	}else{
					            image.getImage(new TIMValueCallBack<byte[]>() {
					                @Override
					                public void onError(int code, String desc) {
					                	//获取图片失败
					                    //服务器返回了错误码code和错误描述desc，可用于定位请求失败原因
					                    //错误码code列表请参见附录
					                    Log.e(TAG, "getOriginPic failed. code: " + code + " errmsg: " + desc);
					                    Toast.makeText(activity, "获取原图失败。 code: " + code + " errmsg: " + desc, Toast.LENGTH_SHORT).show();
			          					progressDialog.dismiss();
					                }
					
					                @Override
					                public void onSuccess(byte[] data) {//成功，参数为图片数据
					                    //doSomething
					                    Log.d(TAG, "getOriginPic success. data size: " + data.length);
					                 //   final byte[] info = data;     
//					            		activity.runOnUiThread(new Runnable() {
//					    					public void run(){      	               
					                        //	
			                        	SaveMap(filePath,data);				                        
			                        	Intent intent = new Intent(activity,DisplayOrgPicActivity.class);
			                        	intent.putExtra("filePath", filePath);
			                        	activity.startActivity(intent);
			                        	progressDialog.dismiss();		
			                        	ChatNewActivity.bFromOrgPic = true;
					    						
//					    					}
//					            		});	        	                    
					                }
					            });
				    	    }
				            break;
			    	    }			    	    
			    	}	
			}
    		 
    	});   
    
	}
	
	private void DisplayFileMsg(TIMElem e,final boolean isSelf,ViewHolder holder,final int position,final TIMMessageStatus status){
		final TIMFileElem elem = (TIMFileElem)e;		
        final ViewHolder viewHolder = holder;
        if(!isSelf){
        	String fileName = Constant.FILE_DIR + elem.getUuid();
        	File tmpFile = new File(fileName);
        	if(tmpFile.exists()){
        		viewHolder.tvSaveState.setText("已保存");
        	}else{
        		viewHolder.tvSaveState.setText("未保存");
        	}
        }
        viewHolder.tvFileSize.setText(String.valueOf(elem.getFileSize()/1024 + "k")); 
		viewHolder.tvContent.setText(elem.getFileName());
		
//        	elem.getFile(new TIMValueCallBack<byte []>(){
//            @Override
//            public void onError(int code, String desc) {
//                Log.d(TAG, "getFile failed. code: " + code + " errmsg: " + desc);
//          		activity.runOnUiThread(new Runnable() {
//    					public void run(){   
//    						viewHolder.tvFileSize.setText("未知"); 
//    						viewHolder.tvContent.setText("未知");
//    					}
//            		});	                 
//            }
//
//            @Override
//            public void onSuccess(byte [] info) {
//        		activity.runOnUiThread(new Runnable() {
//					public void run(){   
//						Log.d(TAG,"size:" + elem.getFileSize() + ":" + elem.getFileName());
//						viewHolder.tvFileSize.setText(String.valueOf(elem.getFileSize()/1024 + "k")); 
//						viewHolder.tvContent.setText(elem.getFileName());
//					}
//        		});	         	
//            }
//        });  

        	if(status == TIMMessageStatus.Sending){
            //	viewHolder.ivContent.setVisibility(View.VISIBLE);
    		//	viewHolder.ivContent.setImageResource(R.drawable.defaultpic);
    			viewHolder.pbSending.setVisibility(View.VISIBLE);
        		return;
        	}
        	viewHolder.pbSending.setVisibility(View.GONE);
    		viewHolder.rl_file_content.setOnLongClickListener(new OnLongClickListener(){

    			@Override
    			public boolean onLongClick(View v) {
    				// TODO Auto-generated method stub
    				Intent intent = new Intent(activity,ChatMenuActivity.class);
    				intent.putExtra("item", position);
    				intent.putExtra("type", ChatNewActivity.FOR_CHAT_IMAGE_MENU);
    				if(status == TIMMessageStatus.SendFail ){
    					intent.putExtra("needReSend", true);
    				}else{
    					intent.putExtra("needReSend", false);
    				}
    				if(!isSelf){
    					intent.putExtra("needSave", true);
    				}
    				activity.startActivityForResult(intent,ChatNewActivity.FOR_CHAT_IMAGE_MENU);	
    				Log.d(TAG,"file menu,put item:" + position);
    				
    				return true;
    			}
    			
    		});
	}		
	
	private void stopCurrentPttMedia(boolean bSelf){
		if(mPlayer!=null){
	    	mPlayer.stop();
	    	mPlayer.release();
	    	mPlayer =null;
		}
    	currentAnimation.stop();
		if(bSelf){
			currentPalyingIV.setImageResource(R.drawable.skin_aio_ptt_record_user_nor);
		}else{
			currentPalyingIV.setImageResource(R.drawable.skin_aio_ptt_record_friend_nor);
		}	
		mIsVoicePalying = false;
	}
	private void DisplayPttMsg(TIMElem e,boolean isSelf,ViewHolder holder,final int position,final TIMMessageStatus status){
		final TIMSoundElem elem = (TIMSoundElem)e;
		final boolean bSelf=isSelf;
		final int tmpPs = position;
		//临时显示下时间，后期sdk加上				
		holder.tvContent.setText(elem.getDuration() + "'");
		final ImageView im = holder.ivContent;
		//正在播放的过程中，有新的ppt消息发送过来。需要继续播放。（currentPalyingIV指向的item已经变为下一个了。。动画也要重新生成）
		if(position == playPos && mIsVoicePalying){
			currentPalyingIV=im;
			currentAnimation =null;
			int animationResId ;
			if(bSelf){
				animationResId= R.anim.mystop;					
			}else{
				animationResId= R.anim.stop;
			}
			AnimationDrawable ani =(AnimationDrawable) context.getResources().getDrawable(animationResId);			
			im.setImageDrawable(ani);
			ani.start();
			currentAnimation= ani;
		}else{
			if(bSelf){
				im.setImageResource(R.drawable.skin_aio_ptt_record_user_nor);
			}else{
				im.setImageResource(R.drawable.skin_aio_ptt_record_friend_nor);
			}		
		}
	  	holder.rlPttContent.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {	
				//点击正在播放的ptt，停止
				if(currentPalyingIV == im && mIsVoicePalying){
					stopCurrentPttMedia(bSelf);					
					return;
				}				
				   elem.getSound(new TIMValueCallBack<byte []>(){
		            @Override
		            public void onError(int code, String desc) {
		                Log.e(TAG, "getSound failed. code: " + code + " errmsg: " + desc);		             
		            }

		            @Override
		            public void onSuccess(byte[] info) {
		            	Log.d(TAG," getSound succ");
		            	final byte[] bytes = info;
		            	
		        		activity.runOnUiThread(new Runnable() {
							public void run(){  
							   	String filePath = MyApplication.getInstance().getFilesDir().getAbsolutePath() + "/ptt/tmp_ptt.amr";	   		
							    
					    		try {	    			
					    			File file = new File(filePath);
					    			if (!file.getParentFile().exists()) {
					    				file.getParentFile().mkdirs();
					    			}
					    	        FileOutputStream fops = new FileOutputStream(file);
					    	        fops.write(bytes);
					    	        fops.flush();
					    	        fops.close();
					    	        Log.d(TAG,"voice status:" + mIsVoicePalying);					    	        
									    	        
					    	        if(mIsVoicePalying){
					    	        	stopCurrentPttMedia(bSelf);
					    	        }
					    	        mPlayer = new MediaPlayer();  	    	     
				    	            mPlayer.setDataSource(filePath);  
				    	            mPlayer.prepare(); 
				    	            mPlayer.start();   
				    	            mIsVoicePalying =true;
				
									int animationResId ;
									if(bSelf){
										animationResId= R.anim.mystop;					
									}else{
										animationResId= R.anim.stop;
									}
									final AnimationDrawable animateDrawable = (AnimationDrawable) context.getResources().getDrawable(animationResId);
					    	        	currentPalyingIV = im;
					    	        	currentAnimation = animateDrawable;
					    	        	playPos = position;
					    	        	
									im.setImageDrawable(animateDrawable);
									Log.d(TAG,"anmination status:" + tmpPs + ":" +animateDrawable.isRunning());
									currentAnimation.start();																	
									mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
										@Override
										public void onCompletion(MediaPlayer mp) {										
											// TODO Auto-generated method stub
											mIsVoicePalying =false;
											if(mPlayer != null){
												mPlayer.release();
												mPlayer = null;
											}
										//	animateDrawable.stop(); 
											currentAnimation.stop();
											if(bSelf){
												currentPalyingIV.setImageResource(R.drawable.skin_aio_ptt_record_user_nor);
											}else{
												currentPalyingIV.setImageResource(R.drawable.skin_aio_ptt_record_friend_nor);
											}
										}
									});					
								   	            
				    	        }catch(IllegalArgumentException e){
				    	        	Log.e(TAG, "ptt paly  failed" + e.toString());
				    	        } catch (IOException e) {  
				    	            Log.e(TAG, "ptt paly  failed" + e.toString());  
				    	        }  
							}	
								
						});		        		      	
		            }
		        });   
    	}
		
	});
	  	

    	if(status == TIMMessageStatus.Sending){
        //	viewHolder.ivContent.setVisibility(View.VISIBLE);
		//	viewHolder.ivContent.setImageResource(R.drawable.defaultpic);
    		holder.pbSending.setVisibility(View.VISIBLE);
    		return;
    	}
    	holder.pbSending.setVisibility(View.GONE);
    	
	  	holder.rlPttContent.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(activity,ChatMenuActivity.class);
				intent.putExtra("item", position);
				intent.putExtra("type", ChatNewActivity.FOR_CHAT_IMAGE_MENU);
				if(status == TIMMessageStatus.SendFail ){
					intent.putExtra("needReSend", true);
				}else{
					intent.putExtra("needReSend", false);
				}
				activity.startActivityForResult(intent,ChatNewActivity.FOR_CHAT_IMAGE_MENU);	
				Log.d(TAG,"ptt menu,put item:" + position);
				
				return true;
			}
			
		});
	}

	
	private void DisplayVideoMsg(TIMElem e,boolean isSelf,final TIMMessageStatus status,ViewHolder holder,final int position){
        final TIMVideoElem elem = (TIMVideoElem)e; 
        final ViewHolder viewHolder = holder;
        
		viewHolder.ivContent.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(activity,ChatMenuActivity.class);
				intent.putExtra("item", position);
				intent.putExtra("type", ChatNewActivity.FOR_CHAT_IMAGE_MENU);
				if(status == TIMMessageStatus.SendFail ){
					intent.putExtra("needReSend", true);
				}else{
					intent.putExtra("needReSend", false);
				}
				activity.startActivityForResult(intent,ChatNewActivity.FOR_CHAT_IMAGE_MENU);	
				Log.d(TAG,"video menu,put item:" + position);
				
				return true;
			}
			
		});		
		
    	if(status == TIMMessageStatus.Sending){
        	viewHolder.ivContent.setVisibility(View.VISIBLE);
			viewHolder.ivContent.setImageResource(R.drawable.defaultpic);
			viewHolder.pbSending.setVisibility(View.VISIBLE);
    		return;
    	}else if(status == TIMMessageStatus.SendFail){
    		viewHolder.ivContent.setVisibility(View.VISIBLE);
    		viewHolder.ivContent.setImageResource(R.drawable.defaultpic);
    		viewHolder.pbSending.setVisibility(View.GONE);
    		return;
    	}else{
    		viewHolder.pbSending.setVisibility(View.GONE);
    	}
    	
    	
        TIMSnapshot  snap = elem.getSnapshotInfo();
        

    	final String filePath = Constant.VEDIO_DIR  + snap.getUuid() + ".jpg";
    	File thumbFile = new File(filePath);
    	if(thumbFile.exists()){
    		viewHolder.ivContent.setVisibility(View.VISIBLE);
			viewHolder.ivContent.setImageBitmap(GetRightOritationNew(filePath));
    	}else{
    		snap.getImage(filePath,new TIMCallBack() {
                @Override
                public void onError(int code, String desc) {
                    Log.e(TAG, "getThumbPic failed. code: " + code + " errmsg: " + desc);		
  	              	viewHolder.ivContent.setVisibility(View.VISIBLE);
  					viewHolder.ivContent.setImageResource(R.drawable.defaultpic);          					
                }

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					Log.d(TAG,"get video thumb image succ");
		         	viewHolder.ivContent.setVisibility(View.VISIBLE);
		        	viewHolder.ivContent.setImageBitmap(GetRightOritationNew(filePath));   
		         

//		         	Drawable[] layers = new Drawable[2]; 
//		         	layers[0] = new BitmapDrawable(GetRightOritationNew(filePath)); 
//		         	layers[1] = new BitmapDrawable(BitmapFactory.decodeResource(activity.getResources(),R.drawable.qav_gvideo_camera_normal));
//		         	LayerDrawable layerDrawable = new LayerDrawable(layers);
//		         	layerDrawable.setLayerInset(0, 0, 0, 0, 0);  
//		         	layerDrawable.setLayerInset(1, 20, 20, 20, 20);  
//		         	viewHolder.ivContent.setImageDrawable(layerDrawable);
	                notifyDataSetChanged();
				}
            });
	    }     
        

   
    	holder.ivContent.setOnClickListener(new OnClickListener(){  
			@Override
			public void onClick(View v) {				
				  progressDialog = ProgressDialog.show(activity, "加载中...", "请稍后...", true, false);  
				  progressDialog.setCancelable(true);
				  TIMVideo video =  elem.getVideoInfo();		  
				  String typeName=null;
				  if(video.getType().equals("THREE_GPP")){
					  typeName =  ".3gp";
				  }else if(video.getType().equals("MOV")){
					  typeName =  ".mov";
				  }else if(video.getType().equals("mp4")){
					  typeName =  ".mp4";
				  }else{
					  Log.e(TAG,"error format:" + video.getType());
					  return;
				  }
				  final String  filePath = Constant.VEDIO_DIR  + video.getUuid() + typeName;
		    	    	File orgFile = new File(filePath);
		    	    	if(orgFile.exists()){	
		    	    		Intent intent = new Intent(activity,DisplayVideoActivity.class);
                      	intent.putExtra("filePath", filePath);
                      	activity.startActivity(intent);
                      	progressDialog.dismiss();		
		    	    	}else{
		    	    		video.getVideo(filePath, new TIMCallBack() {
				                @Override
				                public void onError(int code, String desc) {
				                	//获取图片失败
				                    //服务器返回了错误码code和错误描述desc，可用于定位请求失败原因
				                    //错误码code列表请参见附录
				                    Log.e(TAG, "get video failed. code: " + code + " errmsg: " + desc);
				                    Toast.makeText(activity, "获取视频失败。 code: " + code + " errmsg: " + desc, Toast.LENGTH_SHORT).show();
		          					progressDialog.dismiss();
				                }
				
				                @Override
				                public void onSuccess() {
				                    //doSomething
				                    Log.d(TAG, "get video success.");   
		                        	Intent intent = new Intent(activity,DisplayVideoActivity.class);		                        	
		                        	intent.putExtra("filePath", filePath);
		                        	activity.startActivity(intent);
		                        	progressDialog.dismiss();		       	                    
				                }

				            });
			    	    }
			           	  
			}
    		 
    	});   
    
	}
}
