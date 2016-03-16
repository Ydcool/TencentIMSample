package com.example.mydemo.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.mydemo.MyApplication;
import  com.example.mydemo.R;
import com.example.mydemo.TLSHelper;
import com.example.mydemo.adapter.SearchFriendsAdapter;
import com.example.mydemo.c2c.HttpProcessor;
import com.example.mydemo.utils.FriendInfo;
import com.tencent.TIMAddFriendRequest;
import com.tencent.TIMFriendAllowType;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendStatus;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMUserSearchSucc;
import com.tencent.TIMValueCallBack;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CompoundButton.OnCheckedChangeListener;



 public class AddFriendNewActivity  extends MyBaseActivity {
	private final static String TAG = AddFriendNewActivity.class.getSimpleName();
//	private  RelativeLayout mRLSearchUserResult;
	private EditText mEVSearchUserName;
	private  TextView mTVFriendName;
	private  String mStrUserID;
	private String mStrNickName;
	private boolean mNeedVerify;
//	private boolean bPhone =false;
//	private CheckBox cbPhone;
	private String friendName;
	private ListView mLVSearchFriends;
	private SearchFriendsAdapter adapter;
	private List<FriendInfo> lSearchFriends;
	private boolean bPhone = false;
	
	private boolean mIsLoading = false;
	private boolean mBMore = true;
	private final int  MAX_PAGE_NUM = 20;
	private int mLoadMsgNum =0;
	private ProgressBar mPBLoadData;
	private InputMethodManager inputKeyBoard;
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);		
		initView();	
	}
	
	public void initView() {	
		mEVSearchUserName = (EditText) findViewById(R.id.et_search_user);
	//	mRLSearchUserResult = (RelativeLayout) findViewById(R.id.rl_search_user_result);
		mTVFriendName = (TextView)findViewById(R.id.tv_friend_name);
		mLVSearchFriends = (ListView) findViewById(R.id.lv_friends);
		lSearchFriends = new ArrayList<FriendInfo>();
		adapter = new SearchFriendsAdapter(this,lSearchFriends);
		mLVSearchFriends.setAdapter(adapter);
		mPBLoadData = (ProgressBar) findViewById(R.id.pb_load_more);
		inputKeyBoard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mLVSearchFriends.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					Log.d(TAG,view.getFirstVisiblePosition() + ":" + mIsLoading + ":" +mBMore);
					if (view.getFirstVisiblePosition() == 0 && !mIsLoading && mBMore) {
						mPBLoadData.setVisibility(View.VISIBLE);	
					//	mBNerverLoadMore = false;
						mIsLoading =true;
						mLoadMsgNum += MAX_PAGE_NUM;
						Log.d(TAG,"num:" + mLoadMsgNum);
						searchByNick();
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
	}	
		
	public void onBack(View view){	
		finish();
	}
	public void onSearchFriend(View view){
		hideMsgIputKeyboard();
		String  strTmp = mEVSearchUserName.getText().toString().trim();
		friendName = strTmp;
		if(friendName.isEmpty()){
			Toast.makeText(this, "请输入要查找的用户!", Toast.LENGTH_SHORT).show();
			return ;
		}
	//	if(bPhone){		
	
//		}
	//	Log.d(TAG,"search:" + friendName  + ":" + bPhone);
		bPhone =false;
		searchByID(friendName);
	}

	private void hideMsgIputKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				inputKeyBoard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}	
	
	private void searchByID(final String friendName){
		TIMFriendshipManager.getInstance().searchFriend(friendName, new TIMValueCallBack<TIMUserProfile>(){

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.e(TAG,"searchFriendByID error:"+bPhone +":"+ arg0 + ":" + arg1);
				
				
				//检索不到再查询手机号 
				if(!bPhone && (40003 == arg0 || 6011 ==arg0)){
					String phoneName;
					if(!friendName.startsWith("86-", 0)){
						//friendName = strTmp.substring(3);
						phoneName = TLSHelper.PREFIX + friendName;	
						bPhone=true;
						searchByID(phoneName);						
					}					
				} else if(bPhone&& (40003 == arg0|| 6011 ==arg0)){
					//昵称检测
					lSearchFriends.clear();
					searchByNick();
				}else{
					Toast.makeText(getBaseContext(), "查询异常:" + arg0 + ":" + arg1, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onSuccess(TIMUserProfile arg0) {
				// TODO Auto-generated method stub
				lSearchFriends.clear();
				FriendInfo friendInfo = new FriendInfo();
				friendInfo.setID(arg0.getIdentifier());
				friendInfo.setName(arg0.getNickName());
				friendInfo.setNeedVerify ( arg0.getAllowType()== TIMFriendAllowType.TIM_FRIEND_NEED_CONFIRM) ;  
				
//				if(mStrNickName.length()!=0){
//					mTVFriendName.setText(mStrUserID + "(" + mStrNickName + ")");
//				}else{
//					mTVFriendName.setText(mStrUserID );
//				}
//				mRLSearchUserResult.setVisibility(View.VISIBLE);
//				Button btn = (Button)AddFriendNewActivity.this.findViewById(R.id.btn_add_friend);
//				btn.setClickable(true);
//				btn.setText("添加");
				lSearchFriends.add(friendInfo);
				mLVSearchFriends.setVisibility(View.VISIBLE);
		    	adapter.notifyDataSetChanged();
				Log.d(TAG,"searchFriend ok:" + arg0.getIdentifier() + ":" + arg0.getNickName() + ":" + arg0.getAllowType() + ":" + mNeedVerify);
				
			}
			
		});		
	}
	
	private void searchByNick(){
		Log.d(TAG,"searchFriend by nick:"); 
		TIMFriendshipManager.getInstance().searchUser(friendName,mLoadMsgNum, this.MAX_PAGE_NUM, new TIMValueCallBack<TIMUserSearchSucc>(){

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.e(TAG,"searchByNick error:" + arg0 + ":" + arg1);
				if( 6011 ==arg0)
					arg1="用户不存在";
				Toast.makeText(getBaseContext(), "查询异常:" + arg0 + ":" + arg1, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(TIMUserSearchSucc arg0) {
				// TODO Auto-generated method stub
				Log.d(TAG,"searchByNick ok:" + arg0.getInfoList().size());
				if(arg0.getInfoList().size() == 0){
					Toast.makeText(getBaseContext(), "该用户不存在!", Toast.LENGTH_SHORT).show();
					return;
				}
				for(int i=0;i<arg0.getInfoList().size();i++){
					TIMUserProfile userProfile = arg0.getInfoList().get(i);
					FriendInfo info = new FriendInfo();
					info.setID(userProfile.getIdentifier());
					info.setName(userProfile.getNickName());
					info.setNeedVerify(userProfile.getAllowType()== TIMFriendAllowType.TIM_FRIEND_NEED_CONFIRM);
					lSearchFriends.add(info);
				}
				
				if( arg0.getTotalNum() == lSearchFriends.size()) {
            		mBMore = false;
            	}
				mLVSearchFriends.setVisibility(View.VISIBLE);
		    	adapter.notifyDataSetChanged();
		    	
//		    	if(mLVSearchFriends.getCount() > 1){
//             		if(mIsLoading){
//             			mLVSearchFriends.setSelection(0);
//             		}else{
//             			mLVSearchFriends.setSelection(mLVSearchFriends.getCount() - 1);
//             		}
//             	}
             	mIsLoading = false;	
             	mPBLoadData.setVisibility(View.GONE); 
			}
			
		});
	}
	
	public void onAddFriend(View view){
	//	 String friendName = mTVFriendName.getText().toString().trim();
		if(mStrUserID.equals(TLSHelper.userID)){
			Toast.makeText(getBaseContext(), "不能加自己为好友!", Toast.LENGTH_SHORT).show();
			return;
		}
		if(mNeedVerify){
			Intent intent = new Intent(AddFriendNewActivity.this,AddFriendInfoActivity.class);
			intent.putExtra("userID",mStrUserID);
			//intent.putExtra("needVerify", mNeedVerify);
			startActivity(intent);
		}else{
			List<TIMAddFriendRequest> reqList = new ArrayList<TIMAddFriendRequest>();
			TIMAddFriendRequest friend = new TIMAddFriendRequest();
			friend.setIdentifier(mStrUserID);
			friend.setRemark("qq");
			friend.setAddrSource("qq");
			reqList.add(friend);
			TIMFriendshipManager.getInstance().addFriend(reqList, new TIMValueCallBack<List<TIMFriendResult>>(){

				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub
					Log.e(TAG,"add friend error:" + arg0 + ":" + arg1);
					finish();
				}

				@Override
				public void onSuccess(List<TIMFriendResult> arg0) {
					// TODO Auto-generated method stub
					Log.d(TAG,"add friend response" );
					for( TIMFriendResult arg : arg0){
						Log.d(TAG, "add friend  result:" + arg.getIdentifer() + arg.getStatus());
						if(arg.getStatus() == TIMFriendStatus.TIM_ADD_FRIEND_STATUS_FRIEND_SIDE_FORBID_ADD){
							Toast.makeText(getBaseContext(), "该用户不允许添加好友!", Toast.LENGTH_SHORT).show();
						}
					}
					finish();
				}
				
			});
		}

	}

}
