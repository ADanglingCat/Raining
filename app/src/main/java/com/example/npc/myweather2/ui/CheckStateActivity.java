package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.npc.myweather2.util.BaseActivity;

public class CheckStateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }
    public void onResume(){
        super.onResume();
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        boolean state=preferences.getBoolean("state",false);
        Intent intent;
        if(state){
            intent=new Intent(this, PersonalActivity.class);
        }else{
            intent=new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
