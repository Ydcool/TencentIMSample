package com.example.mydemo.adapter;


import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.mydemo.R;
import com.example.mydemo.utils.DateHelper;
import com.example.mydemo.utils.RecentEntity;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMImageElem;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;

public class RecentListAdapter extends BaseAdapter {


	 private static final String TAG = RecentListAdapter.class.getSimpleName();	 

	private List<RecentEntity> listRecentMsg;
	private LayoutInflater inflater;

	public RecentListAdapter(Context context, List<RecentEntity> list) {
		// TODO Auto-generated constructor stub
		this.listRecentMsg = list;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listRecentMsg.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listRecentMsg.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void refresh(){
		this.notifyDataSetChanged();
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.recent_item, null);
			holder = new ViewHolder();		
			holder.userName = (TextView) convertView.findViewById(R.id.recent_name);
			holder.sendTime = (TextView) convertView.findViewById(R.id.recent_time);
			holder.message = (TextView) convertView.findViewById(R.id.recent_msg);
			holder.unread_num = (TextView) convertView.findViewById(R.id.recent_unread_num);
			holder.avatar = (ImageView)convertView.findViewById(R.id.img_avatar);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final RecentEntity entity = listRecentMsg.get(position);	
		holder.userName.setText( (entity.getNick()!=null && !entity.getNick().trim().equals(""))? entity.getNick():entity.getName());
		holder.sendTime.setText(DateHelper.GetStringFormat(entity.getMessage().timestamp()) );
		holder.message.setText( getMsgContent(entity.getMessage()) );
 		if (entity.getCount() > 0) {
 			holder.unread_num.setVisibility(View.VISIBLE);
 			if(entity.getCount() > 99){
 				holder.unread_num.setText("99+");
 			}else{
 				holder.unread_num.setText(entity.getCount() + "");
 			}
			holder.unread_num.setTextColor(Color.BLACK);
		} else {
			holder.unread_num.setVisibility(View.INVISIBLE);
		}
		if(entity.getIsGroupMsg()){	
			holder.avatar.setImageResource(R.drawable.icon_group);
		}else{
			holder.avatar.setImageResource(R.drawable.chat_default_avatar);
		}
	    return convertView;
	}
	
	
	private static class ViewHolder {
		TextView userName;
		TextView message;
		TextView unread_num;
		TextView sendTime;		
		ImageView avatar;			
}	
	
  private String getMsgContent(TIMMessage msg){
	  String msgContent = new String();
	TIMElem elem = msg.getElement(0);
	if(elem == null){
		return "";
	}
	if(elem.getType()  == TIMElemType.Text) {
		//文本元素, 获取文本内容
		TIMTextElem e = (TIMTextElem)elem;
		Log.d(TAG, "msg: " + e.getText());
		msgContent = e.getText();
	} else if (elem.getType() == TIMElemType.Image){
	    //图片元素, getThumb()获取缩略图byte[]
		msgContent = "[图片]";		  
	}else if (elem.getType() == TIMElemType.File){
		msgContent = "[文件]";
	}else if (elem.getType() == TIMElemType.Sound){
		msgContent = "[语音]";
	}else if (elem.getType() == TIMElemType.GroupTips){
		msgContent = "[群事件通知]";
	}else if (elem.getType() == TIMElemType.Video){
		msgContent = "[视频]";
	}else {
		msgContent = "";
	}
            
      return msgContent; 
    }
	
}


