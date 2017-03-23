package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.db.County;
import com.example.npc.myweather2.db.CountyList;
import com.example.npc.myweather2.util.BaseActivity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class AreaManagerActivity extends BaseActivity implements View.OnClickListener{
    private Button backBu;
    private FloatingActionButton addCityBu;
    private LinearLayout areaManageLayout;
    private List<CountyList> countyCollect;
    private List<County> counties;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_manager);
        initVar();
        backBu.setOnClickListener(this);
        addCityBu.setOnClickListener(this);
        countyCollect= DataSupport.findAll(CountyList.class);
        if(countyCollect.size()>0){
            areaManageLayout.removeAllViews();
            counties=new ArrayList<>();
            counties.clear();
            for(CountyList countyList:countyCollect){
                List<County> data=DataSupport.where("id=?",countyList.getCountyId()+"").find(County.class);
                if(data.size()>0){
                    County county=data.get(0);
                    counties.add(county);
                }
            }
            if(counties.size()>0){
                for(County county:counties){
                    View view= LayoutInflater.from(this).inflate(R.layout.area_manager_item,areaManageLayout,false);
                    TextView cityFirstName=(TextView)view.findViewById(R.id.cityFirstName_manager);
                    TextView cityName=(TextView)view.findViewById(R.id.cityName_manager);
                    cityFirstName.setText(county.getCountyName().substring(0,1));
                    cityName.setText(county.getCountyName());
                    areaManageLayout.addView(view);
                }
            }
        }

    }
    public void initVar(){
        backBu=(Button)findViewById(R.id.backBu_citymanage);
        addCityBu=(FloatingActionButton)findViewById(R.id.addCityBu);
        areaManageLayout=(LinearLayout)findViewById(R.id.area_manage);
//        counties=;
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
                finish();
                break;
        }
    }
}
