package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.os.Bundle;

import com.example.npc.myweather2.model._User;
import com.example.npc.myweather2.util.BaseActivity;

import cn.bmob.v3.BmobUser;

public class CheckStateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }
    public void onResume(){
        super.onResume();
       // SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
       // boolean state=preferences.getBoolean("state",false);
        _User user= BmobUser.getCurrentUser(_User.class);
        Intent intent;
        if(user!=null){
            intent=new Intent(this, PersonalActivity.class);
        }else{
            intent=new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
