package com.example.npc.myweather2.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.util.BaseActivity;

public class SettingActivity extends BaseActivity implements View.OnClickListener{
   private Button backBu_setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getFragmentManager().beginTransaction()
                .replace(R.id.setting_fram,new PreferenceFragment())
                .commit();
        backBu_setting=(Button)findViewById(R.id.backBu_setting);
        backBu_setting.setOnClickListener(this);
    }
    public void onClick(View view){
                onBackPressed();
                finish();
    }
}
