package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.example.npc.myweather2.R;

public class AreaChooseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_choose);
    }
//    public void onBackPressed(){
//        Log.d("TAGDFY", "onBackPressed: ");
//        Intent intent=new Intent(AreaChooseActivity.this,Main2Activity.class);
//        startActivity(intent);
//    }
    public boolean onKeyDown(int keyCode,KeyEvent keyEvent){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent(AreaChooseActivity.this,Main2Activity.class);
            startActivity(intent);
            return true;
        }else{
            return super.onKeyDown(keyCode,keyEvent);
        }
    }
}
