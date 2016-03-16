package com.example.mydemo.activity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.mydemo.MyApplication;
import  com.example.mydemo.R;
import com.example.mydemo.adapter.GroupListAdapter;
import com.example.mydemo.adapter.GroupMemberOperaAdapter;
import com.example.mydemo.adapter.NewFriendsAdapter;
import com.example.mydemo.adapter.GroupMemberOperaAdapter.ViewHolder;
import com.example.mydemo.c2c.UserInfo;
import com.example.mydemo.c2c.UserInfoManager;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.example.mydemo.utils.Constant;
import com.example.mydemo.utils.GroupInfo;
import com.example.mydemo.utils.SNSChangeEntity;

import android.app.ActivityManager;
import android.app.AlertDialog; 
import android.app.Dialog; 
import android.content.ComponentName;
import android.content.DialogInterface; 
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMFriendFutureItem;
import com.tencent.TIMFriendFutureMeta;
import com.tencent.TIMFriendPendencyItem;
import com.tencent.TIMFriendPendencyMeta;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMFutureFriendType;
import com.tencent.TIMGetFriendFutureListSucc;
import com.tencent.TIMGetFriendPendencyListSucc;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupTipsElem;
import com.tencent.TIMGroupTipsType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMPageDirectionType;
import com.tencent.TIMPendencyGetType;
import com.tencent.TIMSNSChangeInfo;
import com.tencent.TIMSNSSystemElem;
import com.tencent.TIMSNSSystemType;
import com.tencent.TIMValueCallBack;
import com.tencent.imcore.FriendPendencyItem;


 public class NewFriendsActivity  extends MyBaseActivity {
	private final static String TAG = NewFriendsActivity.class.getSimpleName();
	private  List<TIMFriendFutureItem> mListNewFriend;
//	private  List<TIMFriendPendencyItem> mListNewFriend;
	private ListView mLVNewFriend;
	private Button mBtnCreateGroup;
	private long reqFlag=0;
	private long futureFlags=0;
	public NewFriendsAdapter mAdapter;
	private TIMFriendFutureMeta beginMeta;
	//private TIMFriendPendencyMeta beginMeta;
	private ProgressBar mPBLoadData;
	private boolean mIsLoading = false;
	private boolean mBMore = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_friends);	
		initView();	
		// TIMManager.getInstance().addMessageListener(msgListener);
	}
	
	
	public void initView() {
		TextView tvTopName = (TextView) findViewById(R.id.top_name);
		tvTopName.setText("新的朋友");
		beginMeta = null;
		mLVNewFriend= (ListView)findViewById(R.id.lv_new_friends);
		mPBLoadData = (ProgressBar) findViewById(R.id.pb_load_more);
		mListNewFriend = new ArrayList<TIMFriendFutureItem>();
		mAdapter = new NewFriendsAdapter(getBaseContext(),mListNewFriend);
		mLVNewFriend.setAdapter(mAdapter);		
		getMessage();
		
	
		Button btAddFriend = (Button) findViewById(R.id.btn_goto_add_friend);
		if(MyApplication.getInstance().getThirdIdLogin()){
			btAddFriend.setVisibility(View.GONE);
		}else{
			btAddFriend.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				//	if(MyApplication.getInstance().bHostRelaytionShip){
						startActivity(new Intent(NewFriendsActivity.this, AddFriendNewActivity.class));					
//					}else{
//						startActivity(new Intent(NewFriendsActivity.this, AddFriendActivity.class));
//					}
				}
			});		
		}
		

		

		mLVNewFriend.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					Log.d(TAG,view.getFirstVisiblePosition() + ":" + mIsLoading + ":" +mBMore);
					if (view.getFirstVisiblePosition() == 0 && !mIsLoading && mBMore) {
						mPBLoadData.setVisibility(View.VISIBLE);	
				//		mBNerverLoadMore = false;
						mIsLoading =true;
				
					//	Log.d(TAG,"num:" + mLoadMsgNum);
						getMessage();					
						
//						mIsLoading = false;

					}
					break;
				}			
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
			
		});		
		
		mLVNewFriend.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				final TIMFriendFutureItem entity = (TIMFriendFutureItem) mAdapter.getItem(position);
				Log.d(TAG,"type:"+ entity.getType());
				if(entity.getType() != TIMFutureFriendType.TIM_FUTURE_FRIEND_RECOMMEND_TYPE 
						&& entity.getType() != TIMFutureFriendType.TIM_FUTURE_FRIEND_PENDENCY_IN_TYPE
						&& entity.getType() != TIMFutureFriendType.TIM_FUTURE_FRIEND_PENDENCY_OUT_TYPE
						&& entity.getType() != TIMFutureFriendType.TIM_FUTURE_FRIEND_DECIDE_TYPE){
					return true;
				}
			       Dialog alertDialog = new AlertDialog.Builder(NewFriendsActivity.this). 
			                setTitle("确定删除？"). 
			                setMessage("确定删除该记录吗？"). 			       
			                setNegativeButton("确定", new DialogInterface.OnClickListener() { 
			                     
			                    @Override 
			                    public void onClick(DialogInterface dialog, int which) { 
			                        // TODO Auto-generated method stub
			                    	List<String> ids = new ArrayList<String>();
			                    	ids.add(entity.getIdentifier());
			                    	if(entity.getType() == TIMFutureFriendType.TIM_FUTURE_FRIEND_RECOMMEND_TYPE){
				                    	TIMFriendshipManager.getInstance().deleteRecommend(ids, new TIMValueCallBack<List<TIMFriendResult>>(){
	
											@Override
											public void onError(int arg0,
													String arg1) {
												// TODO Auto-generated method stub
												Log.e(TAG,"deleteRecommend error:" + arg0+":" + arg1);
											}
	
											@Override
											public void onSuccess(
													List<TIMFriendResult> arg0) {
												Log.d(TAG,"deleteRecommend succ:" );
												beginMeta = null;
												getMessage();
												// TODO Auto-generated method stub
												
											}
				                    	});
			                    	}else if(entity.getType() == TIMFutureFriendType.TIM_FUTURE_FRIEND_PENDENCY_IN_TYPE || 
			                    			entity.getType() == TIMFutureFriendType.TIM_FUTURE_FRIEND_PENDENCY_OUT_TYPE){
			                    		TIMPendencyGetType getType;
			                    		if(entity.getType() == TIMFutureFriendType.TIM_FUTURE_FRIEND_PENDENCY_IN_TYPE ){//|| entity.getType() == TIMFutureFriendType.TIM_FUTURE_FRIEND_DECIDE_TYPE){
			                    			getType = TIMPendencyGetType.TIM_PENDENCY_GET_COME_IN;
			                    		}else{
			                    			getType = TIMPendencyGetType.TIM_PENDENCY_GET_SEND_OUT;
			                    		}
			                    		
			                    		TIMFriendshipManager.getInstance().deletePendency(getType,ids, new TIMValueCallBack<List<TIMFriendResult>>(){
			                    			
											@Override
											public void onError(int arg0,
													String arg1) {
												// TODO Auto-generated method stub
												Log.e(TAG,"deletePendency error:" + arg0+":" + arg1);
											}
	
											@Override
											public void onSuccess(
													List<TIMFriendResult> arg0) {
												Log.d(TAG,"deletePendency succ:" );
												beginMeta = null;
												getMessage();
												// TODO Auto-generated method stub
												
											}
				                    	});	
			                    	}else{
			                    		TIMFriendshipManager.getInstance().deleteDecide(ids, new TIMValueCallBack<List<TIMFriendResult>>(){
			                    			
													@Override
													public void onError(int arg0,
															String arg1) {
														// TODO Auto-generated method stub
														Log.e(TAG,"deleteDecide error:" + arg0+":" + arg1);
													}
			
													@Override
													public void onSuccess(
															List<TIMFriendResult> arg0) {
														Log.d(TAG,"deleteDecide succ:" );
														beginMeta = null;
														getMessage();
														// TODO Auto-generated method stub
														
													}
						                    	});				                    		
			                    	}
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
	
	private void getMessage(){
		
		if(beginMeta == null){
			mListNewFriend.clear();
			beginMeta = new TIMFriendFutureMeta();
		//	beginMeta.setTimestamp(0);
		//	beginMeta.setNumPerPage(20);
		//	beginMeta.setSeq(0);
			beginMeta.setReqNum(20);	
			beginMeta.setPendencySeq(0);
			beginMeta.setDirectionType(TIMPageDirectionType.TIM_PAGE_DIRECTION_DOWN_TYPE);
			
			
			reqFlag |= TIMFriendshipManager.TIM_PROFILE_FLAG_NICK;
			reqFlag |= TIMFriendshipManager.TIM_PROFILE_FLAG_FACE_URL;
			reqFlag |= TIMFriendshipManager.TIM_PROFILE_FLAG_REMARK ;
			reqFlag |= TIMFriendshipManager.TIM_PROFILE_FLAG_ALLOW_TYPE ;	
			
			futureFlags |= TIMFriendshipManager.TIM_FUTURE_FRIEND_DECIDE_TYPE;
			futureFlags |= TIMFriendshipManager.TIM_FUTURE_FRIEND_PENDENCY_IN_TYPE;
			futureFlags |= TIMFriendshipManager.TIM_FUTURE_FRIEND_PENDENCY_OUT_TYPE;
			futureFlags |= TIMFriendshipManager.TIM_FUTURE_FRIEND_RECOMMEND_TYPE;
		}
		
//		TIMFriendshipManager.getInstance().getPendencyFromServer(beginMeta,TIMPendencyGetType.TIM_PENDENCY_GET_BOTH ,new TIMValueCallBack<TIMGetFriendPendencyListSucc>(){
//
//			@Override
//			public void onError(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onSuccess(TIMGetFriendPendencyListSucc arg0) {
//				// TODO Auto-generated method stub
//				beginMeta = arg0.getMeta();				
//			//	beginMeta.getPendencyUnReadCnt();
//				
//				if(beginMeta.getTimestamp() == 0){
//					mBMore = false;
//				}
//				 List<TIMFriendPendencyItem> items = arg0.getPendencies();
//				 for(TIMFriendPendencyItem item :items){					 
//					 mListNewFriend.add(item);
//					 Log.d(TAG,item.getAddWording() + ":" + item.getType() + ":" + item.getAddWording());
//				 }
//				 mPBLoadData.setVisibility(View.GONE);
//				 mIsLoading =false;
//				 mAdapter.notifyDataSetChanged();
//				 	
//			}
//			
//		});
	
		TIMFriendshipManager.getInstance().getFutureFriends(reqFlag, futureFlags, null,beginMeta, new TIMValueCallBack<TIMGetFriendFutureListSucc>(){

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(TIMGetFriendFutureListSucc arg0) {
				// TODO Auto-generated method stub
				beginMeta = arg0.getMeta();				
				beginMeta.getPendencyUnReadCnt();
				
				if(beginMeta.getTimestamp() == 0){
					mBMore = false;
				}
				 List<TIMFriendFutureItem> items = arg0.getItems();
				 for(TIMFriendFutureItem item :items){					 
					 mListNewFriend.add(item);
					 Log.d(TAG,item.getIdentifier() + ":" + item.getType() + ":" + item.getAddWording());
				 }
				 mPBLoadData.setVisibility(View.GONE);
				 mIsLoading =false;
				 mAdapter.notifyDataSetChanged();
				 
				 
				 //mLVNewFriend.setSelection(position)
				 
			}
			
		});
	}

	private boolean isTopActivity()  
    {       
		boolean isTop = false;
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);  
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;          
        if (cn.getClassName().contains(TAG)){  
        	isTop = true;  
        }  
        Log.d(TAG,"is Top Activity:" + isTop);
        return isTop;  
    }  
	

	@Override
	public void onResume() {		
		super.onResume();			
		Log.d(TAG,"activity resume ,refresh list");
		loadNewFriends();		
	}	
	
	private void loadNewFriends(){
		
	}
	
	public void onBack(View view){	
		finish();
	}
	
	@Override
	public void onDestroy() {
	//	TIMManager.getInstance().removeMessageListener(msgListener);
		super.onDestroy();
	}	
	
}
