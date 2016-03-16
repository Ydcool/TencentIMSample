package com.tencent.tls.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.tencent.tls.helper.MResource;
import com.tencent.tls.service.StrAccLoginService;
import com.tencent.tls.service.Constants;
import com.tencent.tls.service.PhonePwdLoginService;
import com.tencent.tls.service.TLSService;

import tencent.tls.report.QLog;

public class ImgCodeActivity extends Activity implements View.OnClickListener{

    private final static String TAG = "ImgCodeActivity";
    private static ImageView imgcodeView;

    private EditText imgcodeEdit;
    private TLSService tlsService;
    private int login_way;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MResource.getIdByName(getApplication(), "layout", "tencent_tls_ui_activity_img_code"));

        tlsService = TLSService.getInstance();

        imgcodeEdit = (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "txt_checkcode"));
        imgcodeView = (ImageView) findViewById(MResource.getIdByName(getApplication(), "id", "imagecode"));
        imgcodeView.setOnClickListener(this);

        Intent intent = getIntent();
        byte[] picData = intent.getByteArrayExtra(Constants.EXTRA_IMG_CHECKCODE);
        login_way = intent.getIntExtra(Constants.EXTRA_LOGIN_WAY, Constants.NON_LOGIN);

        fillImageview(picData);
        findViewById(MResource.getIdByName(getApplication(), "id", "btn_verify")).setOnClickListener(this);
        findViewById(MResource.getIdByName(getApplication(), "id", "btn_cancel")).setOnClickListener(this);
        findViewById(MResource.getIdByName(getApplication(), "id", "refreshImageCode")).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == MResource.getIdByName(getApplication(), "id", "btn_verify")) {
            String imgcode = imgcodeEdit.getText().toString();
            if (login_way == Constants.PHONE_PWD_LOGIN) {
                tlsService.TLSPwdLoginVerifyImgcode(imgcode, PhonePwdLoginService.pwdLoginListener);
            } else if (login_way == Constants.STR_ACC_LOGIN) {
                tlsService.TLSPwdLoginVerifyImgcode(imgcode, StrAccLoginService.pwdLoginListener);
            }
            finish();
        } else if (v.getId() == MResource.getIdByName(getApplication(), "id", "imagecode")
                || v.getId() == MResource.getIdByName(getApplication(), "id", "refreshImageCode")) { // 刷新验证码
            tlsService.TLSPwdLoginReaskImgcode(StrAccLoginService.pwdLoginListener);
        } if (v.getId() == MResource.getIdByName(getApplication(), "id", "btn_cancel")) {
            onBackPressed();
        }
    }

    public static void fillImageview(byte[] picData) {
        if (picData == null)
            return;
        Bitmap bm = BitmapFactory.decodeByteArray(picData, 0, picData.length);
        Log.e(TAG, "w " + bm.getWidth() + ", h " + bm.getHeight());
        imgcodeView.setImageBitmap(bm);
    }

    protected void onDestroy() {
        super.onDestroy();

        QLog.i(getClass().getName() + " onDestroy");

        tlsService = null;
        imgcodeEdit = null;
    }
}
