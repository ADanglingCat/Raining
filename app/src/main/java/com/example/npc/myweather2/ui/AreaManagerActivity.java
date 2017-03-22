package com.example.npc.myweather2.ui;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.util.BaseActivity;

public class AreaManagerActivity extends BaseActivity implements View.OnClickListener{
    Button backBu;
    FloatingActionButton addCityBu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_manager);
        initVar();
        backBu.setOnClickListener(this);
        addCityBu.setOnClickListener(this);
    }
    public void initVar(){
        backBu=(Button)findViewById(R.id.backBu_citymanage);
        addCityBu=(FloatingActionButton)findViewById(R.id.addCityBu);
    }
    @Override
    public void onClick(View view){
        Intent intent;
        switch (view.getId()){
            case R.id.backBu_citymanage:
                intent=new Intent(AreaManagerActivity.this,Main2Activity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.addCityBu:
                intent=new Intent(AreaManagerActivity.this,AreaChooseActivity.class);
                startActivity(intent);
                break;
        }
    }
}
