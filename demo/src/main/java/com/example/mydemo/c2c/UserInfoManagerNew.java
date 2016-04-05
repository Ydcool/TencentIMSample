package com.example.mydemo.c2c;

import android.util.Log;

import com.example.mydemo.TLSHelper;
import com.example.mydemo.utils.Constant;
import com.example.mydemo.utils.GroupInfo;
import com.tencent.TIMFriendAllowType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserInfoManagerNew {
    private final static String TAG = UserInfoManagerNew.class.getSimpleName();

    private Map<String, UserInfo> mContactsList;
    //	private Map<String,String> mGroupID2Name;
    private Map<String, GroupInfo> mGroupID2Info;
    private Map<String, String> mPublicGroupID2Name;
    private Map<String, String> mPrivateGroupID2Name;
    private Map<String, String> mChatRoomID2Name;
    private Map<String, TIMGroupDetailInfo> mGroupsInfo;
    private Map<String, List<TIMGroupMemberInfo>> mGroupMembers;
    private List<String> mBlackList;
    private static UserInfoManagerNew instance;
    //	private String mMyID = null;
    private String mMyNickName = null;
    private TIMFriendAllowType mAllowType = null;
    private long lUnReadSystem = 0;

    UserInfoManagerNew() {
        mContactsList = new HashMap<String, UserInfo>();
        mBlackList = new ArrayList<String>();
        //mGroupID2Name = new HashMap<String,String>();
        mGroupID2Info = new HashMap<String, GroupInfo>();
        mPublicGroupID2Name = new HashMap<String, String>();
        mPrivateGroupID2Name = new HashMap<String, String>();
        mChatRoomID2Name = new HashMap<String, String>();
        mGroupsInfo = new HashMap<String, TIMGroupDetailInfo>();
        mGroupMembers = new HashMap<String, List<TIMGroupMemberInfo>>();
    }

    public void ClearData() {
        mContactsList.clear();
        mBlackList.clear();
        mGroupID2Info.clear();
        mPublicGroupID2Name.clear();
        mPrivateGroupID2Name.clear();
        mChatRoomID2Name.clear();
        mMyNickName = null;
        mAllowType = null;
        lUnReadSystem = 0;
        Log.d(TAG, "clear data");
    }

    public static UserInfoManagerNew getInstance() {
        if (instance == null) {
            instance = new UserInfoManagerNew();
        }
        return instance;
    }


    public Map<String, UserInfo> getContactsList() {
        return mContactsList;
    }

    public String getNickName() {
        return mMyNickName;
    }

    public TIMFriendAllowType getAllowType() {
        return mAllowType;
    }

    public void addGroupInfo(String groupID, TIMGroupDetailInfo info) {
        mGroupsInfo.put(groupID, info);
    }

    public void deleteGroupInfo(String groupID) {
        mGroupsInfo.remove(groupID);
    }

    public TIMGroupDetailInfo getGroupInfo(String groupID) {
        return mGroupsInfo.get(groupID);
    }

    public void addGroupMembers(String groupID, List<TIMGroupMemberInfo> members) {
        mGroupMembers.put(groupID, members);
    }

    public void deleteGroupMembers(String groupID) {
        mGroupMembers.remove(groupID);
    }

    public List<TIMGroupMemberInfo> getGroupMembers(String groupID) {
        return mGroupMembers.get(groupID);
    }

    public void SetAllowType(TIMFriendAllowType allowType) {
        this.mAllowType = allowType;
    }

    public String getDisplayName(String userID) {
        String strName;
        if (userID.equals(TLSHelper.userID)) {
            strName = UserInfoManagerNew.getInstance().getNickName();
        } else {
            com.example.mydemo.c2c.UserInfo user = mContactsList.get(userID);
            strName = (user != null ? user.getDisplayUserName() : userID);
        }
        return strName;
    }

    public void getSelfProfile() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                Log.e(TAG, "getSelfProfile error:" + arg0 + ":" + arg1);
            }

            @Override
            public void onSuccess(TIMUserProfile arg0) {
                // TODO Auto-generated method stub

                String mMyID = arg0.getIdentifier();
                mMyNickName = (arg0.getNickName() != null && arg0.getNickName().length() != 0) ? arg0.getNickName() : mMyID;
                mAllowType = arg0.getAllowType();
                Log.d(TAG, "getSelfProfile succ:" + mMyID + ":" + mMyNickName + ":" + mAllowType);

            }
        });

    }

    public void UpdateContactList(String userID) {
        //本地列表先生成，可以立即展现。然后从服务器拉取更多的资料
        UserInfo user = new UserInfo();
        user.setName(userID);
        user.ProcessHeader();
        mContactsList.put(userID, user);

        getContactsListFromServer();

    }

    public void getContactsListFromServer() {
        TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMUserProfile>>() {

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                Log.e(TAG, "getFriendList error:" + arg0 + ":" + arg1);
            }

            @Override
            public void onSuccess(List<TIMUserProfile> arg0) {
                // TODO Auto-generated method stub
                Log.d(TAG, "getFriendList succ:" + arg0.size());
                //资料没有变更就不会返回资料,增量更新.不直接清空
                mContactsList.clear();
                for (TIMUserProfile tmp : arg0) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setName(tmp.getIdentifier());
                    if (tmp.getNickName().length() != 0) {
                        userInfo.setNick(tmp.getNickName());
                    }
                    userInfo.ProcessHeader();
                    mContactsList.put(tmp.getIdentifier(), userInfo);
                    Log.d(TAG, "friend:" + tmp.getIdentifier() + ":" + tmp.getNickName());
                }

//				UserInfo userInfo = new UserInfo();
//				userInfo.setName(mMyID);
//				userInfo.setNick(mMyNickName);
//				userInfo.ProcessHeader();
//				mContactsList.put(TLSHelper.userID,userInfo);	

            }

        });
    }

    public void getBlackListFromServer() {
        TIMFriendshipManager.getInstance().getBlackList(new TIMValueCallBack<List<String>>() {
            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                Log.e(TAG, "getBlackList error:" + arg0 + ":" + arg1);
            }

            @Override
            public void onSuccess(List<String> arg0) {
                // TODO Auto-generated method stub
                mBlackList.clear();
                Log.d(TAG, "getBlackList succ");
                for (String str : arg0) {
                    mBlackList.add(str);
                }
            }

        });
    }

