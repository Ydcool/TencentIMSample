package com.example.mydemo.activity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import  com.example.mydemo.R;
import com.example.mydemo.adapter.GroupListAdapter;
import com.example.mydemo.c2c.UserInfoManager;
import com.example.mydemo.utils.Constant;
import com.example.mydemo.utils.GroupInfo;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupSearchSucc;
import com.tencent.TIMValueCallBack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


 public class SearchGroupActivity  extends MyBaseActivity {
	private final static String TAG = SearchGroupActivity.class.getSimpleName();
	
	private  List<GroupInfo> mListGroup;
	private ListView mLVGroup;
	private Button mBtnSearchGroup;
	public GroupListAdapter mAdapter;
	private EditText mEtGroupName;
	private String strSearchName;

	private boolean mIsLoading = false;
	private boolean mBMore = true;
	private final int  MAX_PAGE_NUM = 20;
	private int mLoadMsgNum =0;
	private ProgressBar mPBLoadData;
	//private boolean mBNerverLoadMore = true;
	private InputMethodManager inputKeyBoard;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_group);	
		initView();	
	}
	
	public void initView() {
		mEtGroupName = (EditText) findViewById(R.id.et_search_group);		
		mBtnSearchGroup = (Button) findViewById(R.id.btn_search_group);
		mLVGroup= (ListView)findViewById(R.id.lv_groups);
		mPBLoadData = (ProgressBar) findViewById(R.id.pb_load_more);
		
		inputKeyBoard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		//mListGroup = new ArrayList<TIMGroupBaseInfo>();
		mListGroup = new ArrayList<GroupInfo>();
		mAdapter = new GroupListAdapter(getBaseContext(),mListGroup);
		mLVGroup.setAdapter(mAdapter);	
		TextView tvName = (TextView) findViewById(R.id.chat_name);
		String mStrGroupType = getIntent().getStringExtra("groupType");
		if(mStrGroupType.equals(Constant.TYPE_PRIVATE_GROUP)){
			tvName.setText("查找讨论组");
		}else if(mStrGroupType.equals(Constant.TYPE_CHAT_ROOM)){
			tvName.setText("查找聊天室");
			mEtGroupName.setHint("请输入聊天室id/聊天室名称");
		}else if(mStrGroupType.equals(Constant.TYPE_PUBLIC_GROUP)){
			tvName.setText("查找群");
		}		

		mBtnSearchGroup.setOnClickListener( new OnClickListener(){
			@Override
			public void onClick(View v) { 	
				hideMsgIputKeyboard();
				strSearchName = mEtGroupName.getText().toString().trim();
				if(strSearchName.length() != 0){
					//TO DO LIST
					List<String> ids = new ArrayList<String>();
					ids.add(strSearchName);
					TIMGroupManager.getInstance().getGroupPublicInfo(ids,new TIMValueCallBack<List<TIMGroupDetailInfo>>(){

						@Override
						public void onError(int arg0, String arg1) {
							// TODO Auto-generated method stub
							Log.d(TAG,"search getGroupPublicInfo:" + strSearchName +":"+ arg0 +":" + arg1);
							if(arg0 == 10015){
								mListGroup.clear();
								mLoadMsgNum=0;
								searchByName();
							}
						}

						@Override
						public void onSuccess(List<TIMGroupDetailInfo> arg0) {
							
							if(arg0.size()!=1){
								Log.e(TAG,"search getGroupPublicInfo error:" + arg0.size());
								return;
							}
							TIMGroupDetailInfo detailInfo = arg0.get(0);
							Log.d(TAG,"search getGroupPublicInfo scucc:" +   strSearchName + ":"+ detailInfo.getGroupOwner() + ":"+detailInfo.getGroupIntroduction());
							mListGroup.clear();
						 	GroupInfo entity = new GroupInfo();
						 	entity.setID(detailInfo.getGroupId());
						 	entity.setName(detailInfo.getGroupName());		
						 	entity.setOwner(detailInfo.getGroupOwner());
						 	entity.setIntroduction(detailInfo.getGroupIntroduction());
						 	entity.setType(detailInfo.getGroupType());
						 	mListGroup.add(entity);
					    	mLVGroup.setVisibility(View.VISIBLE);
					    	mAdapter.notifyDataSetChanged();	
							
						}
					});
					
				}
			} 			
		});
		
		mLVGroup.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {			
				//TIMGroupBaseInfo entity = (TIMGroupBaseInfo) mAdapter.getItem(position);
				GroupInfo entity = (GroupInfo) mAdapter.getItem(position);
				
			    Intent intent = new Intent(SearchGroupActivity.this, GroupSimpleInfoActivity.class);
			    intent.putExtra("groupID",entity.getID());
			    intent.putExtra("groupName", entity.getName());
			    intent.putExtra("owner", entity.getOwner());
			    intent.putExtra("describe", entity.getIntroduction());
			    intent.putExtra("groupType", entity.getType());
			    startActivity(intent);				
			}
		});
		
		
		mLVGroup.setOnScrollListener(new OnScrollListener(){

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
						searchByName();
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

	private void hideMsgIputKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				inputKeyBoard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}	

	@Override
	public void onResume() {		
		super.onResume();			
		Log.d(TAG,"activity resume ,refresh list");
		
	}	
	
	private void searchByName(){
		long flag =0;
		flag |= TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_GROUP_TYPE;
		flag |= TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_OWNER_UIN;
		flag |= TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_INTRODUCTION;
		flag |= TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_APP_ID;
		flag |= TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_NAME;
		TIMGroupManager.getInstance().searchGroup(strSearchName, flag, null, mLoadMsgNum, MAX_PAGE_NUM, new TIMValueCallBack<TIMGroupSearchSucc>(){

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.d(TAG,"searchByName error:"+ arg0 +":" + arg1);
	              mPBLoadData.setVisibility(View.GONE);
					mIsLoading = false;
			}

			@Override
			public void onSuccess(TIMGroupSearchSucc arg0) {
				// TODO Auto-generated method stub
				Log.d(TAG,"searchByName ok:"+ arg0.getInfoList().size());
				 List<TIMGroupDetailInfo>  infos = arg0.getInfoList();
				for(int i=0;i<infos.size();i++){
				 	GroupInfo entity = new GroupInfo();
				 	TIMGroupDetailInfo info = infos.get(i);
				 	entity.setID(info.getGroupId());
				 	entity.setName(info.getGroupName());		
				 	entity.setOwner(info.getGroupOwner());
				 	entity.setIntroduction(info.getGroupIntroduction());
				 	entity.setType(info.getGroupType());
				 	mListGroup.add(entity);
				}

				if( arg0.getTotalNum() == mListGroup.size()) {
            		mBMore = false;
            	}
		    	mLVGroup.setVisibility(View.VISIBLE);
		    	mAdapter.notifyDataSetChanged();
             	mIsLoading = false;	
             	mPBLoadData.setVisibility(View.GONE);             	
			}
			
		});
	}
	public void onBack(View view){	
		finish();
	}
	

}
