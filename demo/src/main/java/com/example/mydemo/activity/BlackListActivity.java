package com.example.mydemo.activity;
import java.util.ArrayList;
import java.util.List;

import  com.example.mydemo.R;
import com.example.mydemo.adapter.BlackListAdapter;
import com.example.mydemo.c2c.UserInfoManagerNew;
import android.app.AlertDialog; 
import android.app.Dialog; 
import android.content.DialogInterface; 
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMValueCallBack;


 public class BlackListActivity  extends MyBaseActivity {
	private final static String TAG = BlackListActivity.class.getSimpleName();
	private  List<String> mBlackList;
	private ListView mLVBlack;
	public BlackListAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_list);	
		initView();	
	}
	
	
	public void initView() {	
		mLVBlack= (ListView)findViewById(R.id.lv_new_friends);	
	//	mBlackList = new ArrayList<String>();
		loadContent();		
		mBlackList = UserInfoManagerNew.getInstance().getBlackList();
		mAdapter = new BlackListAdapter(getBaseContext(),mBlackList);
		mLVBlack.setAdapter(mAdapter);
		
		
		mLVBlack.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
					final int pos = position;
				
			       Dialog alertDialog = new AlertDialog.Builder(BlackListActivity.this). 
			                setTitle("确定删除？"). 
			                setMessage("确定从黑名单中删除？"). 			       
			                setNegativeButton("确定", new DialogInterface.OnClickListener() { 
			                     
			                    @Override 
			                    public void onClick(DialogInterface dialog, int which) { 
			                        // TODO Auto-generated method stub  
			                    	List<String> bList = new ArrayList<String>();
			                    	bList.add(mBlackList.get(pos));
			                    	TIMFriendshipManager.getInstance().delBlackList(bList, new TIMValueCallBack<List<TIMFriendResult>> (){
										
										@Override
										public void onError(int arg0, String arg1) {
											// TODO Auto-generated method stub
											Log.e(TAG,"delBlackList error:" + arg0 + ":" + arg1);
										}
			
										@Override
										public void onSuccess(List<TIMFriendResult> arg0) {
											// TODO Auto-generated method stub
											Log.d(TAG,"delBlackList succ:" + arg0.size());
											mBlackList.remove(pos);
											mAdapter.notifyDataSetChanged();
											
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
					 

				return true;
			}
		});					
	}	
	
	private void loadContent(){
		UserInfoManagerNew.getInstance().getBlackListFromServer();
	}

	
	
	public void onBack(View view){	
		finish();
	}
	
	
}
