package com.example.npc.myweather2.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.gson.DailyForecast;
import com.example.npc.myweather2.gson.HourlyForecast;
import com.example.npc.myweather2.gson.Weather;
import com.example.npc.myweather2.util.ActivityCollector;
import com.example.npc.myweather2.util.BaseActivity;
import com.example.npc.myweather2.util.MyUtil;

import junit.framework.Assert;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.npc.myweather2.R.id.daily_dateTx;

public class Main2Activity extends BaseActivity implements GestureDetector.OnGestureListener{
    public DrawerLayout drawerLayout;
    private Button menuBu;
    public GestureDetector gestureDetector;
    public SwipeRefreshLayout swipeRefresh;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView aqiText;
    private TextView flText;
    private TextView windText;
    private TextView astroText;

    private LinearLayout hourlylayout;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private TextView travelText;
    private TextView uvText;
    private TextView fluText;
    private TextView airText;
    private TextView designText;
    private TextView title_comfortText;
    private TextView title_carWashText;
    private TextView title_sportText;
    private TextView title_travelText;
    private TextView title_uvText;
    private TextView title_fluText;
    private TextView title_airText;
    private TextView title_designText;
    private ImageView backgroundImg;
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
        String imagePath;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("imagePath",null)!=null){
            imagePath=prefs.getString("imagePath", null);
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            backgroundImg.setImageBitmap(bitmap);
        }else{
            backgroundImg.setImageResource(R.drawable.ic_background);
        }
