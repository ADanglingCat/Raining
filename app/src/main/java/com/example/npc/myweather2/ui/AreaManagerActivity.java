package com.example.npc.myweather2.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.npc.myweather2.R;
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
        final AreaManageAdapter adapter=new AreaManageAdapter(this,R.layout.area_manager_item,countyCollect);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final CountyList countyList=countyCollect.get(position);
                AlertDialog.Builder builder=new AlertDialog.Builder(AreaManagerActivity.this)
                        .setItems(R.array.area_manage_select_list, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        Intent intent=new Intent(AreaManagerActivity.this,Main2Activity.class);
                                        intent.putExtra("weatherId",countyList.getWeatherId());
                                        Log.d("TAG", "1111"+countyList.getWeatherId());
                                        startActivity(intent);
                                        finish();
                                        break;
                                    case 1:
//                                        ContentValues values=new ContentValues();
//                                        values.put("mainCity",false);
//                                        DataSupport.updateAll(CountyList.class,values,"mainCity=?","1");
                                        CountyList countyList1=new CountyList();
                                        countyList1.setMainCity(false);
                                        countyList1.setToDefault("mainCity");
                                        countyList1.updateAll();
                                        countyList.setMainCity(true);
                                        countyList.save();
                                        Log.d("TAG", "default: "+countyList.getWeatherId());
                                        break;
                                    case 2:
                                        countyList.delete();
                                        countyCollect.remove(position);
                                        adapter.notifyDataSetChanged();
                                        break;
                                }
                            }
                        });
                builder.create().show();


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
                onBackPressed();
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
