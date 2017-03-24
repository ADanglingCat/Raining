package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.model.County;
import com.example.npc.myweather2.model.CountyList;
import com.example.npc.myweather2.util.AreaManageAdapter;
import com.example.npc.myweather2.util.BaseActivity;

import org.litepal.crud.DataSupport;

import java.util.List;

public class AreaManagerActivity extends BaseActivity implements View.OnClickListener{
    private Button backBu;
    private ListView listView;
    private FloatingActionButton addCityBu;
    private List<CountyList> countyCollect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_manager);
        initVar();
        backBu.setOnClickListener(this);
        addCityBu.setOnClickListener(this);
        countyCollect= DataSupport.findAll(CountyList.class);
        AreaManageAdapter adapter=new AreaManageAdapter(this,R.layout.area_manager_item,countyCollect);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CountyList countyList=countyCollect.get(position);
                List<County> counties= DataSupport.where("id=?",countyList.getCountyId()+"").find(County.class);
                Intent intent=new Intent(AreaManagerActivity.this,Main2Activity.class);
                intent.putExtra("weatherId",counties.get(0).getWeatherId());
                startActivity(intent);
                finish();
            }
        });
        listView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });

    }
    public void initVar(){
        backBu=(Button)findViewById(R.id.backBu_citymanage);
        addCityBu=(FloatingActionButton)findViewById(R.id.addCityBu);
        listView=(ListView)findViewById(R.id.area_manage_listView);
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
