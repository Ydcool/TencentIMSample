package com.example.mydemo.activity;

import java.io.File;

import  com.example.mydemo.R;
import com.example.mydemo.adapter.ChatMsgListAdapter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;


 public class DisplayOrgPicActivity  extends MyBaseActivity {
	private final static String TAG = DisplayOrgPicActivity.class.getSimpleName();
	private ImageView mIVOrgPic;
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_org_pic);		
		initView();	
	}
	
	public void initView() {	
		
		String filePath = getIntent().getStringExtra("filePath");
		Log.d(TAG,"init ivew:" + filePath);
		Bitmap bitmap = ChatMsgListAdapter.GetRightOritationNew(filePath);
		Log.d(TAG,"bitmap");
		mIVOrgPic = (ImageView) findViewById(R.id.iv_org_pic);
		mIVOrgPic.setImageBitmap(bitmap);
	
		mIVOrgPic.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View v) {			
				finish();
			} 			
		});
		
	}

}
