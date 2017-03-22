package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.npc.myweather2.R;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
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
        Intent intent;
        intent=new Intent(SettingActivity.this,Main2Activity.class);
        startActivity(intent);
        finish();
    }
}
