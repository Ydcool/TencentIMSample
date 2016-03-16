package com.example.mydemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;
import tencent.tls.report.QLog;
import com.example.mydemo.R;
import com.example.mydemo.TLSHelper;

/**
 * Created by kinkahuang on 2015/6/3.
 */
public class ImgVerifyActivity extends Activity implements View.OnClickListener {
	private final static String TAG = ImgVerifyActivity.class.getSimpleName();
    EditText imgcodeEdit;
    public ImageView imgcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verify_img);

        imgcodeEdit = (EditText) findViewById(R.id.imgcode);
        imgcodeView = (ImageView) findViewById(R.id.imgcodeView);
        imgcodeView.setOnClickListener(this);

        Intent intent = getIntent();
        byte[] picData = intent.getByteArrayExtra("picdata");

        fillImageview(picData);
        findViewById(R.id.btn_verify).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_verify:
                String imgcode = imgcodeEdit.getText().toString().trim();
                if(imgcode.length() ==0){
                	Toast.makeText(getBaseContext(), "请输入上图中的验证码!", Toast.LENGTH_SHORT).show();
                	return;
                }
                Log.d(TAG,"login:" + imgcode);
       //         TLSHelper.loginHelper.TLSPwdLoginVerifyImgcode(imgcode, new PwdLoginListener());               
                break;
            case R.id.imgcodeView:
           // 	TLSHelper.loginHelper.TLSPwdLoginReaskImgcode(new PwdLoginListener());
                break;
        }
    }

    public  void fillImageview(byte[] picData) {
        if (picData == null)
            return;
        Bitmap bm = BitmapFactory.decodeByteArray(picData, 0, picData.length);
        QLog.i("w " + bm.getWidth() + ", h " + bm.getHeight());
        imgcodeView.setImageBitmap(bm);
    }

	public void showToast(String msg) {
		Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
	}

	private void notOK(TLSErrInfo errInfo) {
		showToast(String.format("%s: ret: %d, msg: %s", errInfo.ErrCode == TLSErrInfo.TIMEOUT ? "网络超时" : "错误", errInfo.ErrCode, errInfo.Msg));
	}    
	
	class PwdLoginListener implements TLSPwdLoginListener {
		@Override
		public void OnPwdLoginSuccess(TLSUserInfo userInfo) {
			showToast("密码登录成功！");
			Log.d(TAG,"密码登录成功");
			startActivity(new Intent(ImgVerifyActivity.this,MainActivity.class));
			 finish();
		}

		@Override
		public void OnPwdLoginReaskImgcodeSuccess(byte[] picData) {
			Log.d(TAG,"OnPwdLoginReaskImgcodeSuccess");
			fillImageview(picData);
		}

		@Override
		public void OnPwdLoginNeedImgcode(byte[] picData, TLSErrInfo errInfo) {
			Log.d(TAG,"OnPwdLoginNeedImgcode");
			Toast.makeText(getBaseContext(), "验证码错误，重新输入！", Toast.LENGTH_SHORT).show();
			fillImageview(picData);
		}

		@Override
		public void OnPwdLoginFail(TLSErrInfo errInfo) {
			Log.e(TAG,"OnPwdLoginFail");			
			notOK(errInfo);
		}

		@Override
		public void OnPwdLoginTimeout(TLSErrInfo errInfo) {
			Log.e(TAG,"OnPwdLoginTimeout");
			notOK(errInfo);
		}
	}    
}
