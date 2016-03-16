package com.example.mydemo.activity;

import com.example.mydemo.MyApplication;
import com.tencent.TIMManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MyBaseActivity extends Activity  implements  Thread.UncaughtExceptionHandler{
	protected static final String TAG = MyBaseActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.e(TAG, "onCreate updating currentActivity");
		MyApplication.getInstance().currentActivity = this;
	}
	
	@Override
	public void onStart() {	
		super.onStart();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyApplication.getInstance().currentActivity = this;
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		//  Toast.makeText(this, "很抱歉，程序异常，即将退出！",Toast.LENGTH_LONG).show();	
		  
	       Dialog alertDialog = new AlertDialog.Builder(this). 
	                setTitle("异常"). 
	                setMessage("很抱歉，程序异常，即将退出"). 
	                setNegativeButton("确定", new DialogInterface.OnClickListener() { 
	                     
	                    @Override 
	                    public void onClick(DialogInterface dialog, int which) { 
	                        // TODO Auto-generated method stub  	                    	
	                    } 
	                }). 
	                create(); 
	        alertDialog.show(); 		  

		 	Log.e(TAG,"exit:" + ex.toString());
		 	ex.printStackTrace();
			TIMManager.getInstance().logout();	
			
			System.exit(0);
	}	
}