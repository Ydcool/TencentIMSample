package com.example.mydemo.utils;

import com.example.mydemo.R;
import com.example.mydemo.activity.ChatNewActivity;
import com.example.mydemo.activity.MainActivity;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.tencent.TIMConversationType;
import com.tencent.TIMGroupReceiveMessageOpt;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class PushUtil {
	
	private static int pushNum=0;
	
	private final static int pushId=1;
	
	
	public static void PushNotify(TIMMessage msg,Context context){
		if (msg==null||Foreground.get().isForeground()) return;
		TIMConversationType type=msg.getConversation().getType();
		if (type!=TIMConversationType.Group&&type!=TIMConversationType.C2C) return;
		String senderStr="",contentStr="";
		switch(type){
			case Group:
				GroupInfo info = UserInfoManagerNew.getInstance().getGroupID2Info().get(msg.getConversation().getPeer());
				if(info.getMessageOpt() == TIMGroupReceiveMessageOpt.ReceiveNotNotify){
					return;
				}
				senderStr=info.getName();
				break;
			case C2C:
				senderStr=UserInfoManagerNew.getInstance().getDisplayName(msg.getConversation().getPeer());
				break;
		}		
		switch(msg.getElement(0).getType()) {
	        case Text:
	        	TIMTextElem e = (TIMTextElem) msg.getElement(0);
	        	contentStr=e.getText();
	        	break;
	        case Image:
	        	contentStr="[图片]";	        	
	        	break;	        	
	        case Sound:
	        	contentStr="[语音]";	        	
	        	break;	
	        default:
	        	contentStr="一条新消息"; 	        	
	        	break;
		}
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        mBuilder.setContentTitle(senderStr)//设置通知栏标题
                .setContentText(contentStr)
                .setContentIntent(intent) //设置通知栏点击意图
                .setNumber(++pushNum) //设置通知集合的数量
                .setTicker(senderStr+":"+contentStr) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间                   
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合                        
                .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
        Notification notify = mBuilder.build();
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(pushId, notify);
	}
	
	public static void resetPushNum(){
		pushNum=0;
	}

}
