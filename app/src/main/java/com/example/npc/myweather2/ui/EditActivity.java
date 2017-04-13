package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.util.BaseActivity;

import cn.bmob.v3.BmobUser;

public class EditActivity extends BaseActivity implements View.OnClickListener {
    private Button backBu;
    private TextView titleTx;
    private EditText contentEd;
    private String type;
    private String oldContent;
    private SharedPreferences.Editor editor;
    private static final String TAG = "TAGEditActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        type = intent.getStringExtra("personalType");
        editor= PreferenceManager.getDefaultSharedPreferences(this).edit();
        backBu = (Button) findViewById(R.id.backBu_edit);
        backBu.setOnClickListener(this);
        titleTx = (TextView) findViewById(R.id.title_pe);
        titleTx.setText("修改" + type);
        contentEd = (EditText) findViewById(R.id.edit_content);
        oldContent="";
        if ("昵称".equals(type)) {
            oldContent=(String) BmobUser.getObjectByKey("name");
            contentEd.setText(oldContent);
            contentEd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        }else if ("签名".equals(type)) {
            oldContent=(String) BmobUser.getObjectByKey("sign");
            contentEd.setText(oldContent);
            contentEd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(38)});
        }
//        contentEd.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBu_edit:
                onBackPressed();
                break;
            default:
        }
    }

    public void onBackPressed() {
        String content = contentEd.getText().toString();
        if (content != null && !"".equals(content)&&!oldContent.equals(content)) {
           editor.putBoolean("isChanged",true);
            if ("昵称".equals(type)){
                editor.putString("name",content);
            }else if("签名".equals(type)){
                editor.putString("sign",content);
            }
            editor.apply();
        }
        super.onBackPressed();
        finish();
    }

}
