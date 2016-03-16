package com.tencent.tls.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.tls.helper.MResource;
import com.tencent.tls.service.TLSService;

import tencent.tls.report.QLog;


public class StrAccRegisterActivity extends Activity {

    public final static String TAG = "StrAccRegisterActivity";
    private TLSService tlsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MResource.getIdByName(getApplication(), "layout", "tencent_tls_ui_activity_independent_register"));

        tlsService = TLSService.getInstance();
        tlsService.initAccountRegisterService(this,
                (TextView) findViewById(MResource.getIdByName(getApplication(), "id", "username")),
                (TextView) findViewById(MResource.getIdByName(getApplication(), "id", "password")),
                (TextView) findViewById(MResource.getIdByName(getApplication(), "id", "repassword")),
                findViewById(MResource.getIdByName(getApplication(), "id", "btn_register"))
        );

        // 设置返回按钮
        findViewById(MResource.getIdByName(getApplication(), "id", "returnIndependentLoginActivity"))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBackPressed();
                    }
                });
    }

    protected void onDestroy() {
        super.onDestroy();

        tlsService = null;

        QLog.i(getClass().getName() + " onDestroy");
    }

}
