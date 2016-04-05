package com.example.mydemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mydemo.MyApplication;
import com.example.mydemo.R;
import com.example.mydemo.c2c.HttpProcessor;
import com.example.mydemo.utils.CommonHelper;
import com.example.mydemo.utils.Constant;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMManager;
import com.tencent.TIMUser;
import com.tencent.TIMValueCallBack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends MyBaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText mAccount, mPassword;
    private MyApplication application;
    private final static int GO_TO_REGISTER = 1;
    private StringBuilder mStrRetMsg;
    private StringBuilder strUserSig;
    private String mStrUserName, mStrPassWord;
    boolean mBLogin = false;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        application = (MyApplication) this.getApplicationContext();
        onInit();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void onInit() {
        mAccount = (EditText) findViewById(R.id.login_account);
        mPassword = (EditText) findViewById(R.id.login_password);
        mStrRetMsg = new StringBuilder();
        strUserSig = new StringBuilder();
        String tmpName = MyApplication.getInstance().getUserName();
        if (tmpName != null) {
            mAccount.setText(tmpName);
        }
//		 mStrUserName = mAccount.getText().toString().trim();
//		 mStrPassWord = mPassword.getText().toString().trim();    
    }


    public void onLogin(View view) {
        if (mBLogin) {
            Toast.makeText(this, "已经在登陆中，请稍等！", Toast.LENGTH_SHORT).show();
            return;
        }
        mBLogin = true;
        mStrUserName = mAccount.getText().toString().trim();
        mStrPassWord = mPassword.getText().toString().trim();
        if (!CommonHelper.GetNetWorkStatus(getBaseContext())) {
            Toast.makeText(getBaseContext(), "网络不可用，请检查网络设置!", Toast.LENGTH_LONG).show();
            mBLogin = false;
            return;
        }
        if ((mStrUserName.length() == 0) || (mStrPassWord.length() == 0)) {
            Toast.makeText(this, "亲！帐号或密码不能为空哦", Toast.LENGTH_SHORT).show();
            mBLogin = false;
            return;
        }

//		if (application.isClientStart()){
//			LoginToAppServer();
//		}else{
//			Toast.makeText(this, "亲！sdk暂未初始化", Toast.LENGTH_SHORT).show();				
//		}

    }


    public void onGoToResgiter(View view) {
        Intent intent = new Intent();
        intent.setClass(this, RegisterActivity.class);
        startActivityForResult(intent, GO_TO_REGISTER);
    }

    private void LoginToAppServer() {
        //发起到app c2c关系链后台验证是否有注册成功过的
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Log.d(TAG, "log to app:" + mStrUserName + ":" + mStrPassWord);
                final int iRet = HttpProcessor.doRequestLogin(mStrUserName, mStrPassWord, mStrRetMsg, strUserSig);
                Log.d(TAG, iRet + ":" + mStrRetMsg + ":" + strUserSig);
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (iRet == 0) {
                            getContactsFromServer();
                        } else {
                            Toast.makeText(getBaseContext(), "APPServer登录失败:" + iRet + ":" + mStrRetMsg, Toast.LENGTH_SHORT).show();
                            mBLogin = false;
                        }
                    }
                });
            }

        }).start();

    }

    private void getContactsFromServer() {
        //	final String userName = mAccount.getText().toString().trim();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                final int iRet = HttpProcessor.doRequestGetFriend(mStrUserName, mStrRetMsg);
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (iRet == 0) {
                            LoginToIMServer();
                        } else {
                            Toast.makeText(getBaseContext(), "获取好友列表:" + iRet + ":" + mStrRetMsg, Toast.LENGTH_SHORT).show();
                            mBLogin = false;
                        }
                    }
                });
            }

        }).start();
    }

    private void LoginToIMServer() {
//		 final String userName = mAccount.getText().toString().t;
        TIMUser user = new TIMUser();
        user.setAccountType(String.valueOf(Constant.ACCOUNT_TYPE));
        user.setAppIdAt3rd(String.valueOf(Constant.SDK_APPID));
        user.setIdentifier(mStrUserName);

        //发起到IM后台登录请求
        TIMManager.getInstance().login(Constant.SDK_APPID, user, strUserSig.toString(),
                new TIMCallBack() {//回调接口
                    @Override
                    public void onSuccess() {//登录成功
                        Log.d(TAG, "login succ");
                        MyApplication.getInstance().setUserName(mStrUserName);
                        MyApplication.getInstance().setUserSig(strUserSig.toString());
                        TIMGroupManager.getInstance().getGroupList(new TIMValueCallBack<List<TIMGroupBaseInfo>>() {
                            @Override
                            public void onError(int code, String desc) {
                                Log.e(TAG, "get gruop list failed: " + code + " desc");
                                Toast.makeText(getBaseContext(), "获取群列表失败!", Toast.LENGTH_SHORT).show();
                                mBLogin = false;
                            }

                            @Override
                            public void onSuccess(List<TIMGroupBaseInfo> arg0) {
                                // TODO Auto-generated method stub
                                //缓存群列表。生成群id和群名称的对应关系
                                Map<String, String> mGroup = new HashMap<String, String>();
                                for (TIMGroupBaseInfo baseInfo : arg0) {
                                    mGroup.put(baseInfo.getGroupId(), baseInfo.getGroupName());
                                }
                                //	UserInfoManager.getInstance().setGroupID2Name(mGroup);

                                Intent intent = new Intent();
                                //	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setClass(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                mBLogin = false;
                            }
                        });


                    }

                    @Override
                    public void onError(int code, String desc) {
                        Toast.makeText(getBaseContext(), "IMServer登录失败:" + code + ":" + desc, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "login imserver failed. code: " + code + " errmsg: " + desc);
                        mBLogin = false;
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GO_TO_REGISTER) {
            if (MyApplication.getInstance().getUserName() != null) {
                mAccount.setText(MyApplication.getInstance().getUserName());
                mPassword.setText("");
            }

        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        TIMManager.getInstance().logout();
        System.exit(0);
    }
}


