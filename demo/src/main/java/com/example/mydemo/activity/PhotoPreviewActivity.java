package com.example.mydemo.activity;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

import  com.example.mydemo.R;
import com.example.mydemo.adapter.ChatMsgListAdapter;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.example.mydemo.utils.Constant;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupManager;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



 public class PhotoPreviewActivity  extends MyBaseActivity {
	private final static String TAG = PhotoPreviewActivity.class.getSimpleName();
	CheckBox checkBox ;
	TextView tvSize;
	private boolean bOrg = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_preview);		
		initView();	
	}
	
	public void initView() {	
		final String filePath = getIntent().getStringExtra("photo_url");
		ImageView ivPic;
		if(filePath!= null){
			Bitmap bitmap = ChatMsgListAdapter.GetRightOritationNew(filePath);
			Log.d(TAG,"bitmap");
			ivPic = (ImageView) findViewById(R.id.iv_pic);
			ivPic.setImageBitmap(bitmap);
		}
		checkBox = (CheckBox) findViewById(R.id.cb_pic_org);
		tvSize=(TextView) findViewById(R.id.tv_size);
		tvSize.setVisibility(View.GONE);
		checkBox.setChecked(false);
		Button btnSend = (Button) findViewById(R.id.btn_photo_send);
		
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					tvSize.setVisibility(View.VISIBLE);
					tvSize.setText("(" + getFileSize(filePath) + ")");
					bOrg = true;
				}else{
					bOrg =false;
					tvSize.setVisibility(View.GONE);
				}
			}
			
		});
		btnSend.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {				
				Intent resultIntent = new Intent();  
				resultIntent.putExtra("pic_org", bOrg);  
				resultIntent.putExtra("filePath", filePath);
				Log.d(TAG,"btnSend:" + bOrg +":" + filePath);
				setResult(RESULT_OK, resultIntent);  
				finish();
			}
			
		});
			
	}	
		
	
	private static String getFileSize(String filePath) 
	{
		long size = 0;
		try{			
			File file=new File(filePath);
			 if (file.exists()){
				 FileInputStream fis = null;
				 fis = new FileInputStream(file);
				 size = fis.available();
			 }else{
				 Log.e(TAG,"获取文件大小,文件不存在!");
			 }
			 
		}catch(Exception e){
			Log.e(TAG,"getFileSize" + e.toString());
		}
		return FormetFileSize(size);
	}

	
	private static String FormetFileSize(long size)
	{
	DecimalFormat df = new DecimalFormat("#.00");
	String fileSizeString = "";
	String wrongSize="0B";
	if(size==0){
	return wrongSize;
	}
	if (size < 1024){
	fileSizeString = df.format((double) size) + "B";
	 }
	else if (size < 1048576){
	fileSizeString = df.format((double) size / 1024) + "K";
	}
	else if (size < 1073741824){
	    fileSizeString = df.format((double) size / 1048576) + "M";
	  }
	else{
	    fileSizeString = df.format((double) size / 1073741824) + "G";
	  }
	return fileSizeString;
	}	
	public void onBack(View view){	
		finish();
	}

}
