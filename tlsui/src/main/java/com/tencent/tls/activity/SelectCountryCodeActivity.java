package com.tencent.tls.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tls.customview.CharacterParser;
import com.tencent.tls.customview.EditTextWithClearButton;
import com.tencent.tls.customview.PinyinComparator;
import com.tencent.tls.customview.SideBar;
import com.tencent.tls.customview.SortAdapter;
import com.tencent.tls.customview.SortModel;
import com.tencent.tls.helper.MResource;
import com.tencent.tls.service.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tencent.tls.report.QLog;


public class SelectCountryCodeActivity extends Activity {

    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;        // 显示字母的TextView
    private SortAdapter adapter;
    private EditTextWithClearButton mClearEditText;
    private int login_way;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MResource.getIdByName(getApplication(), "layout", "tencent_tls_ui_activity_select_country_code"));

        login_way = getIntent().getIntExtra(Constants.EXTRA_LOGIN_WAY, Constants.NON_LOGIN);

        Log.e("SelectCountryCode", login_way + "");

        findViewById(MResource.getIdByName(getApplication(), "id", "back")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectCountryCodeActivity.super.onBackPressed();
            }
        });

        initViews();
    }

    private void initViews() {
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) findViewById(MResource.getIdByName(getApplication(), "id", "sidebar"));
        dialog = (TextView) findViewById(MResource.getIdByName(getApplication(), "id", "dialog"));
        sideBar.setTextView(dialog);

        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) findViewById(MResource.getIdByName(getApplication(), "id", "country_lvcountry"));
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
                Toast.makeText(getApplication(), ((SortModel) adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
                SortModel data = (SortModel)adapter.getItem(position);
                Intent intent = new Intent();
                if (login_way == Constants.PHONE_SMS_LOGIN) {
                    intent = new Intent(SelectCountryCodeActivity.this, PhoneSmsLoginActivity.class);
                } else if (login_way == Constants.SMS_REG) {
                    intent = new Intent(SelectCountryCodeActivity.this, PhoneSmsRegisterActivity.class);
                } else if (login_way == Constants.PHONE_PWD_LOGIN) {
                    intent = new Intent(SelectCountryCodeActivity.this, PhonePwdLoginActivity.class);
                } else if (login_way == Constants.PHONE_PWD_REG) {
                    intent = new Intent(SelectCountryCodeActivity.this, PhonePwdRegisterActivity.class);
                } else if (login_way == Constants.PHONE_PWD_RESET) {
                    intent = new Intent(SelectCountryCodeActivity.this, ResetPhonePwdActivity.class);
                }
                intent.putExtra(Constants.COUNTRY_NAME, data.getName());
                intent.putExtra(Constants.COUNTRY_CODE, data.getCountryCode());
                startActivity(intent);
                SelectCountryCodeActivity.this.finish();
            }
        });

        SourceDateList = filledData(getResources().getStringArray(MResource.getIdByName(getApplication(), "array", "tencent_tls_ui_countryCode")));

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(this, SourceDateList);
        sortListView.setAdapter(adapter);


        mClearEditText = (EditTextWithClearButton) findViewById(MResource.getIdByName(getApplication(), "id", "filter_edit"));

        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    /**
     * 为ListView填充数据
     * @param data
     * @return
     */
    private List<SortModel> filledData(String[] data){
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for(int i=0; i<data.length; i++){
            SortModel sortModel = new SortModel();

            int k = data[i].indexOf('+');
            String countryName = data[i].substring(0, k);
            String countryCode = data[i].substring(k+1);

            sortModel.setName(countryName);
            sortModel.setCountryCode(countryCode);

            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(data[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                sortModel.setSortLetters(sortString.toUpperCase());
            }else{
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr){
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if(TextUtils.isEmpty(filterStr)){
            filterDateList = SourceDateList;
        }else{
            filterDateList.clear();
            for(SortModel sortModel : SourceDateList){
                String name = sortModel.getName();
                if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    protected void onDestroy() {
        super.onDestroy();

        sortListView = null;
        sideBar = null;
        dialog = null;
        adapter = null;
        mClearEditText = null;
        characterParser = null;
        SourceDateList = null;
        pinyinComparator = null;

        QLog.i(getClass().getName() + " onDestroy");
    }
}
