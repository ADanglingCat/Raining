package com.example.npc.myweather2.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.npc.myweather2.R;

public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getFragmentManager().beginTransaction()
                .replace(R.id.setting_fram,new PreferenceFragment())
                .commit();
    }
}