//        Intent intent=getIntent();
//        imagePath=intent.getStringExtra("imagePath");
//        if(imagePath!=null){
//            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Main2Activity.this).edit();
//            editor.putString("imagePath", imagePath);
//            editor.apply();
//            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
//            backgroundImg.setImageBitmap(bitmap);
//        }else{
//            backgroundImg.setImageResource(R.drawable.ic_background);
//        }
        String weatherString = prefs.getString("weather", null);
        final String weatherId;
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = MyUtil.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
           // Log.d("TAG", "onCreate: 1111"+weatherId);
            showWeatherInfo(weather);
        } else {
            // 无缓存时去服务器查询天气
            weatherId = getIntent().getStringExtra("weatherId");
           // Log.d("TAG", "onCreate: "+weatherId);
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
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
    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + weatherId + "&key=d1169cf0b6fa4bc9a43253890b582a5b";
        MyUtil.sendRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = MyUtil.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Main2Activity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            MyUtil.showToast(Main2Activity.this, "获取天气信息失败");                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyUtil.showToast(Main2Activity.this, "获取天气信息失败");
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        //loadBingPic();
    }
    public int getDrawable(Context context, String name)
    {
        Assert.assertNotNull(context);
        Assert.assertNotNull(name);

        return context.getResources().getIdentifier(name,
                "drawable", context.getPackageName());
    }
    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.tmp + "℃";
        String weatherInfo = weather.now.cond.txt;
        String windDir=weather.now.wind.dir;
        String windSc=weather.now.wind.sc;
        String fl=weather.now.fl;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime+"更新");
        flText.setText("体感温度:"+fl+"℃");
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        windText.setText("风向/风力:"+windDir+"/"+windSc);
        forecastLayout.removeAllViews();
        hourlylayout.removeAllViews();
        boolean flag=true;
        for(HourlyForecast forecast:weather.hourlyForecasts){
            View view=LayoutInflater.from(this).inflate(R.layout.hourly_item,hourlylayout,false);
            TextView tmpText=(TextView)view.findViewById(R.id.hour_tmpTx);
            ImageView image=(ImageView)view.findViewById(R.id.hour_imageTx);
            TextView hourText=(TextView)view.findViewById(R.id.hour_time);
            String code="ic_"+forecast.cond.code;
            int id=getDrawable(Main2Activity.this,code);
            image.setImageResource(id);
            tmpText.setText(forecast.tmp+"℃");
            hourText.setText(forecast.date.substring(11));
            hourlylayout.addView(view);
        }
        for (DailyForecast forecast : weather.dailyForecasts) {
            View view = LayoutInflater.from(this).inflate(R.layout.daily_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(daily_dateTx);
            TextView condText = (TextView) view.findViewById(R.id.daily_condTx);
            TextView popText = (TextView) view.findViewById(R.id.daily_popTx);
            TextView maxText = (TextView) view.findViewById(R.id.daily_maxTx);
            TextView minText = (TextView) view.findViewById(R.id.daily_minTx);
            dateText.setText(forecast.date.substring(5));
            condText.setText(forecast.cond.txt_d);
            popText.setText(forecast.pop+"%");
            maxText.setText(forecast.tmp.max+"℃");
            minText.setText(forecast.tmp.min+"℃");
            forecastLayout.addView(view);
            if(flag){
                String sr=forecast.astro.sr;//日出
                String ss=forecast.astro.ss;//日落
                astroText.setText("日出/日落:"+sr+"/"+ss);
                flag=false;
            }
        }
        if (weather.aqi != null) {
            aqiText.setText("空气质量:"+weather.aqi.city.aqi+"/"+weather.aqi.city.qlty);
        }
        String comfort =weather.suggestion.comf.txt;
        String carWash =weather.suggestion.cw.txt;
        String sport =weather.suggestion.sport.txt;
        String drsg=weather.suggestion.drsg.txt;
        String uv=weather.suggestion.uv.txt;
        String air=weather.suggestion.air.txt;
        String trav=weather.suggestion.trav.txt;
        String flu=weather.suggestion.flu.txt;
        String title_comfort = "舒适度指数：" + weather.suggestion.comf.brf;
        String title_carWash = "洗车指数：" + weather.suggestion.cw.brf;
        String title_sport = "运动指数：" + weather.suggestion.sport.brf;
        String title_drsg="穿衣指数:"+weather.suggestion.drsg.brf;
        String title_uv="紫外线指数:"+weather.suggestion.uv.brf;
        String title_air="污染指数:"+weather.suggestion.air.brf;
        String title_trav="旅行指数:"+weather.suggestion.trav.brf;
        String title_flu="感冒指数:"+weather.suggestion.flu.brf;

        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        travelText.setText(trav);
        uvText.setText(uv);
        fluText.setText(flu);
        airText.setText(air);
        designText.setText(drsg);
        title_comfortText.setText(title_comfort);
        title_carWashText.setText(title_carWash);
        title_sportText.setText(title_sport);
        title_travelText.setText(title_trav);
        title_uvText.setText(title_uv);
        title_fluText.setText(title_flu);
        title_airText.setText(title_air);
        title_designText.setText(title_drsg);

        weatherLayout.setVisibility(View.VISIBLE);
//        Intent intent = new Intent(this, AutoUpdateService.class);
//        startService(intent);
    }
    public void initVar(){
        titleCity=(TextView)findViewById(R.id.titleCity);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        menuBu=(Button)findViewById(R.id.menuBu);
        gestureDetector=new GestureDetector(this, this);
        backgroundImg = (ImageView) findViewById(R.id.backgroundIm);
        weatherLayout = (ScrollView) findViewById(R.id.sv_weather_layout);
        titleCity = (TextView) findViewById(R.id.titleCity);
        titleUpdateTime = (TextView) findViewById(R.id.timeTx);
        degreeText = (TextView) findViewById(R.id.tmpTx);
        flText= (TextView) findViewById(R.id.flTx);
        windText= (TextView) findViewById(R.id.windTx);
        astroText= (TextView) findViewById(R.id.astroTx);
        hourlylayout = (LinearLayout) findViewById(R.id.hourly_layout);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);

        weatherInfoText = (TextView) findViewById(R.id.condTx);
        aqiText = (TextView) findViewById(R.id.aqiTx);
        comfortText = (TextView) findViewById(R.id.comf_sug);
        carWashText = (TextView) findViewById(R.id.cw_sug);
        sportText = (TextView) findViewById(R.id.sport_sug);
        travelText=(TextView) findViewById(R.id.trav_sug);
        uvText= (TextView) findViewById(R.id.uv_sug);
        fluText= (TextView) findViewById(R.id.flu_sug);
        airText= (TextView) findViewById(R.id.air_sug);
        designText= (TextView) findViewById(R.id.drsg_sug);
        title_comfortText = (TextView) findViewById(R.id.suggestion_comf);
        title_carWashText = (TextView) findViewById(R.id.suggestion_cw);
        title_sportText = (TextView) findViewById(R.id.suggestion_sport);
        title_travelText=(TextView) findViewById(R.id.suggestion_travel);
        title_uvText= (TextView) findViewById(R.id.suggestion_uv);
        title_fluText= (TextView) findViewById(R.id.suggestion_flu);
        title_airText= (TextView) findViewById(R.id.suggestion_air);
        title_designText= (TextView) findViewById(R.id.suggestion_drsg);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
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
//        if(e1!=null&&e2!=null){
//            if(e1.getX()-e2.getX()>200&&Math.abs(e1.getY()-e2.getY())<50){
//               titleCity.setText("深圳");
//                return false;
//            }else if(e2.getX()-e1.getX()>=200&&Math.abs(e1.getY()-e2.getY())<50){
//                titleCity.setText("项城");
//                return false;
//            }
//        }
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
    public void onBackPressed(){
        ActivityCollector.removeAll();
        //android.os.Process.killProcess(android.os.Process.myPid());
    }
}
