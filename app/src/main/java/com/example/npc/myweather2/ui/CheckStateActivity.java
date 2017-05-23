package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.os.Bundle;

import com.example.npc.myweather2.model._User;
import com.example.npc.myweather2.util.BaseActivity;

import cn.bmob.v3.BmobUser;
//如果用户已登录就跳转到个人中心，否则跳转到登录界面
public class CheckStateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }
    public void onResume(){
        super.onResume();
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
