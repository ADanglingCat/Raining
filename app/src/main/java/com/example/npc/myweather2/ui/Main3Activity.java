package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.npc.myweather2.R;
import com.example.npc.myweather2.model.CountyList;
import com.example.npc.myweather2.service.UpdateWeatherService;
import com.example.npc.myweather2.util.ActivityCollector;
import com.example.npc.myweather2.util.BaseActivity;
import com.example.npc.myweather2.util.MyUtil;
import com.example.npc.myweather2.util.PagerAdapter;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Main3Activity extends BaseActivity {
    private PagerAdapter pagerAdapter;
    private ViewPager mViewPager;
    private List<CountyList> countyLists;
    private List<Fragment> fragmentList;
    private Button menuBu;
    private TextView titleCounty;
    private DrawerLayout drawer;
    private ImageView backIm;
    private NavigationView navigationView;
    private String imagePath;
    private String bingPic;
    private Calendar calendar;
    private int today;
    private int yesterday;
    private SharedPreferences preferences;
    private ImageView userImage;
    private TextView userSign;
    private TextView userName;
    private View headerView;
    public static TextToSpeech tts;
    private static final String TAG = "TAGMain3Activity";
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
        setContentView(R.layout.activity_main3);
        initTTS();
        menuBu = (Button) findViewById(R.id.menuBu);
        titleCounty = (TextView) findViewById(R.id.titleCity);
        countyLists = DataSupport.findAll(CountyList.class);
        fragmentList = new ArrayList<>();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.menuNa);
        backIm = (ImageView) findViewById(R.id.backgroundIm);
        preferences = PreferenceManager.getDefaultSharedPreferences(Main3Activity.this);
        calendar = Calendar.getInstance();
        today = calendar.get(Calendar.DATE);
        yesterday = preferences.getInt("date", 0);

        headerView = navigationView.getHeaderView(0);
        userSign = (TextView) headerView.findViewById(R.id.user_sign);
        userName = (TextView) headerView.findViewById(R.id.user_name);
        userImage = (ImageView) headerView.findViewById(R.id.user_image);
        menuBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.myweather.PERSONAL");
                startActivity(intent);
                drawer.closeDrawers();
            }
        });

        for (CountyList countyList : countyLists) {
            Fragment fragment = new PagerFragment();
            Bundle arg = new Bundle();
            arg.putString("weatherId", countyList.getWeatherId());
            fragment.setArguments(arg);
            fragmentList.add(fragment);
        }
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(pagerAdapter);
        ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                titleCounty.setText(countyLists.get(position).getCountyName());
                stopTTS();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mViewPager.addOnPageChangeListener(listener);
        mViewPager.setOffscreenPageLimit(2);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.cityMe:
                        intent = new Intent(Main3Activity.this, AreaManagerActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.settingMe:
                        intent = new Intent(Main3Activity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.updateMe:
                        MyUtil.showToast(Main3Activity.this, "没有新版本");
                        break;
                    case R.id.aboutMe:
                        intent = new Intent(Main3Activity.this, AboutUsActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                drawer.closeDrawers();
                Log.d(TAG, "onNavigationItemSelected: 1111closeDrawers");
                return true;
            }
        });
        if (preferences.getBoolean("autoUpdate", true)) {
            //启动自动更新
            Intent intent = new Intent(this, UpdateWeatherService.class);
            startService(intent);
        }

    }

    public void onResume() {
        super.onResume();

        String headerPath = preferences.getString("headerPath", null);
        String sign = preferences.getString("sign", getString(R.string.my_sign));
        String name = preferences.getString("name", getString(R.string.my_name));

        int mainPosition = getMainPosition();
        titleCounty.setText(countyLists.get(mainPosition).getCountyName());
        if (countyLists.size() != fragmentList.size()) {
            fragmentList.clear();
            for (CountyList countyList : countyLists) {
                Fragment fragment1 = new PagerFragment();
                Bundle arg = new Bundle();
                arg.putString("weatherId", countyList.getWeatherId());
                fragment1.setArguments(arg);
                fragmentList.add(fragment1);
            }
            pagerAdapter.notifyDataSetChanged();
        }
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", mainPosition);
        intent.removeExtra("position");
        if (fragmentList.size() != 0) {
            mViewPager.setCurrentItem(position);
        }

        if (preferences.getBoolean("diy", false)) {
            if (preferences.getBoolean("autoBing", false)) {
                setBackgroundByBing();
            } else if (preferences.getString("imagePath", null) != null) {
                imagePath = preferences.getString("imagePath", null);
                // Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
               // Bitmap bitmap = getBitmap(imagePath);
                File file=new File(imagePath);
                Glide.with(this)
                        .load(file)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(backIm);
                //backIm.setImageBitmap(bitmap);
            } else {
                backIm.setImageResource(R.drawable.ic_background);
            }
            backIm.setImageAlpha(Integer.parseInt(preferences.getString("alpha", "255")));
        } else {
            backIm.setImageAlpha(255);
            backIm.setImageResource(R.drawable.ic_background);
        }

        if (headerPath != null) {
            File file=new File(headerPath);
            Glide.with(this)
                    .load(file)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(userImage);
            Bitmap bitmap = getBitmap(headerPath);
            //userImage.setImageBitmap(bitmap);
//            if(preferences.getBoolean("isUIChanged",false)&&!preferences.getBoolean("save",false)){
//                _User user=new _User();
//                BmobFile bmobFile=new BmobFile(new File(headerPath));
//                user.setUserImage(bmobFile);
//                user.update(BmobUser.getCurrentUser(_us))
//            }
            getColor(bitmap);

        } else {
            userImage.setImageResource(R.drawable.ic_userimage);
            headerView.setBackgroundResource(R.color.colorImage);
        }
        if (sign != null) {
            userSign.setText(sign);
        }
        if (name != null) {
            userName.setText(name);
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        stopTTS();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyTTS();
    }

    public void onBackPressed() {
        ActivityCollector.removeAll();
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    //获取默认城市
    public int getMainPosition() {
        countyLists = DataSupport.findAll(CountyList.class);
        int position = -1;
        for (int i = 0; i < countyLists.size(); i++) {
            if (countyLists.get(i).isMainCity()) {
                position = i;
                break;
            }
        }
        if (position == -1) {
            countyLists.get(0).setMainCity(true);
            position = 0;
        }
        return position;
    }

    //必应每日一图设置背景
    public void setBackgroundByBing() {
        preferences = PreferenceManager.getDefaultSharedPreferences(Main3Activity.this);
        bingPic = preferences.getString("bingPic", null);
        if (today != yesterday || bingPic == null) {
            requestBing();
        } else {
            Glide.with(Main3Activity.this).load(bingPic).into(backIm);
        }
    }

    //获取必应每日一图
    public void requestBing() {
        MyUtil.sendRequest(getResources().getString(R.string.bingPicture), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyUtil.showToast(Main3Activity.this, "获取图片失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Main3Activity.this).edit();
                editor.putString("bingPic", bingPic);
                editor.putInt("date", today);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(Main3Activity.this).load(bingPic).into(backIm);

                    }
                });
            }
        });
    }

    //图片处理
    public Bitmap getBitmap(String path) {

        Bitmap bitmap = BitmapFactory.decodeFile(path);

       // if (bitmap != null) {
            //int bheight = bitmap.getHeight();
           // int bwidth = bitmap.getWidth();
           // if (bheight > 4096 || bwidth > 4096) {

           //     bheight = (int) (bitmap.getHeight() * 0.9);
           //     bwidth = (int) (bitmap.getWidth() * 0.9);
          //  }
           // bitmap = Bitmap.createBitmap(bitmap, 0, 0, bwidth, bheight);
      //  } else {
        if(bitmap==null)
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_userimage);
      //  }
        return bitmap;

    }

    //从图片中取色
    public void getColor(Bitmap bitmap) {
        Palette.Builder builder = Palette.from(bitmap);
        builder.generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                Palette.Swatch vibrant1 = palette.getDarkVibrantSwatch();
                if (vibrant1 != null) {
                    headerView.setBackgroundColor(vibrant1.getRgb());

                } else if (vibrant != null) {
                    headerView.setBackgroundColor(vibrant.getRgb());
                } else {
                    headerView.setBackgroundResource(R.color.colorHeaderBackground);
                }
            }
        });
    }
    public void initTTS() {
        tts = new TextToSpeech(Main3Activity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                   tts.setLanguage(Locale.CHINA);
                }
            }
        });
    }
    public void stopTTS(){
        if (tts != null&& tts.isSpeaking()) {
            tts.stop();
        }
    }
    public void destroyTTS(){
        if (tts != null) {
            tts.stop();
            tts.shutdown();

        }
    }
    public static TextToSpeech getTTS(){
        if(tts!=null){
            return tts;
        }else{
            return null;
        }
    }
}