package com.example.npc.myweather2.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.example.npc.myweather2.R;
import com.example.npc.myweather2.gson.DailyForecast;
import com.example.npc.myweather2.gson.HourlyForecast;
import com.example.npc.myweather2.gson.Weather;
import com.example.npc.myweather2.model.County;
import com.example.npc.myweather2.model.CountyList;
import com.example.npc.myweather2.util.ActivityCollector;
import com.example.npc.myweather2.util.BaseActivity;
import com.example.npc.myweather2.util.MyUtil;

import junit.framework.Assert;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.npc.myweather2.R.id.daily_dateTx;

public class Main2Activity extends BaseActivity implements GestureDetector.OnGestureListener {
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
    private Resources resources;
    private Button voiceBu;
    public String weatherId;
    public TextToSpeech tts;
    public String voiceWeather;
    public String bingPic;
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
        setBackgroundByBing();
        drawerLayout.closeDrawer(GravityCompat.START);
        String imagePath;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("imagePath", null) != null) {
            imagePath = prefs.getString("imagePath", null);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            backgroundImg.setImageBitmap(bitmap);
        } else {
            //backgroundImg.setImageResource(R.drawable.ic_background);
            setBackgroundByBing();
        }
        String id=getIntent().getStringExtra("weatherId");
        if(id!=null){

            weatherId = id;
        }else {
            List<CountyList> countyLists= DataSupport.where("mainCity=?","true").find(CountyList.class);
            List<County> counties;
            if(countyLists.size()>0){
                counties=DataSupport.where("id=?",countyLists.get(0).getCountyId()+"").find(County.class);
            }else{
                List<CountyList> countyListss= DataSupport.findAll(CountyList.class);
                counties=DataSupport.where("id=?",countyListss.get(0).getCountyId()+"").find(County.class);
            }
            weatherId=counties.get(0).getWeatherId();
        }
        String weatherString = prefs.getString("weather"+weatherId, null);
