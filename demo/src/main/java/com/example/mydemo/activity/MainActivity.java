package com.example.mydemo.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydemo.MyApplication;
import com.example.mydemo.R;
import com.example.mydemo.SDKHelper;
import com.example.mydemo.TLSHelper;
import com.example.mydemo.c2c.UserInfoManagerNew;
import com.example.mydemo.utils.Constant;
import com.example.mydemo.utils.Foreground;
import com.example.mydemo.utils.PushUtil;
import com.tencent.TIMCallBack;
import com.tencent.TIMElemType;
import com.tencent.TIMGroupTipsElem;
import com.tencent.TIMGroupTipsType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMSNSChangeInfo;
import com.tencent.TIMSNSSystemElem;
import com.tencent.TIMSNSSystemType;
import com.tencent.TIMUser;
import com.tencent.TIMUserStatusListener;
import com.tencent.tls.callback.LoginCallBack;
import com.tencent.tls.model.ErrorCode;
import com.tencent.tls.model.LoginSession;
import com.tencent.tls.service.Constants;
import com.tencent.tls.service.TLSConfiguration;
import com.tencent.tls.service.TLSService;

import java.util.List;

//import com.tencent.tls.activity.HostLoginActivity;

public class MainActivity extends MyBaseActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    private RadioButton rbtn_recent, rbtn_contacts, rbtn_setting;
    private TextView tv_unread_num;
    private Fragment recentFragment, contactsFragment, settingFragment;
    FragmentManager fgManager;
    private long lUnReadNum = 0;
    private int currentTab = 1;
    public static boolean bLogin = false;
    private SDKHelper helper = new SDKHelper();
    private TLSHelper tlsHelper = new TLSHelper();
    private boolean isBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginToIMServer();
        setContentView(R.layout.activity_main);
        fgManager = getFragmentManager();
        Log.d(TAG, "init");
        //	onInitSDK();
        onInit();
    }

    private void onInitSDK() {
        helper.init(this.getApplicationContext());
        tlsHelper.init(this.getApplicationContext());
        Foreground.init(this.getApplication());
    }

    @Override
    public void onResume() {
        super.onResume();
        PushUtil.resetPushNum();
    }

    public void onInit() {
        rbtn_recent = (RadioButton) findViewById(R.id.btn_goto_recent);
        rbtn_contacts = (RadioButton) findViewById(R.id.btn_goto_contacts);
        rbtn_setting = (RadioButton) findViewById(R.id.btn_goto_setting);
        tv_unread_num = (TextView) findViewById(R.id.tv_unread_num);
        recentFragment = new RecentFragment();
        contactsFragment = new ContactsFragment();
        settingFragment = new SettingFragment();

        changeFrament(recentFragment, null, "recentFragment");
        rbtn_recent.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_recent_pressed, 0, 0);
        rbtn_contacts.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_contacts, 0, 0);
        rbtn_setting.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_setting, 0, 0);
        SetMessageListener();
        SetForceLogout();

        rbtn_recent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                recentFragment = new RecentFragment();
                changeFrament(recentFragment, null, "recentFragment");
                changeRadioButtonImage(v.getId());
            }
        });
        rbtn_contacts.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RecentFragment) recentFragment).backPos = ((RecentFragment) recentFragment).recentList.getFirstVisiblePosition();
                contactsFragment = new ContactsFragment();
                changeFrament(contactsFragment, null, "contactsFragment");
                changeRadioButtonImage(v.getId());
            }
        });
        rbtn_setting.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((RecentFragment) recentFragment).backPos = ((RecentFragment) recentFragment).recentList.getFirstVisiblePosition();
                settingFragment = new SettingFragment();
                changeFrament(settingFragment, null, "settingFragment");
                changeRadioButtonImage(v.getId());

            }
        });

    }

    private boolean isTopActivity() {
        boolean isTop = false;
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if (cn.getClassName().contains(TAG)) {
            isTop = true;
        }
        Log.d(TAG, "is Top Activity:" + isTop);
        return isTop;
    }

    private TIMMessageListener msgListener = new TIMMessageListener() {

        @Override
        public boolean onNewMessages(List<TIMMessage> arg0) {

            if (arg0 != null && arg0.size() > 0) {
                PushUtil.PushNotify(arg0.get(0), MainActivity.this);
            }
            // TODO Auto-generated method stub
            // 改群名，加人等操作更新下群名称
            for (TIMMessage msg : arg0) {
                for (int i = 0; i < msg.getElementCount(); i++) {
                    Log.d(TAG, "new msg:" + msg.getElement(i).getType() + ":" + currentTab);
                    if (msg.getElement(i).getType() == TIMElemType.GroupTips) {
                        TIMGroupTipsElem tipsElem = (TIMGroupTipsElem) msg.getElement(i);
                        Log.d(TAG, "new gourp tips:" + tipsElem.getTipsType() + ":" + tipsElem.getGroupName());
                        // Map<String,String> groupID2Name =
                        // UserInfoManager.getInstance().getGroupID2Name();
                        if (tipsElem.getTipsType() == TIMGroupTipsType.Join
                                || tipsElem.getTipsType() == TIMGroupTipsType.ModifyGroupInfo) {
                            UserInfoManagerNew.getInstance().getGroupListFromServer();
                        } else if (tipsElem.getTipsType() == TIMGroupTipsType.Quit) {
                            UserInfoManagerNew.getInstance().getGroupListFromServer();
                        }

                    } else if (msg.getElement(i).getType() == TIMElemType.SNSTips) {
                        // 关系链推送，更新本地缓存
                        TIMSNSSystemElem snsElem = (TIMSNSSystemElem) msg.getElement(i);
                        Log.d(TAG, "snsn tips type:" + snsElem.getSubType());
                        if (snsElem.getSubType() == TIMSNSSystemType.TIM_SNS_SYSTEM_ADD_FRIEND) {
                            for (TIMSNSChangeInfo info : snsElem.getChangeInfoList()) {
                                UserInfoManagerNew.getInstance().UpdateContactList(info.getIdentifier());
                            }
                        } else if (snsElem.getSubType() == TIMSNSSystemType.TIM_SNS_SYSTEM_DEL_FRIEND) {
                            for (TIMSNSChangeInfo info : snsElem.getChangeInfoList()) {
                                UserInfoManagerNew.getInstance().getContactsList().remove(info.getIdentifier());
                            }
                        }
                        if (isTopActivity() && currentTab == 2) {
                            Log.d(TAG, "sns tips : " + msg.getConversation().getUnreadMessageNum());
                            UserInfoManagerNew.getInstance()
                                    .setUnReadSystem(msg.getConversation().getUnreadMessageNum());
                            ((ContactsFragment) contactsFragment).loadContactsContent();
                            return true;
                        }
                    } else if (msg.getElement(i).getType() == TIMElemType.GroupSystem) {
                        UserInfoManagerNew.getInstance().setUnReadSystem(msg.getConversation().getUnreadMessageNum());
                        Log.d(TAG, "group system : " + msg.getConversation().getUnreadMessageNum());
                        if (isTopActivity() && currentTab == 2) {
                            ((ContactsFragment) contactsFragment).loadContactsContent();
                            return true;
                        }
                    }

                    if (isTopActivity() && currentTab == 1) {
                        ((RecentFragment) recentFragment).ProcessNewMsg(msg);
                        //	return true;
                    }
                }

            }
//			Log.d(TAG, "new messge:" + arg0.size() + ":" + currentTab);
//			if (isTopActivity() && currentTab == 1) {
//				((RecentFragment) recentFragment).loadRecentContent();			
//				return true;
//			}
            return false;
        }

    };

    private void SetMessageListener() {
        // 设置消息监听器，收到新消息时，通过此监听器回调
        TIMManager.getInstance().addMessageListener(msgListener);
    }

    private void SetForceLogout() {
        TIMManager.getInstance().setUserStatusListener(new TIMUserStatusListener() {

            @Override
            public void onForceOffline() {
                // TODO Auto-generated method stub
                Log.d(TAG, "onForceOffline");
                Activity activity = MyApplication.getInstance().currentActivity;
                while (activity.getParent() != null) {
                    activity = activity.getParent();
                }
                Dialog alertDialog = new AlertDialog.Builder(activity).setTitle("提醒")
                        .setMessage("您的账号在其他设备登陆，您被迫下线").setNegativeButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                //	TIMManager.getInstance().logout();
                                UserInfoManagerNew.getInstance().ClearData();
                                MainActivity.bLogin = false;
                                //	Intent intent;
                                if (MyApplication.getInstance().bHostRelaytionShip) {

                                    Log.d(TAG, "log out:" + TLSHelper.userID);
//							intent = new Intent(MyApplication.getInstance().currentActivity, HostLoginActivity.class);
//							intent.putExtra(Constants.EXTRA_THIRDAPP_PACKAGE_NAME_SUCC, "com.example.mydemo");
//							intent.putExtra(Constants.EXTRA_THIRDAPP_CLASS_NAME_SUCC,"com.example.mydemo.activity.MainActivity");
                                    TLSService.getInstance().showPhoneSmsLogin(MyApplication.getInstance().currentActivity, new LoginCallBack() {

                                        @Override
                                        public void onSuccess(LoginSession session) {
                                            // TODO Auto-generated method stub
                                            Log.d(TAG, "tls login succ");
                                            Intent intent;
                                            intent = new Intent(MyApplication.getInstance().currentActivity, MainActivity.class);
                                            intent.putExtra("session", session);
                                            startActivity(intent);
                                            MyApplication.getInstance().currentActivity.finish();
                                        }

                                        @Override
                                        public void onFailure(ErrorCode errCode,
                                                              String msg) {
                                            Log.e(TAG, "tls login Error:" + errCode + ":" + msg);
                                            // TODO Auto-generated method stub

                                        }

                                    });
                                }
//						else {
//							intent = new Intent(MyApplication.getInstance().currentActivity, LoginActivity.class);
//						}
//						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//						startActivity(intent);


                            }
                        }).create();
                alertDialog.show();

            }

        });
    }

    public void changeFrament(Fragment fragment, Bundle bundle, String tag) {

        for (int i = 0, count = fgManager.getBackStackEntryCount(); i < count; i++) {
            fgManager.popBackStack();
        }
        FragmentTransaction fg = fgManager.beginTransaction();
        fragment.setArguments(bundle);
        fg.add(R.id.rl_fgmanager, fragment, tag);
        fg.addToBackStack(tag);
        fg.commit();

    }

    public void changeRadioButtonImage(int btids) {
        int[] image_normal = {R.drawable.tab_recent, R.drawable.tab_contacts, R.drawable.tab_setting};
        int[] image_pressed = {R.drawable.tab_recent_pressed, R.drawable.tab_contacts_pressed,
                R.drawable.tab_setting_pressed};
        int[] rBtnIDs = {R.id.btn_goto_recent, R.id.btn_goto_contacts, R.id.btn_goto_setting};
        switch (btids) {
            case R.id.btn_goto_recent:
                changeImage(image_normal, image_pressed, rBtnIDs, 0);
                currentTab = 1;
                break;
            case R.id.btn_goto_contacts:
                changeImage(image_normal, image_pressed, rBtnIDs, 1);
                currentTab = 2;
                break;
            case R.id.btn_goto_setting:
                changeImage(image_normal, image_pressed, rBtnIDs, 2);
                currentTab = 3;
                break;
            default:
                break;
        }
    }

    public void changeImage(int[] image1, int[] image2, int[] rabtid, int index) {
        for (int i = 0; i < image1.length; i++) {
            if (i != index) {
                ((RadioButton) findViewById(rabtid[i])).setCompoundDrawablesWithIntrinsicBounds(0, image1[i], 0, 0);
            } else {
                ((RadioButton) findViewById(rabtid[i])).setCompoundDrawablesWithIntrinsicBounds(0, image2[i], 0, 0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "keyDown" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if ((event.getKeyCode() == KeyEvent.KEYCODE_BACK) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
            moveTaskToBack(true);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TIMManager.getInstance().removeMessageListener(msgListener);
    }

    private void LoginToIMServer() {
        Intent data = getIntent();
        String userID;
        String userSig;
        TIMUser user = new TIMUser();
        if (data == null) {
            Log.e(TAG, "LoginToIMServer data null");
            return;
        }
        //int login_way = data.getIntExtra(Constants.EXTRA_LOGIN_WAY, Constants.NON_LOGIN);
        LoginSession session = (LoginSession) data.getParcelableExtra("session");
        int login_way = Constants.NON_LOGIN;
        if (session != null) {
            login_way = session.getLoginWay();
        }

        if (login_way == Constants.QQ_LOGIN) {
//			userID = data.getStringExtra(Constants.EXTRA_QQ_OPENID);
//			userSig =  data.getStringExtra(Constants.EXTRA_QQ_ACCESS_TOKEN);	
            userID = session.getOpenid();
            userSig = session.getAccess_token();
            user.setIdentifier(userID);
            user.setAccountType("1");
            //	user.setAppIdAt3rd("222222");
            user.setAppIdAt3rd(TLSConfiguration.QQ_APP_ID);
            MyApplication.getInstance().setThirdIdLogin(true);
        } else if (login_way == Constants.WX_LOGIN) {
            //	userID = data.getStringExtra(Constants.EXTRA_WX_OPENID);
            //	userSig =  data.getStringExtra(Constants.EXTRA_WX_ACCESS_TOKEN);
            userID = session.getOpenid();
            userSig = session.getAccess_token();

            user.setIdentifier(userID);
            user.setAccountType("2");
//			user.setAppIdAt3rd("wx65f71c2ea2b122da");
            user.setAppIdAt3rd(TLSConfiguration.WX_APP_ID);
            MyApplication.getInstance().setThirdIdLogin(true);
        } else if (login_way == Constants.SINA_WEIBO_LOGIN) {
            userID = session.getOpenid();
            userSig = session.getAccess_token();
            user.setIdentifier(userID);
            user.setAccountType("3");
            //	user.setAppIdAt3rd("1119045858");
            user.setAppIdAt3rd(TLSConfiguration.SINA_WEIBO_APP_ID);
            MyApplication.getInstance().setThirdIdLogin(true);
        } else if (login_way == Constants.GUEST_LOGIN) {
            userID = TLSService.getInstance().getGuestIdentifier();
            userSig = TLSService.getInstance().getUserSig(userID);

            user.setIdentifier(userID);
            user.setAccountType(String.valueOf(Constant.ACCOUNT_TYPE));
            user.setAppIdAt3rd(String.valueOf(Constant.SDK_APPID));
            MyApplication.getInstance().setThirdIdLogin(false);
        } else {
            // tls_sdk 更新了id
            userID = TLSService.getInstance().getLastUserIdentifier();
            userSig = TLSService.getInstance().getUserSig(userID);
            user.setIdentifier(userID);
            user.setAccountType(String.valueOf(Constant.ACCOUNT_TYPE));
            user.setAppIdAt3rd(String.valueOf(Constant.SDK_APPID));
            MyApplication.getInstance().setThirdIdLogin(false);
        }
        TLSHelper.userID = userID;
        Log.d(TAG, login_way + ":" + userID + ":" + userSig);

        // 发起到IM后台登录请求
        TIMManager.getInstance().login(Constant.SDK_APPID, user, userSig, new TIMCallBack() {// 回调接口
            @Override
            public void onSuccess() {// 登录成功
                Log.d(TAG, "login succ");
                bLogin = true;

                if (MyApplication.getInstance().bHostRelaytionShip) {
                    UserInfoManagerNew.getInstance().ClearData();
                    UserInfoManagerNew.getInstance().getGroupListFromServer();
                    UserInfoManagerNew.getInstance().getSelfProfile();
                    UserInfoManagerNew.getInstance().getContactsListFromServer();
                    UserInfoManagerNew.getInstance().getBlackListFromServer();
                }

                Log.d(TAG, "start main activity");
                ((RecentFragment) recentFragment).loadRecentContent();
            }

            @Override
            public void onError(int code, String desc) {
                Toast.makeText(getBaseContext(), "IMServer登录失败:" + code + ":" + desc, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "login imserver failed. code: " + code + " errmsg: " + desc);
                // goLoginNewActivity();
                // finish();
            }
        });
    }

}
