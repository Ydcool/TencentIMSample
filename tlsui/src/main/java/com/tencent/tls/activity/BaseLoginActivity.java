package com.tencent.tls.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.tencent.tls.service.TLSService;

public class BaseLoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            try {
                TLSService.getInstance().srcActivity.finish();
                onBackPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
