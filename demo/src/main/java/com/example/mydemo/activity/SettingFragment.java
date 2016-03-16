package com.example.mydemo.activity;

import com.example.mydemo.MyApplication;
import  com.example.mydemo.R;
import com.example.mydemo.TLSHelper;
//import com.example.mydemo.TLSHelper;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.tencent.TIMManager;
import com.tencent.qalsdk.QALSDKManager;
import com.tencent.tls.callback.LoginCallBack;
import com.tencent.tls.model.ErrorCode;
import com.tencent.tls.model.LoginSession;
//import com.tencent.tls.activity.HostLoginActivity;
import com.tencent.tls.service.Constants;
import com.tencent.tls.service.TLSService;

import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

 public class SettingFragment extends Fragment {
	 private final static String TAG = SettingFragment.class.getSimpleName();

	private Button mBtnQuit ;
	private ToggleButton tbNotify;
	private ToggleButton tbVibrate;
	private ToggleButton tbSound;
	private ToggleButton tbSetEnv;
	private ToggleButton tb_set_log_console;
	private TextView tvNickName;
	private String strNickName;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fg_setting, container, false);
		onInit(view);
		return view;
	}

	private void onInit(View v){
		onInitFace(v);
		onInitNick(v);
		onInitPassFriendStyle(v);
		onInitBlackList(v);
		onInitSetLogLevel(v);
		onVersion(v);
		
		

			mBtnQuit = (Button) v.findViewById(R.id.btn_logout);
			mBtnQuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			       Dialog alertDialog = new AlertDialog.Builder(getActivity()). 
			                setTitle("确定退出？"). 
			                setMessage("确定退出吗？"). 			       
			                setPositiveButton("取消", new DialogInterface.OnClickListener() { 
			                     
			                    @Override 
			                    public void onClick(DialogInterface dialog, int which) { 
			                        // TODO Auto-generated method stub  
			                    } 
			                }). 
			                setNegativeButton("确定", new DialogInterface.OnClickListener() { 
			                     
			                    @Override 
			                    public void onClick(DialogInterface dialog, int which) { 
			                        // TODO Auto-generated method stub  
			            			TIMManager.getInstance().logout();
			            			MainActivity.bLogin=false;
			            	//		Intent intent;
			            			if(MyApplication.getInstance().bHostRelaytionShip){		
			            				
			            				TLSService.getInstance().clearUserInfo(TLSHelper.userID);
			            				Log.d(TAG,"log out:" + TLSHelper.userID);
			            				UserInfoManagerNew.getInstance().ClearData();
//			            			    intent = new Intent((MainActivity) getActivity(), HostLoginActivity.class);
//			            			    intent.putExtra(Constants.EXTRA_THIRDAPP_PACKAGE_NAME_SUCC, "com.example.mydemo");
//			            			    intent.putExtra(Constants.EXTRA_THIRDAPP_CLASS_NAME_SUCC, "com.example.mydemo.activity.MainActivity");
			            		
										TLSService.getInstance().showPhoneSmsLogin((MainActivity) getActivity(), new LoginCallBack(){

											@Override
											public void onSuccess(LoginSession session) {
												// TODO Auto-generated method stub
												Log.d(TAG,"tls login succ");
												Intent intent;
				
												intent = new Intent((MainActivity) getActivity(), MainActivity.class);	
												intent.putExtra("session",session);
												startActivity(intent);
												((MainActivity) getActivity()).finish();
											}

											@Override
											public void onFailure(ErrorCode errCode,
													String msg) {
												Log.e(TAG,"tls login Error:" + errCode + ":" + msg);
												Toast.makeText(getActivity().getBaseContext(), errCode +":" + msg, Toast.LENGTH_SHORT).show();
												// TODO Auto-generated method stub
												
											}
											
										});
//			            			    
			            			}
//			            			else{
//			            				intent = new Intent((MainActivity) getActivity(), LoginActivity.class);
//			            			}			    					
//			    			    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			    				    startActivity(intent);	
//			            			
			        			//	((MainActivity) getActivity()).finish();			                    	
			                    } 
			                }). 			               
			                create(); 
			        alertDialog.show();
			}
		});
		
		
		
		
		tbNotify=(ToggleButton) v.findViewById(R.id.tb_set_notify);
		tbNotify.setChecked(MyApplication.getInstance().getSettingNotification());
		tbNotify.setOnCheckedChangeListener(new OnCheckedChangeListener(){         
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
					tbNotify.setChecked(isChecked);
					MyApplication.getInstance().setSettingNotification(isChecked);		
			}
        });			
		
		tbVibrate=(ToggleButton) v.findViewById(R.id.tb_set_vibrate);
		tbVibrate.setChecked(MyApplication.getInstance().getSettingVibrate());
		tbVibrate.setOnCheckedChangeListener(new OnCheckedChangeListener(){         
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
					tbVibrate.setChecked(isChecked);
					MyApplication.getInstance().setSettingVibrate(isChecked);		
			}
        });			
		
		tbSound=(ToggleButton) v.findViewById(R.id.tb_set_voice);
		tbSound.setChecked(MyApplication.getInstance().getSettingSound());
		tbSound.setOnCheckedChangeListener(new OnCheckedChangeListener(){         
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				tbSound.setChecked(isChecked);
				MyApplication.getInstance().setSettingSound(isChecked);		
			}
        });		
		
		tbSetEnv = (ToggleButton) v.findViewById(R.id.tb_set_env);
		tbSetEnv.setChecked(MyApplication.getInstance().getTestEnvSetting());
		tbSetEnv.setOnCheckedChangeListener(new OnCheckedChangeListener(){         
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				tbSetEnv.setChecked(isChecked);
				MyApplication.getInstance().setTestEnv(isChecked);		
			}
        });		
		
		tb_set_log_console = (ToggleButton) v.findViewById(R.id.tb_set_log_console);
		tb_set_log_console.setChecked(MyApplication.getInstance().getLogConsole());
		tb_set_log_console.setOnCheckedChangeListener(new OnCheckedChangeListener(){         
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				tb_set_log_console.setChecked(isChecked);
				MyApplication.getInstance().setLogConsole(isChecked);		
				TIMManager.getInstance().setLogPrintEanble(isChecked);
				Log.d(TAG,"set log console:" + isChecked);
			}
        });			
		
	}	
	
	private void onInitFace(View view){
		RelativeLayout rlSetFace = (RelativeLayout) view.findViewById(R.id.rl_setting_face);
		rlSetFace.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub		    
			    startActivity(new Intent((MainActivity) getActivity(), SetFaceActivity.class));	
			}
			
		});
	}
	
	private void onVersion(View view){
		TextView tvVersion = (TextView) view.findViewById(R.id.tv_version);
		String tmp = TIMManager.getInstance().getVersion() ;
		tmp += "|";
		tmp += tencent.tls.platform.TLSHelper.getInstance().getSDKVersion();
		tmp += "|";
		tmp += QALSDKManager.getInstance().getSdkVersion();
		tvVersion.setText(tmp);
	}	

	@Override
	public void onResume() {		
		super.onResume();		
		strNickName = UserInfoManagerNew.getInstance().getNickName();
		if(strNickName != null){
			tvNickName.setText(strNickName);
		}	
	}	
	
	private void onInitNick(View view){
		TextView userID = (TextView) view.findViewById(R.id.tv_user_ID);		
		userID.setText(TIMManager.getInstance().getLoginUser());
		RelativeLayout rlSetNick = (RelativeLayout) view.findViewById(R.id.rl_setting_nick);
		tvNickName = (TextView) view.findViewById(R.id.tv_nick_name);
		//TO DO LIST 
		//final String name = "Just Test";
		strNickName = UserInfoManagerNew.getInstance().getNickName();
		if(strNickName != null){
			tvNickName.setText(strNickName);
		}
		
		rlSetNick.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub		
				Intent intent = new Intent((MainActivity) getActivity(), EditNickNameActivity.class);
				intent.putExtra("nickName", strNickName);
			    startActivity(intent);	
			}
			
		});
		
	}
		
	private void onInitPassFriendStyle(View view){
		RelativeLayout rlPassStyle = (RelativeLayout) view.findViewById(R.id.rl_pass_style);
		//TO DO LIST 
	
		rlPassStyle.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub		
				Intent intent = new Intent((MainActivity) getActivity(), PassStyleActivity.class);
				
			    startActivity(intent);	
			}
			
		});		
	}

	private void onInitSetLogLevel(View view){
		RelativeLayout rlLogLevel = (RelativeLayout) view.findViewById(R.id.rl_log_level);
		//TO DO LIST 
	
		rlLogLevel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub		
				Intent intent = new Intent((MainActivity) getActivity(), SetLogLevelActivity.class);
				
			    startActivity(intent);	
			}
			
		});		
	}
	
	private void onInitBlackList(View view){
		RelativeLayout rlPassStyle = (RelativeLayout) view.findViewById(R.id.rl_black_list);
		//TO DO LIST 
	
		rlPassStyle.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub		
				Intent intent = new Intent((MainActivity) getActivity(), BlackListActivity.class);
				
			    startActivity(intent);	
			}
			
		});		
	}	
	
	
}
