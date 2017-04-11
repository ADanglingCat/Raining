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

public class EditActivity extends BaseActivity implements View.OnClickListener {
    private Button backBu;
    private TextView titleTx;
    private EditText contentEd;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EditActivity.this);
        Intent intent = getIntent();
        type = intent.getStringExtra("personalType");
        backBu = (Button) findViewById(R.id.backBu_edit);
        backBu.setOnClickListener(this);
        titleTx = (TextView) findViewById(R.id.title_pe);
        titleTx.setText("修改" + type);
        contentEd = (EditText) findViewById(R.id.edit_content);
        contentEd.setText(preferences.getString(type, null));
        if ("昵称".equals(type)) {
            contentEd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        }
        if ("签名".equals(type)) {
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
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(EditActivity.this).edit();
        String content = contentEd.getText().toString();
        if (content != null && !"".equals(content)) {
            editor.putString(type, content);
        }
        editor.apply();
        super.onBackPressed();
        finish();
    }

}