//	public Map<String,String> getGroupID2Name() {
//		return mGroupID2Name;
//	}	

    public Map<String, GroupInfo> getGroupID2Info() {
        return mGroupID2Info;
    }

    public void UpdateGroupID2Name(String groupID, String groupName, String type, boolean bAdd) {
        Map<String, String> groupID2Name = null;
        if (type.equals(Constant.TYPE_PUBLIC_GROUP)) {
            groupID2Name = mPublicGroupID2Name;
        } else if (type.equals(Constant.TYPE_PRIVATE_GROUP)) {
            groupID2Name = mPrivateGroupID2Name;
        } else if (type.equals(Constant.TYPE_CHAT_ROOM)) {
            groupID2Name = mChatRoomID2Name;
        }
        if (groupID2Name == null) {
            Log.e(TAG, "groupID2Name null");
            return;
        }
        if (bAdd) {
            groupID2Name.put(groupID, groupName);
            GroupInfo info = new GroupInfo();
            info.setName(groupName);
            info.setType(type);
            mGroupID2Info.put(groupID, info);
            //	mGroupID2Name.put(groupID, groupName);
        } else {
            groupID2Name.remove(groupID);
            //mGroupID2Name.remove(groupID);
            mGroupID2Info.remove(groupID);
        }
    }

    public void getGroupListFromServer() {

        TIMGroupManager.getInstance().getGroupList(new TIMValueCallBack<List<TIMGroupBaseInfo>>() {
            @Override
            public void onError(int code, String desc) {
                Log.e(TAG, "get gruop list failed: " + code + " desc");
                //  Toast.makeText(getBaseContext(),"获取群列表失败!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<TIMGroupBaseInfo> arg0) {
                //	mGroupID2Name.clear();
                mGroupID2Info.clear();
                mPublicGroupID2Name.clear();
                mPrivateGroupID2Name.clear();
                mChatRoomID2Name.clear();
                for (TIMGroupBaseInfo baseInfo : arg0) {
                    Log.d(TAG, "get group:" + baseInfo.getGroupId() + ":" + baseInfo.getGroupType() + ":" + baseInfo.getGroupName());
                    UpdateGroupID2Name(baseInfo.getGroupId(), baseInfo.getGroupName(), baseInfo.getGroupType(), true);
//					mGroupID2Name.put(baseInfo.getGroupId(),baseInfo.getGroupName());
//					if(baseInfo.getGroupType().equals(Constant.TYPE_PUBLIC_GROUP)){
//						mPublicGroupID2Name.put(baseInfo.getGroupId(),baseInfo.getGroupName());
//					}else if(baseInfo.getGroupType().equals(Constant.TYPE_PRIVATE_GROUP)){
//						mPrivateGroupID2Name.put(baseInfo.getGroupId(),baseInfo.getGroupName());
//					}else if(baseInfo.getGroupType().equals(Constant.TYPE_CHAT_ROOM)){
//						mChatRoomID2Name.put(baseInfo.getGroupId(),baseInfo.getGroupName());
//					}
                }
            }
        });
    }

    //	public void setPublicGroupID2Name(Map<String,String> mPublicGroupID2Name) {
//		 this.mPublicGroupID2Name = mPublicGroupID2Name;
//	}		
    public Map<String, String> getPublicGroupID2Name() {
        return mPublicGroupID2Name;
    }

    //	public void setPrivateGroupID2Name(Map<String,String> mPrivateGroupID2Name) {
//		 this.mPrivateGroupID2Name = mPrivateGroupID2Name;
//	}		
    public Map<String, String> getPrivateGroupID2Name() {
        return mPrivateGroupID2Name;
    }

    //	public void setChatRoomID2Name(Map<String,String> mChatRoomID2Name) {
//		 this.mChatRoomID2Name = mChatRoomID2Name;
//	}		
    public Map<String, String> getChatRoomID2Name() {
        return mChatRoomID2Name;
    }

    public List<String> getBlackList() {
        return mBlackList;
    }

    public void setUnReadSystem(long lUnReadSystem) {
        this.lUnReadSystem = lUnReadSystem;
    }

    public void setNickName(String mMyNickName) {
        this.mMyNickName = mMyNickName;
    }

    public long getUnReadSystem() {
        return lUnReadSystem;
    }
}