//        String wweatherId = getIntent().getStringExtra("weatherId");
//        Log.d("TAG", "onCreate: 1111"+wweatherId);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = MyUtil.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
//             Log.d("TAG", "onCreate: 1111"+weatherId);
            showWeatherInfo(weather);
        } else {
            // 无缓存时去服务器查询天气

//            Log.d("TAG", "onCreate:2222"+weatherId);
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
                requestBing();
            }
        });
        menuBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        tts=new TextToSpeech(Main2Activity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS){
                    int result=tts.setLanguage(Locale.CHINA);
                    if(result!=TextToSpeech.LANG_COUNTRY_AVAILABLE&&result!=TextToSpeech.LANG_AVAILABLE){
                        MyUtil.showToast(Main2Activity.this,"无法语音播报");
                    }
                }
            }
        });
        voiceBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tts.isSpeaking()){
                   tts.stop();
                    return;
                }else{
                    if (Build.VERSION.SDK_INT >= 21){
                        tts.speak(voiceWeather,TextToSpeech.QUEUE_FLUSH,null,"speech");
                       Log.d("TAG", "onClick: "+voiceWeather);

                    }else{
                        MyUtil.showToast(Main2Activity.this,"无法语音播报");
                    }
                }
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.menuNa);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.cityMe:
                        intent = new Intent(Main2Activity.this, AreaManagerActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.settingMe:
                        intent = new Intent(Main2Activity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.updateMe:
                        MyUtil.showToast(Main2Activity.this, "没有新版本");
                        break;
                    case R.id.aboutMe:
                        intent = new Intent(Main2Activity.this, AboutUsActivity.class);
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
    //必应每日一图设置背景
    public void setBackgroundByBing(){
        SharedPreferences preference=PreferenceManager.getDefaultSharedPreferences(Main2Activity.this);
        bingPic=preference.getString("bingPic",null);
        if(bingPic!=null){
            Glide.with(Main2Activity.this).load(bingPic).into(backgroundImg);
        }else{
            requestBing();
        }
    }
    //获取必应每日一图
    public void requestBing(){
       MyUtil.sendRequest(resources.getString(R.string.bingPicture), new Callback() {
           @Override
           public void onFailure(Call call, IOException e) {
               e.printStackTrace();
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       MyUtil.showToast(Main2Activity.this, "获取图片失败");
                       swipeRefresh.setRefreshing(false);
                   }
               });
           }

           @Override
           public void onResponse(Call call, Response response) throws IOException {
               bingPic=response.body().string();
               SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(Main2Activity.this).edit();
               editor.putString("bingPic",bingPic);
               Log.d("TAG", "onCreate: "+bingPic);
               editor.apply();
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       Glide.with(Main2Activity.this).load(bingPic).into(backgroundImg);
                   }
               });
           }
       });
    }
    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather(final String weatherId) {
        String weatherAddress = resources.getString(R.string.weatherAddress);
        String weatherKey = resources.getString(R.string.weatherKey);
        String weatherUrl = weatherAddress + weatherId + "&" + weatherKey;
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
                            editor.putString("weather"+weatherId, responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            MyUtil.showToast(Main2Activity.this, "获取天气信息失败");
                        }
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
    //根据名字获取drawable资源
    public int getDrawable(Context context, String name) {
        Assert.assertNotNull(context);
        Assert.assertNotNull(name);

        return context.getResources().getIdentifier(name,
                "drawable", context.getPackageName());
    }

    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        voiceWeather="";
        if (weather.basic.update!=null) {
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            titleUpdateTime.setText(updateTime + "更新");
        }
        if (weather.basic.cityName!=null) {
            String cityName = weather.basic.cityName;
            titleCity.setText(cityName);
        }
        voiceWeather+=titleCity.getText()+"..今日天气:";
        if (weather.now!=null) {
            if ( weather.now.tmp!=null) {
                String degree = weather.now.tmp + "℃";
                degreeText.setText(degree);
            }
            if (weather.now.cond!=null) {
                String weatherInfo = weather.now.cond.txt;
                weatherInfoText.setText(weatherInfo);
                voiceWeather+=weatherInfo+"。";
            }else{
                voiceWeather+=weatherInfoText.getText();
            }
            if (weather.now.wind!=null) {
                String windDir = weather.now.wind.dir;
                String windSc = weather.now.wind.sc;

                if(windSc.contains("-")){
                    windText.setText("风向/风力:" + windDir + "/" + windSc+"级");
                    voiceWeather+=windDir+","+windSc+"级。最高温度：";
                }else{
                    windText.setText("风向/风力:" + windDir + "/" + windSc);
                    voiceWeather+=windDir+","+windSc+"。最高温度：";
                }


            }
            if (weather.now.fl!=null) {
                String fl = weather.now.fl;
                flText.setText("体感温度:" + fl + "℃");
            }
        }
        forecastLayout.removeAllViews();
        hourlylayout.removeAllViews();
        boolean flag = true;
        //未来几小时天气

        if (weather.hourlyForecasts!=null) {
            for (HourlyForecast forecast : weather.hourlyForecasts) {
                View view = LayoutInflater.from(this).inflate(R.layout.hourly_item, hourlylayout, false);
                TextView tmpText = (TextView) view.findViewById(R.id.hour_tmpTx);
                ImageView image = (ImageView) view.findViewById(R.id.hour_imageTx);
                TextView hourText = (TextView) view.findViewById(R.id.hour_time);
                String code = "ic_" + forecast.cond.code;
                int id = getDrawable(Main2Activity.this, code);
                image.setImageResource(id);
                tmpText.setText(forecast.tmp + "℃");
                hourText.setText(forecast.date.substring(11));
                hourlylayout.addView(view);
            }
        }
        //未来几天天气
        if (weather.dailyForecasts!=null) {
            for (DailyForecast forecast : weather.dailyForecasts) {
                View view = LayoutInflater.from(this).inflate(R.layout.daily_item, forecastLayout, false);
                if (forecast.date!=null) {
                    TextView dateText = (TextView) view.findViewById(daily_dateTx);
                    dateText.setText(forecast.date.substring(5));
                }
                if (forecast.cond!=null) {
                    TextView condText = (TextView) view.findViewById(R.id.daily_condTx);
                    condText.setText(forecast.cond.txt_d);
                }
                if (forecast.pop!=null) {
                    TextView popText = (TextView) view.findViewById(R.id.daily_popTx);
                    popText.setText(forecast.pop + "%");
                }
                TextView maxText = (TextView) view.findViewById(R.id.daily_maxTx);
                if (forecast.tmp.max!=null) {

                    maxText.setText(forecast.tmp.max + "℃");
                }
                TextView minText = (TextView) view.findViewById(R.id.daily_minTx);
                if (forecast.tmp.min !=null) {

                    minText.setText(forecast.tmp.min + "℃");
                }
                forecastLayout.addView(view);
                //只获取第一天的日出日落时间
                if (forecast.astro!=null) {
                    if (flag) {
                        voiceWeather+=maxText.getText()+"，最低温度："+minText.getText();
                        String sr = null;//日出
                        if (forecast.astro.sr!=null) {
                            sr = forecast.astro.sr;
                        }
                        String ss = null;//日落
                        if (forecast.astro.ss!=null) {
                            ss = forecast.astro.ss;
                        }
                        astroText.setText("日出/日落:" + sr + "/" + ss);
                        flag = false;
                    }
                }
            }
        }
        if (weather.aqi != null) {
            aqiText.setText("空气质量:" + weather.aqi.city.aqi + "/" + weather.aqi.city.qlty);
        }
        if(weather.suggestion!=null){
            if (weather.suggestion.comf!=null) {
                String comfort = weather.suggestion.comf.txt;
                comfortText.setText(comfort);
                String title_comfort = "舒适度指数：" + weather.suggestion.comf.brf;
                title_comfortText.setText(title_comfort);
                voiceWeather+="。"+weather.suggestion.comf.txt+"。";
            }
            if (weather.suggestion.cw!=null) {
                String carWash = weather.suggestion.cw.txt;
                String title_carWash = "洗车指数：" + weather.suggestion.cw.brf;
                title_carWashText.setText(title_carWash);
                carWashText.setText(carWash);
            }
            if (weather.suggestion.sport!=null) {
                String sport = weather.suggestion.sport.txt;
                sportText.setText(sport);
                String title_sport = "运动指数：" + weather.suggestion.sport.brf;
                title_sportText.setText(title_sport);
            }
            if (weather.suggestion.drsg!=null) {
                String drsg = weather.suggestion.drsg.txt;
                designText.setText(drsg);
                String title_drsg = "穿衣指数:" + weather.suggestion.drsg.brf;
                title_designText.setText(title_drsg);
                //voiceWeather+=drsg+"。";
            }
            if (weather.suggestion.uv!=null) {
                String uv = weather.suggestion.uv.txt;
                uvText.setText(uv);
                String title_uv = "紫外线指数:" + weather.suggestion.uv.brf;
                title_uvText.setText(title_uv);
            }
            if (weather.suggestion.air!=null) {
                String air = weather.suggestion.air.txt;
                airText.setText(air);
                String title_air = "污染指数:" + weather.suggestion.air.brf;
                title_airText.setText(title_air);
                //voiceWeather+=air+"。";
            }
            if (weather.suggestion.trav!=null) {
                String trav = weather.suggestion.trav.txt;
                travelText.setText(trav);
                String title_trav = "旅行指数:" + weather.suggestion.trav.brf;
                title_travelText.setText(title_trav);
            }
            if (weather.suggestion.flu!=null) {
                String flu = weather.suggestion.flu.txt;
                fluText.setText(flu);
                String title_flu = "感冒指数:" + weather.suggestion.flu.brf;
                title_fluText.setText(title_flu);
            }
            voiceWeather+="祝您一切顺利.";
            weatherLayout.setVisibility(View.VISIBLE);
        }
//        Intent intent = new Intent(this, AutoUpdateService.class);
//        startService(intent);
    }

    //初始化变量
    public void initVar() {
        voiceWeather="";
        voiceBu=(Button)findViewById(R.id.VoiceBu);
        resources = Main2Activity.this.getResources();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuBu = (Button) findViewById(R.id.menuBu);
        gestureDetector = new GestureDetector(this, this);
        backgroundImg = (ImageView) findViewById(R.id.backgroundIm);
        weatherLayout = (ScrollView) findViewById(R.id.sv_weather_layout);
        titleCity = (TextView) findViewById(R.id.titleCity);
        titleUpdateTime = (TextView) findViewById(R.id.timeTx);
        degreeText = (TextView) findViewById(R.id.tmpTx);
        flText = (TextView) findViewById(R.id.flTx);
        windText = (TextView) findViewById(R.id.windTx);
        astroText = (TextView) findViewById(R.id.astroTx);
        hourlylayout = (LinearLayout) findViewById(R.id.hourly_layout);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);

        weatherInfoText = (TextView) findViewById(R.id.condTx);
        aqiText = (TextView) findViewById(R.id.aqiTx);
        comfortText = (TextView) findViewById(R.id.comf_sug);
        carWashText = (TextView) findViewById(R.id.cw_sug);
        sportText = (TextView) findViewById(R.id.sport_sug);
        travelText = (TextView) findViewById(R.id.trav_sug);
        uvText = (TextView) findViewById(R.id.uv_sug);
        fluText = (TextView) findViewById(R.id.flu_sug);
        airText = (TextView) findViewById(R.id.air_sug);
        designText = (TextView) findViewById(R.id.drsg_sug);
        title_comfortText = (TextView) findViewById(R.id.suggestion_comf);
        title_carWashText = (TextView) findViewById(R.id.suggestion_cw);
        title_sportText = (TextView) findViewById(R.id.suggestion_sport);
        title_travelText = (TextView) findViewById(R.id.suggestion_travel);
        title_uvText = (TextView) findViewById(R.id.suggestion_uv);
        title_fluText = (TextView) findViewById(R.id.suggestion_flu);
        title_airText = (TextView) findViewById(R.id.suggestion_air);
        title_designText = (TextView) findViewById(R.id.suggestion_drsg);

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
    public boolean dispatchTouchEvent(MotionEvent ev) {
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

    public void onBackPressed() {
        ActivityCollector.removeAll();
        //android.os.Process.killProcess(android.os.Process.myPid());
    }
    public void onPause(){
        if(tts.isSpeaking()){
            tts.stop();
            //tts.shutdown();
        }
        super.onPause();
    }
    public void onDestroy(){

        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
