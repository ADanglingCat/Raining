package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.model.CountyList;
import com.example.npc.myweather2.util.BaseActivity;

import org.litepal.crud.DataSupport;

import java.util.List;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);

        List<CountyList> countyLists = DataSupport.findAll(CountyList.class);
        Intent intent;
        if (countyLists.size() > 0) {
            boolean flag = true;
            String weatherId = "";
            for (CountyList countyList : countyLists) {
                if (countyList.isMainCity()) {
                    flag = false;
                    weatherId = countyList.getWeatherId();
                    break;
                }
            }
            if (flag) {
                countyLists.get(0).setMainCity(true);
                countyLists.get(0).save();
                weatherId = countyLists.get(0).getWeatherId();
            }
            intent = new Intent(this, Main3Activity.class);
            intent.putExtra("weatherId", weatherId);

        } else {
            PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
            intent = new Intent(this, AreaChooseActivity.class);
            intent.putExtra("isFirst", true);
        }
        startActivity(intent);
        finish();
    }

}
