package com.example.mydemo.activity;

import  com.example.mydemo.R;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


 public class ChatMenuActivity  extends MyBaseActivity {
	private final static String TAG = ChatMenuActivity.class.getSimpleName();
	private int item_pos = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_text_menu);		
		initView();	
	}
	
	public void initView() {	
		item_pos = getIntent().getIntExtra("item", -1);
		int type = getIntent().getIntExtra("type", -1);
		boolean needReSend = getIntent().getBooleanExtra("needReSend", false);
		boolean needSave = getIntent().getBooleanExtra("needSave", false);
		if(type == ChatNewActivity.FOR_CHAT_IMAGE_MENU){
			Button bt = (Button) findViewById(R.id.bt_copy);
			bt.setClickable(false);
			bt.setTextColor(0xffc0c3c4);
		}
		if(!needReSend){
			Button bt = (Button) findViewById(R.id.bt_resend); 
			bt.setVisibility(View.GONE);
		}
		if(!needSave){
			Button bt = (Button) findViewById(R.id.bt_save); 
			bt.setVisibility(View.GONE);			
		}
		Log.d(TAG,"get pos:" + item_pos);
	}	
		
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}
	
	public void onCopy(View view){
		setResult(ChatNewActivity.RESULT_CHAT_MENU_COPY, new Intent().putExtra("item", item_pos));
		finish();
	}
	public void onDelete(View view){
		setResult(ChatNewActivity.RESULT_CHAT_MENU_DELETE, new Intent().putExtra("item", item_pos));
		finish();
	}
	public void onResend(View view){
		setResult(ChatNewActivity.RESULT_CHAT_MENU_RESEND, new Intent().putExtra("item", item_pos));
		finish();
	}
	public void onSave(View view){
		setResult(ChatNewActivity.RESULT_CHAT_MENU_SAVE, new Intent().putExtra("item", item_pos));
		finish();
	}	
}
