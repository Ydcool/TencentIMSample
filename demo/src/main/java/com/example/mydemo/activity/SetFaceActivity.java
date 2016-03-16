package com.example.mydemo.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.example.mydemo.R;
public class SetFaceActivity extends MyBaseActivity {	
	
	private final static String TAG = SetFaceActivity.class.getSimpleName();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setface);
		
		onInit();	
	}

	@Override
	protected void onResume() {

		super.onResume();
		
	}

	public void onInit() {
		Button takePhoto = (Button)findViewById(R.id.bt_takephoto);
		takePhoto.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
		Button selectPhoto = (Button)findViewById(R.id.bt_fromcard);
		selectPhoto.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}			
		});		
		
		
		Button cancel = (Button)findViewById(R.id.bt_cancel);
		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}			
		});			
		
	}	


}
