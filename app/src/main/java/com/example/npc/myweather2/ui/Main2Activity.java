package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.util.MyUtil;

public class Main2Activity extends AppCompatActivity implements GestureDetector.OnGestureListener{
    public DrawerLayout drawerLayout;
    private Button menuBu;
    public GestureDetector gestureDetector;
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    TextView cityTx;
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
        setContentView(R.layout.activity_main2);
        initVar();
//        Intent intent=getIntent();
//        MyApplication myApplication=(MyApplication)getApplication();
//        Bitmap bitmap;
//        BitmapDrawable drawable=null;
//        boolean flag=intent.getBooleanExtra("flag",false);
//        if((bitmap=myApplication.getMyBitmap())!=null){
//            flag=true;
//            drawable=new BitmapDrawable(getResources(),bitmap);
//        }
//        if(flag){
//            drawerLayout.setBackground(drawable);
//        }else{
            drawerLayout.setBackgroundResource(R.drawable.ic_background);
//        }
        menuBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        NavigationView navigationView=(NavigationView)findViewById(R.id.menuNa);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.cityMe:
                        intent=new Intent(Main2Activity.this,AreaManagerActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.settingMe:
                        intent=new Intent(Main2Activity.this,SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.updateMe:
                        MyUtil.showToast(Main2Activity.this,"没有新版本");
                        break;
                    case R.id.aboutMe:
                        intent=new Intent(Main2Activity.this,AboutUsActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }
    public void initVar(){
        linearLayout=(LinearLayout)findViewById(R.id.li_weather_layout);
        cityTx=(TextView)findViewById(R.id.cityTx);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        menuBu=(Button)findViewById(R.id.menuBu);
        scrollView=(ScrollView)findViewById(R.id.sv_weather_layout);
        gestureDetector=new GestureDetector(this, this);
    }
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1!=null&&e2!=null){
            if(e1.getX()-e2.getX()>200&&Math.abs(e1.getY()-e2.getY())<50){
                cityTx.setText("深圳");
                return true;
            }else if(e2.getX()-e1.getX()>=200&&Math.abs(e1.getY()-e2.getY())<50){
                cityTx.setText("项城");
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        //drawerLayout.onTouchEvent(ev);
        //让GestureDetector响应触碰事件
        gestureDetector.onTouchEvent(ev);
        //让Activity响应触碰事件
        super.dispatchTouchEvent(ev);
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
